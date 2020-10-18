package step1;
class NonterminalTerminalRule extends LinearRule {
    public NonterminalTerminalRule(char symbol, char destinationNonterminal, char destinationTerminal) {
        super(symbol, destinationTerminal, destinationNonterminal);
    }
}
