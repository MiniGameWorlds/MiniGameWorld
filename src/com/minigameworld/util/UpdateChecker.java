package com.minigameworld.util;

import org.bukkit.ChatColor;

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
		String latestReleaseTag = com.wbm.plugin.util.UpdateChecker.getGithubLatestReleaseVersion(352097201);
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
			Utils.warning("Download latest version: " + "https://github.com/MiniGameWorlds/MiniGameWorld/releases");
		}
		Utils.info(ChatColor.GREEN + "=============================================");

		return isLatest;
	}

}
