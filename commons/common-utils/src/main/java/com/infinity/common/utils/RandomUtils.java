package com.infinity.common.utils;

import java.util.Random;

public class RandomUtils {
	private static Random rand = new Random();

	public static int randNum(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	public static int randNum(int len) {
		String result = "";
		for (int i = 0; i < len; i++) {
			result += randNum(1, 9);
		}
		return Integer.parseInt(result);
	}
}
