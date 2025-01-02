import org.json.JSONObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Input the number of test cases
            System.out.print("Enter the number of test cases: ");
            int testCaseCount = Integer.parseInt(scanner.nextLine());

            // Process each test case
            for (int i = 1; i <= testCaseCount; i++) {
                System.out.println("Enter JSON input for Test Case " + i + ": ");
                String jsonInput = scanner.nextLine();

                // Parse the JSON input
                JSONObject testcase = new JSONObject(jsonInput);

                // Solve for the current test case
                BigInteger secret = solve(testcase);

                // Print the result for this test case
                System.out.println("Secret for Test Case " + i + ": " + secret);
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BigInteger solve(JSONObject testcase) {
        // Parse keys n and k
        JSONObject keys = testcase.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Parse and decode the roots
        TreeMap<Integer, BigInteger> points = new TreeMap<>();
        for (String key : testcase.keySet()) {
            if (!key.equals("keys")) {
                JSONObject root = testcase.getJSONObject(key);
                int x = Integer.parseInt(key);
                int base = Integer.parseInt(root.getString("base"));
                BigInteger y = new BigInteger(root.getString("value"), base);
                points.put(x, y);
            }
        }

        // Use Lagrange interpolation to find the constant term (c)
        List<Entry<Integer, BigInteger>> selectedPoints = new ArrayList<>(points.entrySet()).subList(0, k);
        return lagrangeInterpolation(selectedPoints, BigInteger.ZERO);
    }

    public static BigInteger lagrangeInterpolation(List<Entry<Integer, BigInteger>> points, BigInteger xValue) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).getKey());
            BigInteger yi = points.get(i).getValue();

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).getKey());
                    numerator = numerator.multiply(xValue.subtract(xj));
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            result = result.add(yi.multiply(numerator).divide(denominator));
        }

        return result;
    }
}
