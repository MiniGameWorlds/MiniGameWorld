package com.minigameworld.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;

public class MiniGameCommandTabCompleter implements TabCompleter {

	private List<String> candidates;
	private MiniGameManager minigameManager;

	public MiniGameCommandTabCompleter(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.candidates = new ArrayList<>();
	}

	private void setLength1Candidates() {
		this.candidates.add("join");
		this.candidates.add("leave");
		this.candidates.add("list");
		this.candidates.add("gui");
		this.candidates.add("reload");
	}

	private void setJoinCandidates() {
		for (MiniGame minigame : this.minigameManager.getMiniGameList()) {
			String title = minigame.getTitle();
			this.candidates.add(title);
		}
	}

	private void setPartyCandidates() {
		this.candidates.add("invite <player>");
		this.candidates.add("accept");
		this.candidates.add("ask");
		this.candidates.add("allow");
		this.candidates.add("leave");
		this.candidates.add("kickvote");
		this.candidates.add("msg");
		this.candidates.add("list");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		this.candidates.clear();
		int length = args.length;

		if (length == 1) {
			this.setLength1Candidates();
		} else if (length == 2) {
			if (args[0].equals("join")) {
				this.setJoinCandidates();
			} else if (args[0].equals("party")) {
				this.setPartyCandidates();
			}
		}
		return this.candidates;
	}

}
