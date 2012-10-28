package r.parser;


public class ParseUtil {

    public static String hexChar(String... chars) {
        int value = 0;
        for (int i = 0; i < chars.length; i++) {
            value = value * 16 + Integer.parseInt(chars[i], 16);
        }
        return new String(new int[] {value}, 0, 1);
    }

}
