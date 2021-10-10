package com.worldbiomusic.minigameworld.util;

import java.util.regex.Pattern;

public class VersionChecker {
	/*
	 * Version format: <Major>.<Minor>.<Patch>
	 * <Major>: fixed 
	 * <Minor>: API changed
	 * <Patch>: add functions, bug/error fixed, 
	 * 
	 */
	public enum Different {
		MAJOR, MINOR, PATCH;
	}

	public static Different getDifferent(String v1, String v2) {
		if (isValid(v1) && isValid(v2)) {
			String[] v1Versions = splitWithDot(v1);
			String[] v2Versions = splitWithDot(v2);

			if (!v1Versions[0].equals(v2Versions[0])) {
				return Different.MAJOR;
			} else if (!v1Versions[1].equals(v2Versions[1])) {
				return Different.MINOR;
			} else { // if (!v1Versions[2].equals(v2Versions[2])) {
				return Different.PATCH;
			}
		}
		return null;
	}

	private static boolean isValid(String v) {
		try {
			// "1.0.0";
			String[] versions = splitWithDot(v);

			if (versions.length != 3) {
				System.out.println(v + " is not valid version " + versions.length);
				return false;
			}

			for (String version : versions) {
				Integer.parseInt(version);
			}
		} catch (Exception e) {
			System.out.println(v + " is not valid version(exception)");
			return false;
		}
		return true;
	}

	private static String[] splitWithDot(String str) {
		return str.split(Pattern.quote("."), -1);
	}
}
