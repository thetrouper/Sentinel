package io.github.thetrouper.sentinel.server.util;

public class GPTUtils {
    // I'd be surprised if anyone knew how tf this shi works, I just asked GPT to write it.
    public static double calculateSimilarity(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i][j - 1], dp[i - 1][j]));
                }
            }
        }

        int maxLen = Math.max(len1, len2);
        int distance = dp[len1][len2];

        double similarity = ((double) (maxLen - distance) / maxLen) * 100;
        return similarity;
    }
}
