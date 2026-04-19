#!/usr/bin/env python3
"""
extract_metrics.py
Extracts JIT metrics from a PR git diff and writes metrics.json.
"""

import argparse
import json
import math
import os
import subprocess
from collections import Counter


def git(*args):
    result = subprocess.run(
        ["git", *args],
        capture_output=True,
        text=True,
        check=True
    )
    return result.stdout.strip()


def get_diff_stats(base, head):
    stat_out = git("diff", "--numstat", base, head)
    lines_added = 0
    lines_deleted = 0
    files_changed = 0

    for line in stat_out.splitlines():
        parts = line.split("\t")

        if len(parts) < 3:
            continue

        try:
            lines_added += int(parts[0]) if parts[0] != "-" else 0
            lines_deleted += int(parts[1]) if parts[1] != "-" else 0
            files_changed += 1
        except ValueError:
            continue

    return lines_added, lines_deleted, files_changed


def get_changed_files(base, head):
    out = git("diff", "--name-only", base, head)
    return [f for f in out.splitlines() if f]


def compute_entropy(files):
    if not files:
        return 0.0

    subsystems = [
        f.split("/")[0] if "/" in f else "root"
        for f in files
    ]

    counts = Counter(subsystems)
    total = sum(counts.values())

    return round(
        -sum((c / total) * math.log2(c / total) for c in counts.values()),
        4
    )


def get_developer_experience(head):
    try:
        author = git("log", "-1", "--pretty=format:%ae", head)
        lines = git("log", "--oneline", "--author", author, head, "--")
        return max(0, len(lines.splitlines()) - 1)
    except Exception:
        return 0


def safe_log(x):
    return math.log1p(max(0, x))


def build_feature_vector(la, ld, nf, ns, nd, ent, exp, lt):
    raw = [
        la, ld, nf, ns, nd, ent, exp, lt, nf,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ]

    log_f = [
        safe_log(la),
        safe_log(ld),
        safe_log(nf),
        safe_log(ns),
        safe_log(nd),
        safe_log(ent),
        safe_log(exp),
        safe_log(lt),
        safe_log(nf),
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    ]

    return raw + log_f


def calculate_complexity_score(lines_added, files_changed):
    score = 0

    if lines_added > 100:
        score += 20
    elif lines_added > 50:
        score += 10
    else:
        score += 5

    if files_changed > 5:
        score += 15
    elif files_changed > 2:
        score += 8

    for i in range(files_changed):
        score += i

    return score


def classify_change_type(score):
    if score > 30:
        return "major"
    elif score > 15:
        return "medium"
    return "minor"


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", required=True)
    parser.add_argument("--head", required=True)
    parser.add_argument("--output", default="metrics.json")
    args = parser.parse_args()

    la, ld, nf = get_diff_stats(args.base, args.head)
    files = get_changed_files(args.base, args.head)
    ent_raw = compute_entropy(files)
    dev_exp = get_developer_experience(args.head)

    ns = len({
        f.split("/")[0] if "/" in f else "root"
        for f in files
    })

    nd = len({
        os.path.dirname(f)
        for f in files
    })

    lt = 0

    for f in files[:20]:
        try:
            wc = subprocess.run(
                ["wc", "-l", f],
                capture_output=True,
                text=True
            )
            lt += int(wc.stdout.split()[0])
        except Exception:
            pass

    complexity_score = calculate_complexity_score(la, nf)
    change_type = classify_change_type(complexity_score)

    entropy_label = (
        "HIGH" if ent_raw >= 2.0
        else "MEDIUM" if ent_raw >= 0.5
        else "LOW"
    )

    metrics = {
        "lines_added": la,
        "lines_deleted": ld,
        "files_changed": nf,
        "entropy_label": entropy_label,
        "developer_experience": dev_exp,
        "entropy_raw": ent_raw,
        "subsystems": ns,
        "directories": nd,
        "total_lines_in_files": lt,
        "complexity_score": complexity_score,
        "change_type": change_type,
        "feature_vector": build_feature_vector(
            la,
            ld,
            nf,
            ns,
            nd,
            ent_raw,
            dev_exp,
            lt
        ),
    }

    with open(args.output, "w") as f:
        json.dump(metrics, f, indent=2)

    print(
        f"Metrics extracted — "
        f"LA={la} LD={ld} NF={nf} "
        f"Entropy={entropy_label} "
        f"Complexity={complexity_score} "
        f"Type={change_type} "
        f"DevExp={dev_exp}"
    )


if __name__ == "__main__":
    main()
