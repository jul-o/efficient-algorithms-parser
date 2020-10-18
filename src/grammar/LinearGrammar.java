package step1;
import java.util.*;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.*;

public class LinearGrammar extends AbstractGrammar {
    // private Character[][] terminals;
    // source, destination nonterminal -> destination terminal
    private Character[][][] nonterminalTerminalRules;
    // source, destination nonterminal -> destination terminal
    private Character[][][] terminalNonterminalRules;

    public LinearGrammar(AbstractRule[] rules) {
        this.initRules(rules);
    }

    public LinearGrammar(String filePath) {
        List<AbstractRule> rules = new ArrayList<>();
        File inputFile = new File(filePath);
        try {
            Scanner inputReader = new Scanner(inputFile);
            while(inputReader.hasNextLine()) {
                String line = inputReader.nextLine();
                char leftHandSymbol = line.charAt(0);
                if (line.length() == 4) {
                    // nonterminal
                    char firstRightHandSymbol = line.charAt(2);
                    char secondRightHandSymbol = line.charAt(3);

                    if (firstRightHandSymbol >= 'A' && firstRightHandSymbol <= 'Z' && secondRightHandSymbol >= 'a' && secondRightHandSymbol <= 'z') {
                        rules.add(new NonterminalTerminalRule(leftHandSymbol, firstRightHandSymbol, secondRightHandSymbol));
                    } else if (firstRightHandSymbol >= 'a' && firstRightHandSymbol <= 'z' && secondRightHandSymbol >= 'A' && secondRightHandSymbol <= 'Z') {
                        rules.add(new TerminalNonterminalRule(leftHandSymbol, firstRightHandSymbol, secondRightHandSymbol));
                    }
                } else {
                    rules.add(new TerminalRule(leftHandSymbol, line.charAt(2)));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.initRules(rules.toArray(new AbstractRule[rules.size()]));
    }

    private void initRules(AbstractRule[] rules) {
        List<Character> nonTerminalsList = new ArrayList<>();

        // fill nonterminals list
        for(AbstractRule r : rules) {
            if(!nonTerminalsList.contains(r.getSymbol())) {
                nonTerminalsList.add(r.getSymbol());
            }
        }

        this.nonterminalNames = nonTerminalsList.toArray(new Character[nonTerminalsList.size()]);

        int nbNonterminals = nonTerminalsList.size();

        List<Character>[] terminalList = new ArrayList[nbNonterminals];
        int maxTerminalsLength = 0;
        for (int i = 0; i < terminalList.length; ++i) {
            terminalList[i] = new ArrayList<>();
        }

        List<Character>[][] terminalNonterminalList = new ArrayList[nbNonterminals][nbNonterminals];
        int maxTerminalNonterminalLength = 0;
        for (int i = 0; i < nbNonterminals; ++i) {
            for (int j = 0; j < nbNonterminals; ++j) {
                terminalNonterminalList[i][j] = new ArrayList<>();
            }
        }

        List<Character>[][] nonterminalTerminalList = new ArrayList[nbNonterminals][nbNonterminals];
        int maxNonterminalTerminalLength = 0;
        for (int i = 0; i < nbNonterminals; ++i) {
            for (int j = 0; j < nbNonterminals; ++j) {
                nonterminalTerminalList[i][j] = new ArrayList<>();
            }
        }

        // fill rules
        for (AbstractRule r : rules) {
            if (r instanceof TerminalRule) {
                TerminalRule rCasted = (TerminalRule) r;
                int index = nonTerminalsList.indexOf(rCasted.getSymbol());
                terminalList[index].add(rCasted.getDestinationSymbol());
                maxTerminalsLength = Math.max(terminalList[index].size(), maxTerminalsLength);
            } else if (r instanceof TerminalNonterminalRule) {
                TerminalNonterminalRule rCasted = (TerminalNonterminalRule) r;
                int indexSymbol = nonTerminalsList.indexOf(rCasted.getSymbol());
                int indexNonterminalDest = nonTerminalsList.indexOf(rCasted.getDestinationNonterminal());
                terminalNonterminalList[indexSymbol][indexNonterminalDest].add(rCasted.getDestinationTerminal());
                // maxTerminalNonterminalLength = Math.max(maxTerminalNonterminalLength, terminalNonterminalList[indexSymbol][indexNonterminalDest].size());
            } else if (r instanceof NonterminalTerminalRule) {
                NonterminalTerminalRule rCasted = (NonterminalTerminalRule) r;
                int indexSymbol = nonTerminalsList.indexOf(rCasted.getSymbol());
                int indexNonterminalDest = nonTerminalsList.indexOf(rCasted.getDestinationNonterminal());
                nonterminalTerminalList[indexSymbol][indexNonterminalDest].add(rCasted.getDestinationTerminal());
                // maxTerminalNonterminalLength = Math.max(maxTerminalNonterminalLength, nonterminalTerminalList[indexSymbol][indexNonterminalDest].size());
            }
        }

        this.terminals = new Character[nbNonterminals][];
        for (int i = 0; i < this.terminals.length; ++i) {
            this.terminals[i] = terminalList[i].toArray(new Character[terminalList[i].size()]);
        }

        this.nonterminalTerminalRules = new Character[nbNonterminals][nbNonterminals][];
        for (int i = 0; i < nbNonterminals; ++i) {
            for (int j = 0; j < nbNonterminals; ++j) {
                this.nonterminalTerminalRules[i][j] = nonterminalTerminalList[i][j].toArray(new Character[nonterminalTerminalList[i][j].size()]);
            }
        }

        this.terminalNonterminalRules = new Character[nbNonterminals][nbNonterminals][];
        for (int i = 0; i < nbNonterminals; ++i) {
            for (int j = 0; j < nbNonterminals; ++j) {
                this.terminalNonterminalRules[i][j] = terminalNonterminalList[i][j].toArray(new Character[terminalNonterminalList[i][j].size()]);
            }
        }

        System.out.println(Arrays.deepToString(this.terminals) + '\t' + Arrays.deepToString(this.nonterminalTerminalRules) + '\t' + Arrays.deepToString(this.terminalNonterminalRules));
    }

    public Character[][][] getNonterminalTerminalRules() {
        return this.nonterminalTerminalRules;
    }

    public Character[][][] getTerminalNonterminalRules() {
        return this.terminalNonterminalRules;
    }
}
