/**
 * Morse code decoder
 */
public class Decoder {
    /**
     * 1st cluster - DOT
     * 2nd cluster - DASH
     * 3rd cluster - long PAUSE
     *
     * @param km
     * @param sig
     * @param pause
     * @return
     */
    private static String decodeSignal(KMeans km, String sig, String pause) {
        String res = "";
        int sigLength = sig.length();
        int pauseLength = pause.length();

        int sigClstr = km.findClusterByPoint(sigLength);
        int pauseClstr = km.findClusterByPoint(pauseLength);

        switch (sigClstr) {
            case 0:
                res += ".";
                break;
            case 1:
                res += "-";
                break;
            default:
                break;
        }

        switch (pauseClstr) {
            case 1:
                res += " ";
                break;
            case 2:
                res += "   ";
                break;
            default:
                break;
        }

        return res;
    }

    /**
     * Makes bits parsing
     * @param bitStream
     * @return
     */
    private static String decodeBitsAdvanced(String bitStream) {
        String morseCode = new String("");

        if (bitStream.isEmpty() || !bitStream.contains("1"))
            return morseCode;

        bitStream = bitStream.replaceAll("^0+", ""); // remove leading 0's
        bitStream = bitStream.replaceAll("0+$", ""); // remove trailing 0's

        KMeans km = new KMeans(bitStream);
        km.run();

        String[] ones = bitStream.split("0+");
        String[] zeros = bitStream.split("1+");

        for (int i = 0; i < ones.length - 1; i++) {
            morseCode += decodeSignal(km, ones[i], zeros[i + 1]);
        }

        morseCode += decodeSignal(km, ones[ones.length - 1], "");

        return morseCode;
    }

    /**
     * Decodes a letter + whitespace
     * @param letterCode
     * @param space
     * @return
     */
    private static String decodeLetter(String letterCode, String space) {
        String letter = "";
        letter += MorseCode.get(letterCode);
        if (space.length() == 3) {
            letter += " ";
        }

        return letter;
    }

    /**
     * Makes morse code parsing
     * @param morseCode
     * @return
     */
    private static String decodeMorse(String morseCode) {
        if (morseCode.equals(new String("")))
            return "";

        String msg = "";

        morseCode = morseCode.trim();

        String[] letters = morseCode.split(" +");   // makes an array of letters in morse code
        String[] spaces = morseCode.split("[.-]+"); // makes an array of spaces in code

        for (int i = 0; i < letters.length - 1; i++) {
            msg += decodeLetter(letters[i], spaces[i + 1]);
        }

        msg += decodeLetter(letters[letters.length - 1], "");

        return msg;
    }

    public static void main(String[] args) {
        String morseCode = decodeBitsAdvanced("0000000011011010011100000110000001111110100111110011111100000000000111011111111011111011111000000101100011111100000111110011101100000100000");
        System.out.println(morseCode);
        String msg = decodeMorse(morseCode);
        System.out.println(msg);
    }
}