package step1;

class TerminalRule extends AbstractRule {
    private char destinationSymbol;

    public TerminalRule(char symbol, char destination) {
        this.setSymbol(symbol);
        this.destinationSymbol = destination;
    }

    public char getDestinationSymbol() {
        return this.destinationSymbol;
    }
}
