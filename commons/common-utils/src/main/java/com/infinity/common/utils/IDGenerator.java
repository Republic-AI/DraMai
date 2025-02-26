package com.infinity.common.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class IDGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateID(int ID_LENGTH) {
        StringBuilder id = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }
        return id.toString();
    }

    public static String genId() {
        LocalDateTime time = LocalDateTime.now();
        String id = time.getYear() % 100
                + getNum(time.getHour())
                + generateID(2)
                + getNum(time.getMonthValue())
                + generateID(2)
                + getNum(time.getDayOfMonth())
                + generateID(2);
        return id;
    }

    private static String getNum(int number) {
        if (number < 10) {
            return "0" + number;
        }
        return String.valueOf(number);
    }
}
