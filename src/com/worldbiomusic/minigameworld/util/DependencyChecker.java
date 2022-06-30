package com.worldbiomusic.minigameworld.util;

import org.bukkit.ChatColor;

import com.wbm.plugin.util.ServerTool;

public class DependencyChecker {
	private boolean allInstalled;

	public DependencyChecker() {
		this.allInstalled = true;
	}

	public boolean checkAll() {
		check("wbmMC");
		check("Multiverse-Core");
		return allInstalled;
	}

	private void check(String pluginName) {
		boolean installed = ServerTool.isPluginEnabled(pluginName);

		ChatColor color = installed ? ChatColor.GREEN : ChatColor.RED;
		String installedMsg = installed ? "Installed" : "Not installed";
		Utils.info(" - " + pluginName + ":  " + color + installedMsg);

		this.allInstalled &= installed;
	}

}
