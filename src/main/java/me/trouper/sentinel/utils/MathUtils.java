package me.trouper.sentinel.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class MathUtils {

    public static double[] uuidToDoubles(UUID uuid) {
        byte[] bytes = uuidToBytes(uuid);
        BigInteger bigInt = new BigInteger(1, bytes);

        // Split into 43, 43, 42 bits
        BigInteger mask43 = BigInteger.ONE.shiftLeft(43).subtract(BigInteger.ONE);
        BigInteger mask42 = BigInteger.ONE.shiftLeft(42).subtract(BigInteger.ONE);

        BigInteger part1 = bigInt.shiftRight(85).and(mask43);
        BigInteger part2 = bigInt.shiftRight(42).and(mask43);
        BigInteger part3 = bigInt.and(mask42);

        return new double[] {
                part1.doubleValue(),
                part2.doubleValue(),
                part3.doubleValue()
        };
    }

    public static UUID doublesToUuid(double[] doubles) {
        if (doubles.length != 3) {
            throw new IllegalArgumentException("Exactly 3 doubles required");
        }

        BigInteger part1 = BigInteger.valueOf((long) doubles[0]);
        BigInteger part2 = BigInteger.valueOf((long) doubles[1]);
        BigInteger part3 = BigInteger.valueOf((long) doubles[2]);

        BigInteger reconstructed = part1.shiftLeft(85)
                .or(part2.shiftLeft(42))
                .or(part3);

        byte[] bytes = reconstructed.toByteArray();
        
        byte[] uuidBytes = new byte[16];
        if (bytes.length > 16) {
            System.arraycopy(bytes, bytes.length - 16, uuidBytes, 0, 16);
        } else {
            System.arraycopy(bytes, 0, uuidBytes, 16 - bytes.length, bytes.length);
        }

        return bytesToUuid(uuidBytes);
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private static UUID bytesToUuid(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("Invalid UUID byte array");
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static double calcSim(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) {
            return 100.0;
        }

        int distance = calculateLevenshteinDistance(s1, s2);
        double similarity = ((double) (maxLength - distance) / maxLength) * 100.0;

        return similarity;
    }

    public static int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
