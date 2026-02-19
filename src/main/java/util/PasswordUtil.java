package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    /** SHA-256 の 16進文字列を返す（ソルト無しの例：既存互換用） */
    public static String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(b.length * 2);
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** ハッシュ方式を自動判定して照合（BCrypt か SHA-256 か） */
    public static boolean matches(String rawPassword, String storedHash) {
        if (storedHash == null) return false;


        // それ以外は SHA-256（16進文字列）として比較（既存互換）
        String inputHash = sha256Hex(rawPassword);
        return inputHash.equalsIgnoreCase(storedHash);
    }
}
