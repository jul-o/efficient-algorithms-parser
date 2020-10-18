package step1;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
        Parser p = null;
        String outputPath = null;
        List < String > testInputs = new ArrayList < > ();
        Integer naiveIterations = null;
        // used to compare linear grammar algorithms
        ChomskyGrammar chomskyGrammar = null;
        LinearGrammar linearGrammar = null;

        boolean linear = false;
        boolean forceChomsky = false;
        boolean helpAsked = false;
        // if help page is asked, do no other actions
        for (String s: args) {
            if (s.equals("-h")) {
                helpAsked = true;
                System.out.println("##########################################");
                System.out.println("Help page");
                System.out.println("##########################################");
                System.out.println();
                System.out.println("List of arguments:");
                System.out.println("-h\tShow this help page");
                System.out.println("-g\tSpecify an input grammar file");
                System.out.println("-l\tSpecify that the input grammar file is linear");
                System.out.println("-c\tSpecify that the grammar should be turned into CNF");
                System.out.println("-i\tSpecify an input file containing a list of strings to be tested");
                System.out.println("-o\tSpecify an output csv file name containing the time and number of fonction calls measurements, that will be saved to the \"csv\" folder");
                System.out.println("-n\tSpecify the number of iterations the naive algorithm should be used (this option should be used to prevent enourmous execution times)");
            }
        }

        if (!helpAsked) {
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "-l":
                        linear = true;
                        break;

                        // case "-c":
                        //     forceChomsky = true;
                        //     break;

                    case "-g":
                        if (args.length >= i + 2) {
                            String grammarPath = args[i + 1];
                            AbstractGrammar g;
                            if (!linear) {
                                g = new ChomskyGrammar(grammarPath);
                            } else {
                                chomskyGrammar = ChomskyGrammar.fromLinearGrammarFile(grammarPath);
                                linearGrammar = new LinearGrammar(grammarPath);
                                // if (forceChomsky) {
                                // g = ChomskyGrammar.fromLinearGrammarFile(grammarPath);
                                // } else {
                                //     g = new LinearGrammar(grammarPath);
                                // }
                            }
                            p = new Parser(chomskyGrammar);
                        } else {
                            throw new Exception("No grammar file specified after \"-g\"");
                        }
                        break;

                    case "-i":
                        if (args.length >= i + 2) {
                            File testInputsFile = new File(args[i + 1]);
                            Scanner testInputsReader = new Scanner(testInputsFile);

                            while (testInputsReader.hasNextLine()) {
                                testInputs.add(testInputsReader.nextLine());
                            }
                        } else {
                            throw new Exception("No input strings list file specified after \"-i\"");
                        }
                        break;

                    case "-o":
                        if (args.length >= i + 2) {
                            outputPath = args[i + 1];
                        } else {
                            throw new Exception("No output file specified after \"-o\"");
                        }
                        break;

                        // case "-e":
                        //     if (args.length >= i + 2) {
                        //         switch(args[i+1]) {
                        //         case "As":
                        //             if (args.length >= i + 4) {

                        //             } else {
                        //                 throw new Exception("not enough arguments for enumeration")
                        //             }

                        //         }
                        //     } else {
                        //         throw new Exception("No input enumeration specified after \"-e\"");
                        //     }

                        // }
                    case "-n":
                        if (args.length >= i + 2) {
                            naiveIterations = Integer.valueOf(args[i + 1]);
                        } else {
                            throw new Exception("No number of naive iterations specified after \"-n\"");
                        }
                }
            }

            if (p != null) {
                if (!linear) {
                    App.test(p, testInputs, outputPath, naiveIterations);
                } else {
                    App.linearComparison(p, testInputs, outputPath, chomskyGrammar, linearGrammar);
                }
            }
        }
    }

    static void linearComparison(Parser p, List < String > testedInputs, String outputPath, ChomskyGrammar chomskyGrammar, LinearGrammar linearGrammar) {
        long startingTime;
        long endingTime;
        long[] chomskyTime = new long[testedInputs.size()];
        int[] chomskyCounter = new int[testedInputs.size()];
        long[] linearTime = new long[testedInputs.size()];
        int[] linearCounter = new int[testedInputs.size()];
        String s;
        boolean result;

        for (int i = 0; i < testedInputs.size(); ++i) {
            s = testedInputs.get(i);

            p.setGrammar(chomskyGrammar);
            startingTime = System.nanoTime();
            result = p.parseBU(s);
            endingTime = System.nanoTime();
            chomskyTime[i] = endingTime - startingTime;
            chomskyCounter[i] = p.getCounter();

            p.setGrammar(linearGrammar);
            startingTime = System.nanoTime();
            p.parseBU(s);
            endingTime = System.nanoTime();
            linearTime[i] = endingTime - startingTime;
            linearCounter[i] = p.getCounter();

            System.out.println("\"" + s + "\" is" + (result ? "" : " NOT") + " part of the grammar");
            System.out.println("Measured parsing complexity with the Chomsky grammar: " + chomskyCounter[i]);
            System.out.println("Measured parsing complexity with the linear grammar: " + linearCounter[i]);
            System.out.println("O(n^3) = " + Math.pow(s.length(), 3));
            System.out.println("O(n^2) = " + Math.pow(s.length(), 2));
            System.out.println();
        }

        if (outputPath != null) {
            String output = "Input string,String size,n^2,n^3,Chomsky,linear";
            for (int i = 0; i < testedInputs.size(); ++i) {
                output += "\n" + testedInputs.get(i) + "," + testedInputs.get(i).length() + "," + Math.pow(testedInputs.get(i).length(), 2) + "," + Math.pow(testedInputs.get(i).length(), 3)+ "," + chomskyCounter[i] + "," + linearCounter[i];
            }

            try {
                // create output dir if it doesn't exist
                new File("csv").mkdirs();
                PrintWriter prw = new PrintWriter("csv/" + outputPath);
                prw.print(output);
                prw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void test(Parser p, List < String > testedInputs, String outputPath, Integer naiveIterations) {
        int size = testedInputs.size();
        int[] stringSizes = new int[size];
        Long[] naiveCounter = new Long[size];
        long[] BUCounter = new long[size];
        long[] TDCounter = new long[size];
        Long[] naiveTimes = new Long[size];
        Long[] BUTimes = new Long[size];
        Long[] TDTimes = new Long[size];

        for (int i = 0; i < testedInputs.size(); ++i) {
            String testedInput = testedInputs.get(i);
            Long[] res = App.testInput(testedInput, p, naiveIterations == null || i < naiveIterations);
            stringSizes[i] = testedInput.length();
            naiveCounter[i] = res[0];
            BUCounter[i] = res[1];
            TDCounter[i] = res[2];
            naiveTimes[i] = res[3];
            BUTimes[i] = res[4];
            TDTimes[i] = res[5];
        }

        if (outputPath != null) {
            App.exportCSV(stringSizes, naiveCounter, BUCounter, TDCounter, naiveTimes, BUTimes, TDTimes, outputPath, testedInputs);
        }
    }

    static void testParentheses() {
        String inputFile = "src/inputs/grammars/well_balanced_parentheses";

        ChomskyGrammar g = new ChomskyGrammar(inputFile);
        Parser p = new Parser(g);

        int stepSize = 5;
        int nbSteps = 5;
        LparenPowNrparenPowN leftsRightsEnum = new LparenPowNrparenPowN(stepSize, nbSteps);
        int[] stringSizes = new int[nbSteps];
        Long[] naiveCounter = new Long[nbSteps];
        long[] BUCounter = new long[nbSteps];
        long[] TDCounter = new long[nbSteps];
        Long[] naiveTimes = new Long[nbSteps];
        Long[] BUTimes = new Long[nbSteps];
        Long[] TDTimes = new Long[nbSteps];
        for (int i = 0; i < nbSteps; ++i) {
            stringSizes[i] = stepSize * i;
        }

        int currentIndex = 0;
        while (leftsRightsEnum.hasMoreElements()) {
            String testedInput = leftsRightsEnum.nextElement();
            Long[] res = App.testInput(testedInput, p, testedInput.length() <= 20);
            naiveCounter[currentIndex] = res[0];
            BUCounter[currentIndex] = res[1];
            TDCounter[currentIndex] = res[2];
            naiveTimes[currentIndex] = res[3];
            BUTimes[currentIndex] = res[4];
            TDTimes[currentIndex++] = res[5];
        }
        // App.exportCSV(stringSizes, naiveCounter, BUCounter, TDCounter, naiveTimes, BUTimes, TDTimes, "parentheses_lefts_rights.csv");

        stepSize = 3;
        nbSteps = 3;
        LparenRparenPowN leftRightsEnum = new LparenRparenPowN(stepSize, nbSteps);
        stringSizes = new int[nbSteps];
        naiveCounter = new Long[nbSteps];
        BUCounter = new long[nbSteps];
        TDCounter = new long[nbSteps];
        naiveTimes = new Long[nbSteps];
        BUTimes = new Long[nbSteps];
        TDTimes = new Long[nbSteps];
        for (int i = 0; i < nbSteps; ++i) {
            stringSizes[i] = stepSize * i;
        }

        currentIndex = 0;
        while (leftRightsEnum.hasMoreElements()) {
            String testedInput = leftRightsEnum.nextElement();
            Long[] res = App.testInput(testedInput, p);
            naiveCounter[currentIndex] = res[0];
            BUCounter[currentIndex] = res[1];
            TDCounter[currentIndex] = res[2];
            naiveTimes[currentIndex] = res[3];
            BUTimes[currentIndex] = res[4];
            TDTimes[currentIndex++] = res[5];

            // App.testInput(')' + testedInput, p);
            // App.testInput(testedInput + '(', p);
        }
        // App.exportCSV(stringSizes, naiveCounter, BUCounter, TDCounter, naiveTimes, BUTimes, TDTimes, "parentheses_leftrights_correct.csv");
    }

    static void testStupid() {
        String inputFile = "src/inputs/grammars/stupid_grammar";
        ChomskyGrammar g = new ChomskyGrammar(inputFile);
        Parser p = new Parser(g);

        int stepSize = 3;
        int nbSteps = 20;
        As aEnum = new As(stepSize, nbSteps);
        int[] stringSizes = new int[nbSteps];
        Long[] naiveCounter = new Long[nbSteps];
        long[] BUCounter = new long[nbSteps];
        long[] TDCounter = new long[nbSteps];
        Long[] naiveTimes = new Long[nbSteps];
        Long[] BUTimes = new Long[nbSteps];
        Long[] TDTimes = new Long[nbSteps];
        for (int i = 0; i < nbSteps; ++i) {
            stringSizes[i] = stepSize * i;
        }

        int i = 0;
        while (aEnum.hasMoreElements()) {
            String testedInput = aEnum.nextElement();
            Long[] res = App.testInput(testedInput, p, false);
            naiveCounter[i] = res[0];
            BUCounter[i] = res[1];
            TDCounter[i] = res[2];
            naiveTimes[i] = res[3];
            BUTimes[i] = res[4];
            TDTimes[i++] = res[5];
        }

        // App.exportCSV(stringSizes, naiveCounter, BUCounter, TDCounter, naiveTimes, BUTimes, TDTimes, "stupid.csv");
    }

    static void exportCSV(int[] stringSizes, Long[] naiveCounter, long[] BUCounter, long[] TDCounter, Long[] naiveTimes, Long[] BUTimes, Long[] TDTimes, String name, List < String > inputs) {
        // String output = "String size," + (naiveCounter != null ? "Naive," : "") + "Bottom-up,Top-down";
        String output = "Input string,String size,O(n^3),Naive,Bottom-up,Top-down,,Naive time,Bottom-up time,Top-down time";

        for (int i = 0; i < stringSizes.length; ++i) {
            output += "\n" + inputs.get(i) + "," + stringSizes[i] + "," + Math.pow(stringSizes[i], 3) + "," + (naiveCounter != null && naiveCounter[i] != null ? naiveCounter[i] : "") + "," + BUCounter[i] + "," + TDCounter[i] + ",," + (naiveTimes[i] != null ? naiveTimes[i] : "") + "," + (BUTimes[i] != null ? BUTimes[i] : "") + "," + (TDTimes[i] != null ? TDTimes[i] : "");
        }

        try {
            // create output dir if it doesn't exist
            new File("csv").mkdirs();
            PrintWriter prw = new PrintWriter("csv/" + name);
            prw.print(output);
            prw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
       @return [ naiveCounter, BUCounter, TDCounter, naiveTime, BUTime, TDTime ]
    */
    static Long[] testInput(String testedInput, Parser p) {
        return testInput(testedInput, p, true);
    }


    /**
       @return [ naiveCounter, BUCounter, TDCounter, naiveTime, BUTime, TDTime ]
     */
    static Long[] testInput(String testedInput, Parser p, boolean parseNaive) {
        Long naiveCounter = null;
        long startingTime;
        long endingTime;
        Long naiveTime = null;
        if (parseNaive) {
            startingTime = System.nanoTime();
            p.parseNaive(testedInput);
            endingTime = System.nanoTime();
            naiveCounter = new Long(p.getCounter());

            naiveTime = endingTime - startingTime;
        }

        startingTime = System.nanoTime();
        boolean result = p.parseBU(testedInput);
        endingTime = System.nanoTime();
        int BUCounter = p.getCounter();
        long BUTime = endingTime - startingTime;

        startingTime = System.nanoTime();
        p.parseTD(testedInput);
        endingTime = System.nanoTime();
        int TDCounter = p.getCounter();
        long TDTime = endingTime - startingTime;

        System.out.println("\"" + testedInput + "\" is" + (result ? "" : " NOT") + " part of the grammar");
        if (parseNaive) {
            System.out.println("Measured parsing complexity with the naive algorithm: " + naiveCounter);
        }
        System.out.println("Measured parsing complexity with the bottom-up algorithm: " + BUCounter);
        System.out.println("Measured parsing complexity with the top-down algorithm: " + TDCounter);
        System.out.println("O(n^3) = " + Math.pow(testedInput.length(), 3));
        System.out.println("O(n^2) = " + Math.pow(testedInput.length(), 2));

        System.out.println();

        return new Long[] {
            naiveCounter,
            new Long(BUCounter),
            new Long(TDCounter),
            naiveTime,
            BUTime,
            TDTime
        };
    }
}
