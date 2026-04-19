import java.util.ArrayList;
import java.util.List;

public class TestSimulation {

    public List<String> simulateResults(int iterations) {
        List<String> results = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            if (i % 5 == 0) {
                results.add("HIGH");
            } else if (i % 2 == 0) {
                results.add("MEDIUM");
            } else {
                results.add("LOW");
            }
        }

        return results;
    }

    public int countHighRisk(List<String> results) {
        int count = 0;

        for (String result : results) {
            if ("HIGH".equals(result)) {
                count++;
            }
        }

        return count;
    }
}
