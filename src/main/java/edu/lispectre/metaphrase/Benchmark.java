package edu.lispectre.metaphrase;


import java.math.BigDecimal;
import java.util.Random;

public class Benchmark {
    public static void startBenchmark(final int numOfEquations, final boolean verbose) {
        long timeElapsed = 0;
        Tokenizer tokenizer = new Tokenizer();
        for (int i = 0; i < numOfEquations; i++) {
            String randomEquation = generateRandomEquation(0);
            if (verbose) {
                System.out.printf("%s%n", randomEquation);
            }
            BigDecimal result;
            long start = System.nanoTime();
            try {
                tokenizer.tokenizeEquation(randomEquation);
                result = Parser.parseTokens(tokenizer.getTokens(), tokenizer.getMathContext()).eval();
            } catch (ArithmeticException ex) {
                timeElapsed += System.nanoTime() - start;
                if (verbose) {
                    System.out.println("Stopped evaluation - division by zero.");
                }
                continue;
            }
            timeElapsed += System.nanoTime() - start;
            if (verbose) {
                System.out.printf("= %s%n", result);
            }
        }
        double convertedToSeconds = (double) timeElapsed / 1_000_000_000;
        System.out.printf("Evaluated %d equations in %f seconds. (%fs/equation)%n", numOfEquations, convertedToSeconds, convertedToSeconds / numOfEquations);
    }


    private static String generateRandomEquation(int depth) {
        if (depth > 3) {
            return String.valueOf(new Random().nextInt(10) + 1);
        }

        StringBuilder equation = new StringBuilder();
        Random random = new Random();
        int numTerms = random.nextInt(3) + 2;

        for (int i = 0; i < numTerms; i++) {
            if (i > 0) {
                char operator = getRandomOperator();
                equation.append(" ").append(operator).append(" ");
            }

            if (random.nextBoolean()) {
                equation.append(random.nextInt(10) + 1);
            } else {
                equation.append("(").append(generateRandomEquation(depth + 1)).append(")");
            }
        }

        return equation.toString();
    }

    private static char getRandomOperator() {
        char[] operators = {'+', '-', '*', '/'};
        // If you don't mind a lot of -Infinities, Infinities and NaNs, uncomment the line below.
        //operators = new char[]{'+', '-', '*', '/', '^'};
        Random random = new Random();
        return operators[random.nextInt(operators.length)];
    }
}
