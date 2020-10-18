package step1;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import step1.NonterminalRule;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class ChomskyGrammar extends AbstractGrammar {
    // source, first destination symbol, second destination symbol -> true if the rule exists
    private boolean[][][] nonterminals;

    public ChomskyGrammar(AbstractRule[] rules) {
        this.initRules(rules);
    }

    // TODO: check that the rules are correct
    public ChomskyGrammar(String filePath) {
        // Count number of rules and init the rules array
        int nbRules = 0;
        File inputFile = new File(filePath);
        try {
            Scanner inputReader = new Scanner(inputFile);
            while(inputReader.hasNextLine()) {
                inputReader.nextLine();
                nbRules++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AbstractRule[] rules = new AbstractRule[nbRules];
        int currentIndex = 0;
        // Parse the file and fill the rules array
        try {
            Scanner inputReader = new Scanner(inputFile);
            while (inputReader.hasNextLine()) {
                String line = inputReader.nextLine();
                char leftHandSymbol = line.charAt(0);
                if(line.length() == 4) {
                    // nonterminal
                    rules[currentIndex++] = new NonterminalRule(leftHandSymbol, new char[] { line.charAt(2), line.charAt(3)});
                } else {
                    rules[currentIndex++] = new TerminalRule(leftHandSymbol, line.charAt(2));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.initRules(rules);
    }

    public static ChomskyGrammar fromLinearGrammarFile(String filePath) {
        List<AbstractRule> rules = new ArrayList<>();
        char currentNonUsedNonterminal = 0;
        try {
            File inputFile = new File(filePath);
            Scanner inputReader = new Scanner(inputFile);
            while (inputReader.hasNextLine()) {
                String line = inputReader.nextLine();
                char leftHandSymbol = line.charAt(0);
                System.out.println("Line: " + line);
                if(line.length() == 4) {
                    // nonterminal
                    char firstRightHandSymbol = line.charAt(2);
                    char secondRightHandSymbol = line.charAt(3);
                    if (firstRightHandSymbol >= 'A' && firstRightHandSymbol <= 'Z' && secondRightHandSymbol >= 'a' && secondRightHandSymbol <= 'z') {
                        // A -> Ba, becomes A -> BC, C -> a
                        System.out.println(leftHandSymbol + " -> " + firstRightHandSymbol + secondRightHandSymbol);
                        rules.add(new NonterminalRule(leftHandSymbol, new char[] { firstRightHandSymbol, currentNonUsedNonterminal }));
                        rules.add(new TerminalRule(currentNonUsedNonterminal++, secondRightHandSymbol));
                    } else if (firstRightHandSymbol >= 'a' && firstRightHandSymbol <= 'z' && secondRightHandSymbol >= 'A' && secondRightHandSymbol <= 'Z') {
                        System.out.println(leftHandSymbol + " -> " + firstRightHandSymbol + secondRightHandSymbol);
                        // A -> aB, becomes A -> CB, C -> a
                        rules.add(new NonterminalRule(leftHandSymbol, new char[] { currentNonUsedNonterminal, secondRightHandSymbol }));
                        rules.add(new TerminalRule(currentNonUsedNonterminal++, firstRightHandSymbol));
                    }
                    // rules[currentIndex++] = new NonterminalRule(leftHandSymbol, new char[] { line.charAt(2), line.charAt(3)});
                } else {
                    System.out.println(leftHandSymbol + " -> " + line.charAt(2));
                    // A -> a
                    rules.add(new TerminalRule(leftHandSymbol, line.charAt(2)));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new ChomskyGrammar(rules.toArray(new AbstractRule[rules.size()]));
    }

    private void initRules(AbstractRule[] rules) {
        List<Character> nonTerminals = new ArrayList<>();

        // fill nonTerminals with non terminal symbols
        for(AbstractRule r : rules) {
            if (!nonTerminals.contains(r.getSymbol())) {
                nonTerminals.add(r.getSymbol());
            }

            if(r.getClass() == NonterminalRule.class) {
                for(char symbol : ((NonterminalRule)r).getDestinationSymbols()) {
                    if(!nonTerminals.contains(symbol)) {
                        nonTerminals.add(symbol);
                    }
                }
            }
        }

        this.nonterminalNames = nonTerminals.toArray(new Character[nonTerminals.size()]);

        int nonTerminalsSize = nonTerminals.size();
        int[] nbsTerminals = new int[nonTerminalsSize];
        Arrays.fill(nbsTerminals, 0);
        this.nonterminals = new boolean[nonTerminalsSize][nonTerminalsSize][nonTerminalsSize];
        for (int i = 0; i < rules.length; ++i) {
            AbstractRule r = rules[i];
            if(r.getClass() == NonterminalRule.class) {
                NonterminalRule NTRule = (NonterminalRule) r;
                int index = nonTerminals.indexOf(NTRule.getSymbol());
                this.nonterminals[index][nonTerminals.indexOf(NTRule.getDestinationSymbols()[0])][nonTerminals.indexOf(NTRule.getDestinationSymbols()[1])] = true;
            } else {
                int index = nonTerminals.indexOf(r.getSymbol());
                nbsTerminals[index]++;
            }
        }

        this.terminals = new Character[nonTerminalsSize][];
        int[] indexes = new int[nonTerminalsSize];
        Arrays.fill(indexes, 0);
        // fill terminals
        for (int i = 0; i < rules.length; ++i) {
            if(rules[i].getClass() == TerminalRule.class) {
                TerminalRule r = (TerminalRule) rules[i];
                int index = nonTerminals.indexOf(r.getSymbol());
                if (indexes[index] == 0) {
                    this.terminals[index] = new Character[nbsTerminals[index]];
                }

                this.terminals[index][indexes[index]++] = r.getDestinationSymbol();
            }
        }

        System.out.println("Generated terminals from each nonterminal: " + Arrays.deepToString(this.terminals));


        this.printMatrixes();
    }

    public void printMatrixes() {
        for(int i = 0; i <  this.nonterminals.length; ++i) {
            boolean[][] matrix = this.nonterminals[i];
            System.out.println(this.nonterminalNames[i]);

            for(boolean[] line : matrix) {
                for(boolean value : line) {
                    System.out.print(value ? 1 : 0);
                }
                System.out.println();
            }
            System.out.println("---------------------");
        }
    }

    public boolean isRule(int leftSymbolIndex, char generatedTerminal) {
        if(this.terminals[leftSymbolIndex] == null) {
            return false;
        }
        for (char c : this.terminals[leftSymbolIndex]) {
            if (c == generatedTerminal) {
                return true;
            }
        }

        return false;
    }

    /**
       @param generatorIndex the left hand of the nonterminal rules returned
       @return [[int, int]] the array of nonterminal pairs generated by generatorSymbol
    */
    public int[][] getNonTerminalRules(int generatorIndex) {
        int nbRules = 0;
        for (boolean[] line : this.nonterminals[generatorIndex]) {
            for (boolean value : line) {
                if (value) { ++nbRules; }
            }
        }

        int[][] res = new int [nbRules][2];
        int currentResIndex = 0;
        for (int i = 0; i < this.nonterminals[generatorIndex].length; ++i) {
            for (int j = 0; j < this.nonterminals[generatorIndex][i].length; ++j) {
                if (this.nonterminals[generatorIndex][i][j]) {
                    res[currentResIndex++] = new int[] { i, j };
                }
            }
        }

        return res;
    }

    public boolean[][][] getNonTerminalRules() {
        return this.nonterminals;
    }

}
