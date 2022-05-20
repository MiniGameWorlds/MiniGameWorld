package com.worldbiomusic.minigameworld.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGameCommandTabCompleter implements TabCompleter {

	private List<String> candidates;
	private MiniGameManager minigameManager;
	private DataManager dataManager;

	public MiniGameCommandTabCompleter(MiniGameManager minigameManager, DataManager dataManager) {
		this.minigameManager = minigameManager;
		this.dataManager = dataManager;
		this.candidates = new ArrayList<>();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		this.candidates.clear();
		int length = args.length;

		if (length == 3) {
			if (args[0].equals("games")) {
				addMiniGameConfigKeyCandidates();
			}
		} else if (length == 2) {
			if (args[0].equals("join") || args[0].equals("view")) {
				addMiniGameTitleCandidates();
			} else if (args[0].equals("party")) {
				addPartyCandidates();
			} else if (args[0].equals("settings")) {
				addSettingsCandidates();
			} else if (args[0].equals("games")) {
				addMiniGameClassCandidates();
			} else if (args[0].equals("reload")) {
				addBackupDataCandidates();
			}
		} else if (length == 1) {
			addLength1Candidates();
		}

		// add player names
		if (this.candidates.isEmpty()) {
			Bukkit.getOnlinePlayers().forEach(p -> candidates.add(p.getName()));
		}

		return this.candidates;
	}

	private void addLength1Candidates() {
		this.candidates.add("help");
		this.candidates.add("join");
		this.candidates.add("view");
		this.candidates.add("leave");
		this.candidates.add("list");
		this.candidates.add("menu");
		this.candidates.add("party");
		this.candidates.add("reload");
		this.candidates.add("backup");
		this.candidates.add("settings");
		this.candidates.add("games");
	}

	private void addMiniGameTitleCandidates() {
		this.minigameManager.getMiniGameList().forEach(game -> this.candidates.add(game.getTitle()));
	}

	private void addPartyCandidates() {
		this.candidates.add("invite");
		this.candidates.add("accept");
		this.candidates.add("ask");
		this.candidates.add("allow");
		this.candidates.add("leave");
		this.candidates.add("kickvote");
		this.candidates.add("msg");
		this.candidates.add("list");
	}

	private void addSettingsCandidates() {
		this.minigameManager.getSettings().keySet().forEach(key -> candidates.add(key));
	}

	private void addMiniGameClassCandidates() {
		this.minigameManager.getMiniGameList().forEach(m -> candidates.add(m.getClassName()));
	}

	private void addMiniGameConfigKeyCandidates() {
		List<MiniGame> minigames = this.minigameManager.getMiniGameList();
		if (!minigames.isEmpty()) {
			minigames.get(0).getDataManager().getData().keySet().forEach(key -> candidates.add(key));
		}
	}

	private void addBackupDataCandidates() {
		Arrays.asList(this.dataManager.getBackupDir().listFiles()).forEach(f -> candidates.add(f.getName()));
	}

}
