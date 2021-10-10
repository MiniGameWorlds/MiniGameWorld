package com.worldbiomusic.minigameworld.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGameCommandTabCompleter implements TabCompleter {

	private List<String> candidates;
	private MiniGameManager minigameManager;

	public MiniGameCommandTabCompleter(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.candidates = new ArrayList<>();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		// /mg settings <key> <value>
		// /mg minigames <classname> <key> <value>
		this.candidates.clear();
		int length = args.length;

		if (length == 1) {
			this.setLength1Candidates();
		} else if (length == 2) {
			if (args[0].equals("join")) {
				this.setJoinCandidates();
			} else if (args[0].equals("party")) {
				this.setPartyCandidates();
			} else if (args[0].equals("settings")) {
				this.setSettingsCandidates();
			} else if (args[0].equals("minigames")) {
				this.setMiniGamesCandidates();
			}
		} else if (length == 3) {
			if (args[0].equals("minigames")) {
				this.setMiniGamesKeyCandidates();
			}
		}
		
		// add player names
		if (this.candidates.isEmpty()) {
			Bukkit.getOnlinePlayers().forEach(p -> candidates.add(p.getName()));
		}
		return this.candidates;
	}

	private void setLength1Candidates() {
		this.candidates.add("join");
		this.candidates.add("leave");
		this.candidates.add("list");
		this.candidates.add("menu");
		this.candidates.add("party");
		this.candidates.add("reload");
		this.candidates.add("settings");
		this.candidates.add("minigames");
	}

	private void setJoinCandidates() {
		for (MiniGame minigame : this.minigameManager.getMiniGameList()) {
			String title = minigame.getTitle();
			this.candidates.add(title);
		}
	}

	private void setPartyCandidates() {
		this.candidates.add("invite");
		this.candidates.add("accept");
		this.candidates.add("ask");
		this.candidates.add("allow");
		this.candidates.add("leave");
		this.candidates.add("kickvote");
		this.candidates.add("msg");
		this.candidates.add("list");
	}

	private void setSettingsCandidates() {
		this.minigameManager.getSettings().keySet().forEach(key -> candidates.add(key));
	}

	private void setMiniGamesCandidates() {
		this.minigameManager.getMiniGameList().forEach(m -> candidates.add(m.getClassName()));
	}

	private void setMiniGamesKeyCandidates() {
		this.minigameManager.getMiniGameList().get(0).getDataManager().getData().keySet()
				.forEach(key -> candidates.add(key));
	}

}
