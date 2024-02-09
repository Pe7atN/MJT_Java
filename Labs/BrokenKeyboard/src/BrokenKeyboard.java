public class BrokenKeyboard {
    public static int calculateFullyTypedWords(String message, String brokenKeys) {
        int count = 0;
        String[] words = message.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (words[i].isBlank())
                continue;

            char[] currentLetters = words[i].toCharArray();

            boolean isFound = false;
            for (int j = 0; j < currentLetters.length; j++) {
                if (brokenKeys.contains(Character.toString(currentLetters[j]))) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound)
                count++;
        }
        return count;
    }

}
