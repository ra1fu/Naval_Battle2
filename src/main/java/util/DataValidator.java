package util;

public class DataValidator {
    public static boolean isValidUsername(String username) {
        return username != null && username.trim().length() >= 3;
    }
}
