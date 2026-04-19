public class RiskAnalyzer {

    public int calculateRiskScore(int linesAdded, int filesChanged, int entropy) {
        int score = 0;

        if (linesAdded > 100) {
            score += 20;
        } else if (linesAdded > 50) {
            score += 10;
        }

        if (filesChanged > 5) {
            score += 15;
        } else if (filesChanged > 2) {
            score += 8;
        }

        if (entropy > 2) {
            score += 10;
        }

        for (int i = 0; i < filesChanged; i++) {
            score += i;
        }

        return score;
    }

    public String classifyRisk(int score) {
        if (score > 40) {
            return "HIGH";
        } else if (score > 20) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
