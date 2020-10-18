package step1;
class NonterminalRule extends AbstractRule {
    private char[] destinationSymbols;
    public NonterminalRule(char symbol, char[] destinationSymbols) {
        this.setSymbol(symbol);
        this.destinationSymbols = destinationSymbols;
    }


    public char[] getDestinationSymbols() {
        return this.destinationSymbols;
    }
}
