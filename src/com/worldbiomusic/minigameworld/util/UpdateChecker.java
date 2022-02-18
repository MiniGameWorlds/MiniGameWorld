package com.worldbiomusic.minigameworld.util;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

/**
 * Check for updates with Github latest release tag
 *
 */
public class UpdateChecker {
	/**
	 * Check current version is latest version<br>
	 * Print update checker in the console
	 * 
	 * @return True if current version is latest version
	 */
	public static boolean check() {
		String latestReleaseTag = getLatestVersion();
		boolean isLatest = false;

		isLatest = Setting.API_VERSION.equals(latestReleaseTag);

		ChatColor currentVersionColor = isLatest ? ChatColor.GREEN : ChatColor.RED;
		ChatColor latestVersionColor = ChatColor.GREEN;

		// print update checkers
		Utils.info("                Update Checker                ");
		Utils.info(" - Current version: " + currentVersionColor + Setting.API_VERSION);
		Utils.info(" - Latest  version: " + latestVersionColor + latestReleaseTag);

		if (!isLatest) {
			Utils.warning("");
			Utils.warning("Your version is " + currentVersionColor + "outdated");
			Utils.warning("Download latest version: " + ChatColor.UNDERLINE
					+ "https://github.com/MiniGameWorlds/MiniGameWorld/releases");
		}
		Utils.info(ChatColor.GREEN + "=============================================");

		return isLatest;
	}

	/**
	 * Get latest version
	 * 
	 * @return latest version
	 */
	public static String getLatestVersion() {
		String version = "";

		try {
			// access github anonymously (not connect())
			GitHub github = GitHub.connectAnonymously();
			long repoId = 352097201;
			GHRepository repo = github.getRepositoryById(repoId);
			GHRelease release = repo.getLatestRelease();
			version = release.getTagName();
		} catch (IOException e) {
			if (Setting.DEBUG_MODE) {
				e.printStackTrace();
			}
			return null;
		}

		return version;
	}
}
