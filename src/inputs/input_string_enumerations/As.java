package step1;

import java.util.Enumeration;

public class As extends AbstractInputStringEnumeration implements Enumeration<String> {
    private int stepsSize;
    private int remainingSteps;
    private String currentString;

    public As(int stepsSize, int nbSteps) {
        this.stepsSize = stepsSize;
        this.remainingSteps = nbSteps;
        this.currentString = "";
    }

    public String nextElement() {
        for (int i = 0; i < stepsSize; ++i) {
            this.currentString = this.currentString + 'a';
        }

        remainingSteps--;
        return this.currentString;
    }

    public boolean hasMoreElements() {
        return remainingSteps > 0;
    }

    public int getCurrentSize() {
        return this.currentString.length();
    }
}
