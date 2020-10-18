package step1;

import java.util.Arrays;

public class Parser {
    private int counter;
    private AbstractGrammar grammar;
    private char[] inputArray;
    private Boolean[][][] TDTable;

    public Parser(AbstractGrammar g) {
        this.grammar = g;
        this.counter = 0;
    }

    void initParse(String input) {
        this.counter = 0;
        this.inputArray = input.toCharArray();

    }

    public boolean parseNaive(String input) {
        this.initParse(input);

        return this.parseNaive(0, 0, this.inputArray.length);
    }

    public boolean parseNaive(int initialIndex, int i, int j) {
        if (this.grammar instanceof ChomskyGrammar) {
            ++this.counter;
            if (i == j - 1) {
                return ((ChomskyGrammar) this.grammar).isRule(initialIndex, this.inputArray[i]);
            }

            // iterate over non terminal rules
            for (int[] NTRule: ((ChomskyGrammar) this.grammar).getNonTerminalRules(initialIndex)) {
                for (int k = i + 1; k <= j - 1; ++k) {
                    if (this.parseNaive(NTRule[0], i, k) && parseNaive(NTRule[1], k, j)) {
                        return true;
                    }
                }
            }

            return false;
        }

        return false;
    }

    public boolean parseBU(String input) {
        this.initParse(input);
        int n = this.inputArray.length;
        Character[] nonterminals = this.grammar.getNonTerminals();
        boolean[][][] P = new boolean[n][n][nonterminals.length];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < nonterminals.length; ++k) {
                    P[i][j][k] = false;
                }
            }
        }

        for (int s = 0; s < n; ++s) {
            for (int v: this.grammar.getTerminalGenerators(this.inputArray[s])) {
                P[0][s][v] = true;
            }
        }

        if (this.grammar instanceof ChomskyGrammar) {
            boolean[][][] nonterminalRules = ((ChomskyGrammar) this.grammar).getNonTerminalRules();
            for (int l = 2; l <= n; ++l) {
                for (int s = 1; s <= n - l + 1; ++s) {
                    for (int p = 1; p <= l - 1; ++p) {
                        // The counter's increment should not be in the next loops since a, b, and c do not depend on the input size
                        ++this.counter;
                        for (int a = 0; a < nonterminalRules.length; ++a) {
                            for (int b = 0; b < nonterminalRules[a].length; ++b) {
                                for (int c = 0; c < nonterminalRules[a][b].length; ++c) {
                                    if (nonterminalRules[a][b][c] && P[p - 1][s - 1][b] && P[l - p - 1][s + p - 1][c]) {
                                        P[l - 1][s - 1][a] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return P[n - 1][0][0];
        } else {
            LinearGrammar castedGrammar = (LinearGrammar) this.grammar;

            for (int i = 1; i < n; ++i) {
                for (int j = 0; j < n - i; ++j) {
                    // System.out.println("i:"+i+"j:"+j);
                    int index = 0;
                    Character[][][] terminalNonterminalRules = castedGrammar.getTerminalNonterminalRules();
                    Character[][][] nonterminalTerminalRules = castedGrammar.getNonterminalTerminalRules();
                    // for each nonterminal
                    for (int a = 0; a < nonterminals.length; ++a) {
                        // for each nonterminal in P[i-1][j+1]
                        for (int b = 0; b < nonterminals.length; ++b) {
                            ++this.counter;
                            // iterate over rules of form A -> this.inputArray[j],P[i-1][j+1], add A to P[i][j]
                            if (P[i-1][j+1][b]) {
                                for(char c : terminalNonterminalRules[a][b]) {
                                    if (i == 3 && j == 0 && c == 'a') {
                                        // System.out.println(Arrays.toString(P[i-1][j+1]) + ", " + this.inputArray[i]);
                                    }
                                    if (c == this.inputArray[j]) {
                                        System.out.println(a + " -> " + c+b);
                                        P[i][j][a] = true;
                                    }
                                }
                            }
                            //iterate over rules of form A -> P[i-1][j],this.inputArray[i+j], add A to P[i][j]
                            if (P[i-1][j][b]) {
                                for (char c : nonterminalTerminalRules[a][b]) {
                                    if (c == this.inputArray[i+j]) {
                                        System.out.println(a + " -> " + b+c);
                                        P[i][j][a] = true;
                                    }
                                }
                            }
                        }
                    }

                }
            }
            // for (int i = 0; i < n; ++i) {
            //     System.out.println(Arrays.deepToString(P[i]));
            // }

            return P[this.inputArray.length - 1][0][0];
        }

        // return false;
    }

    public boolean parseTD(String input) {
        this.initParse(input);
        if (this.grammar instanceof ChomskyGrammar) {
            Character[] nonterminals = ((ChomskyGrammar) this.grammar).getNonTerminals();
            this.TDTable = new Boolean[nonterminals.length][this.inputArray.length][this.inputArray.length + 1];

            for (int i = 0; i < nonterminals.length; ++i) {
                for (int j = 0; j < this.inputArray.length; ++j) {
                    for (int k = 0; k < this.inputArray.length; ++k) {
                        this.TDTable[i][j][k] = null;
                    }
                }
            }

            return this.parseTD(0, 0, this.inputArray.length);
        }

        return false;
    }

    public boolean parseTD(int initialIndex, int i, int j) {
        if (this.grammar instanceof ChomskyGrammar) {
            if (this.TDTable[initialIndex][i][j] != null) {
                return this.TDTable[initialIndex][i][j];
            }
            // I am not sure whether I should increment the counter before or ater the test. It changes the measure in a pretty sensitive way
            ++this.counter;

            if (i == j - 1) {
                return ((ChomskyGrammar) this.grammar).isRule(initialIndex, this.inputArray[i]);
            }


            // iterate over non terminal rules
            for (int[] NTRule: ((ChomskyGrammar) this.grammar).getNonTerminalRules(initialIndex)) {
                for (int k = i + 1; k <= j - 1; ++k) {
                    if (this.parseTD(NTRule[0], i, k) && this.parseTD(NTRule[1], k, j)) {
                        this.TDTable[initialIndex][i][j] = true;
                        return true;
                    }
                }
            }

            this.TDTable[initialIndex][i][j] = false;
            return false;
        }

        return false;
    }


    public int getCounter() {
        return this.counter;
    }

    public void setGrammar(AbstractGrammar g) {
        this.grammar = g;
    }
}
