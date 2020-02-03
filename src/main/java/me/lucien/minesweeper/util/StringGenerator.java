package me.lucien.minesweeper.util;

import java.util.*;

public class StringGenerator {

    private static int[] nums = new int[62];

    static {
        for (int i = 0; i < 10; i++) {
            nums[i] =  i + 48;
        }

        for (int i = 0; i < 26; i++) {
            nums[i + 10] = i + 65;
            nums[i + 36] = i + 97;
        }
    }

    public static String generateRandomString() {
        int targetLength = 32;
        Random random = new Random();

        String generated = random.ints(0, 62)
                .map(i -> nums[i])
                .limit(targetLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generated;
    }
}
