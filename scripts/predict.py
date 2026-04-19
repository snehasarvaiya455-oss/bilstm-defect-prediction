```python
#!/usr/bin/env python3
"""
predict.py
Loads Preksha172/lstm-jit-defect-prediction from Hugging Face
and runs the 3-model ensemble to predict defect probability.
"""

import argparse
import json
import os
import sys
import numpy as np

MODEL_REPO = "Preksha172/lstm-jit-defect-prediction"


def build_model(input_size=44, hidden_size=256, num_layers=3,
                dropout=0.5, bidirectional=True):
    import torch
    import torch.nn as nn

    class UltimateLSTM(nn.Module):
        def __init__(self):
            super().__init__()
            self.lstm_out_size = hidden_size * (2 if bidirectional else 1)
            self.lstm = nn.LSTM(input_size, hidden_size, num_layers,
                                batch_first=True, dropout=dropout,
                                bidirectional=bidirectional)
            self.layer_norm = nn.LayerNorm(self.lstm_out_size)
            self.attention = nn.MultiheadAttention(
                self.lstm_out_size,
                4,
                dropout=dropout,
                batch_first=True
            )
            self.batch_norm = nn.BatchNorm1d(self.lstm_out_size)
            self.dropout = nn.Dropout(dropout)
            self.fc1 = nn.Linear(self.lstm_out_size, 128)
            self.fc2 = nn.Linear(128, 64)
            self.fc3 = nn.Linear(64, 32)
            self.fc4 = nn.Linear(32, 2)
            self.leaky = nn.LeakyReLU(0.1)
            self.bn1 = nn.BatchNorm1d(128)
            self.bn2 = nn.BatchNorm1d(64)
            self.bn3 = nn.BatchNorm1d(32)

        def forward(self, x):
            out, _ = self.lstm(x)
            out = self.layer_norm(out)
            attn, _ = self.attention(out, out, out)
            out = out + attn

            avg = torch.mean(out, dim=1)
            mx = torch.max(out, dim=1)[0]
            ctx = self.batch_norm(avg + mx)

            ctx = self.dropout(self.leaky(self.fc1(ctx)))
            ctx = self.bn1(ctx)

            ctx = self.dropout(self.leaky(self.fc2(ctx)))
            ctx = self.bn2(ctx)

            ctx = self.dropout(self.leaky(self.fc3(ctx)))
            ctx = self.bn3(ctx)

            return self.fc4(ctx)

    return UltimateLSTM()


def predict(feature_vector, hf_token):
    import torch
    import joblib
    from huggingface_hub import hf_hub_download

    cfg_path = hf_hub_download(
        MODEL_REPO,
        "model_config.json",
        token=hf_token
    )

    with open(cfg_path) as f:
        cfg = json.load(f)

    input_size = cfg.get("input_size", 44)
    hidden_size = cfg.get("hidden_size", 256)
    num_layers = cfg.get("num_layers", 3)
    dropout = cfg.get("dropout", 0.5)
    bidirectional = cfg.get("bidirectional", True)
    seq_len = cfg.get("sequence_length", 12)

    scaler_path = hf_hub_download(
        MODEL_REPO,
        "scaler.pkl",
        token=hf_token
    )

    scaler = joblib.load(scaler_path)

    x_raw = np.array(feature_vector, dtype=np.float32).reshape(1, -1)
    x_scaled = scaler.transform(x_raw).astype(np.float32)

    x_tensor = torch.tensor(
        np.repeat(x_scaled, seq_len, axis=0)[np.newaxis, :, :]
    )

    weight_files = [
        "model_1_weights.pth",
        "model_2_weights.pth",
        "model_3_weights.pth",
        "best_model_weights.pth",
    ]

    probs = []

    for wf in weight_files:
        try:
            wpath = hf_hub_download(MODEL_REPO, wf, token=hf_token)

            m = build_model(
                input_size,
                hidden_size,
                num_layers,
                dropout,
                bidirectional
            )

            state = torch.load(
                wpath,
                map_location="cpu",
                weights_only=True
            )

            m.load_state_dict(state)
            m.eval()

            with torch.no_grad():
                p = torch.softmax(m(x_tensor), dim=-1)[0, 1].item()

            probs.append(p)
            print(f"  {wf}: {p:.4f}")

        except Exception as e:
            print(f"  Skipped {wf}: {e}", file=sys.stderr)

    if not probs:
        raise RuntimeError("All model weight files failed to load.")

    return float(np.mean(probs))


def advanced_risk_score(metrics):
    risk = 0

    if metrics.get("lines_added", 0) > 50:
        risk += 3

    if metrics.get("files_changed", 0) > 2:
        risk += 2

    if metrics.get("entropy_raw", 0) > 1.5:
        risk += 3

    return risk


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--metrics", required=True)
    parser.add_argument("--output", default="prediction.json")
    args = parser.parse_args()

    with open(args.metrics) as f:
        metrics = json.load(f)

    fv = metrics.get("feature_vector", [])

    if len(fv) != 44:
        fv = (fv + [0.0] * 44)[:44]

    hf_token = os.environ.get("HF_TOKEN")

    try:
        prob = predict(fv, hf_token)

    except Exception as e:
        print(f"Inference failed: {e}", file=sys.stderr)

        la = metrics.get("lines_added", 0)
        nf = metrics.get("files_changed", 0)
        ent = metrics.get("entropy_raw", 0)

        prob = min(
            0.99,
            (la / 500 * 0.4) +
            (nf / 10 * 0.3) +
            (ent / 3 * 0.3)
        )

    advanced_level = advanced_risk_score(metrics)

    if prob >= 0.55 or advanced_level >= 6:
        risk = "HIGH"
    else:
        risk = "LOW"

    result = {
        "defect_probability": round(prob, 4),
        "risk_level": risk,
        "threshold": 0.55,
        "model_repo": MODEL_REPO,
    }

    with open(args.output, "w") as f:
        json.dump(result, f, indent=2)

    print(f"Result: {risk} ({prob * 100:.1f}% defect probability)")


if __name__ == "__main__":
    main()
```
