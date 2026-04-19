# BiLSTM Defect Prediction

AI-powered Just-In-Time defect prediction using BiLSTM with Multi-Head Attention.

Every Pull Request is automatically analyzed by the model and receives a risk comment.

## Model

**[Preksha172/lstm-jit-defect-prediction](https://huggingface.co/Preksha172/lstm-jit-defect-prediction)**

- Architecture: BiLSTM + Multi-Head Attention ensemble (3 models)
- Trained on: JIT-Defects4J (16,374 commits from 21 Java projects)
- F1-Score: 38.19% | AUC-ROC: 0.834

## How it works

1. A Pull Request is opened
2. GitHub Actions extracts 44 JIT metrics from the git diff
3. The BiLSTM model predicts defect probability
4. A risk comment is posted automatically on the PR

## Project structure

```
src/
  Main.java          — entry point
  Calculator.java    — basic arithmetic operations
  UserManager.java   — user management utilities
scripts/
  extract_metrics.py — extracts JIT metrics from git diff
  predict.py         — runs BiLSTM inference
.github/workflows/
  defect_prediction.yml — GitHub Actions CI/CD pipeline
```
