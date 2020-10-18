package step1;

public class Utils {
    // O(n)
    public static boolean arrayContains(Object[] array, Object o) {
        for (Object item : array) {
            if (item.equals(o)) return true;
        }

        return false;
    }

    public static boolean arrayContains(char[] array, char o) {
        for (char item : array) {
            if(item == o) return true;
        }

        return false;
    }
}
