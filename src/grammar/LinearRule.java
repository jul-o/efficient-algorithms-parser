package step1;

abstract class LinearRule extends AbstractRule {
    private char destinationTerminal;
    private char destinationNonterminal;

    public LinearRule(char symbol, char destinationTerminal, char destinationNonterminal) {
        this.setSymbol(symbol);
        this.destinationTerminal = destinationTerminal;
        this.destinationNonterminal = destinationNonterminal;
    }

    public char getDestinationNonterminal() {
        return this.destinationNonterminal;
    }

    public char getDestinationTerminal() {
        return this.destinationTerminal;
    }
}
