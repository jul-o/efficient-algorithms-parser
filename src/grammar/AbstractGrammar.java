package step1;

public abstract class AbstractGrammar {
    protected Character[] nonterminalNames;
    // source, ? -> destination terminal symbol
    protected Character[][] terminals;

    public Character[] getNonTerminals() {
        return this.nonterminalNames;
    }

    public int[] getTerminalGenerators(char terminal) {
        int[] res;
        int nbGenerators = 0;
        for (int i = 0; i < this.terminals.length; ++i) {
            if (this.terminals[i] != null && Utils.arrayContains(this.terminals[i], terminal)) {
                ++nbGenerators;
            }
        }

        res = new int[nbGenerators];

        int currentIndex = 0;
        for (int sourceIndex = 0; sourceIndex < this.terminals.length; ++sourceIndex) {
            if(this.terminals[sourceIndex] != null && Utils.arrayContains(this.terminals[sourceIndex], terminal)) {
                res[currentIndex++] = sourceIndex;
            }
        }

        return res;
    }
}
