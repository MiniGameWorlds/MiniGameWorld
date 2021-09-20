package com.minigameworld.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minigameworld.manager.MiniGameDataManager;
import com.minigameworld.manager.MiniGameManager;

public class MiniGamePartyCommand implements CommandExecutor {
	private MiniGameManager minigameM;
	private MiniGameDataManager MiniGameDataM;

	public MiniGamePartyCommand(MiniGameManager minigameM) {
		this.minigameM = minigameM;
		this.MiniGameDataM = this.minigameM.getMiniGameDataManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// only player
		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;

		try {

			// menu
			String menu = args[0];
			switch (menu) {
			case "party":
				return this.party(p, args);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private boolean party(Player p, String[] args) {
		// check player is online
		
		/*
		 * - /mg party invite <player(invitee)>
		 * - /mg party accept <player(inviter)>
		 * - /mg party ask <player(member)>
		 * - /mg party leave
		 * - /mg party kickvote <player(member)>
		 */
		
		String menu = args[1];
		switch (menu) {
		case "invite":
			return this.invite(p, args);
		case "accept":
			return this.accept(p, args);
		case "ask":
			return this.ask(p, args);
		case "leave":
			return this.leave(p, args);
		case "kickvote":
			return this.kickvote(p, args);
		}

		return true;
	}

	private boolean invite(Player p, String[] args) {
		
		return true;
	}

	private boolean accept(Player p, String[] args) {
		return true;
	}

	private boolean ask(Player p, String[] args) {
		return true;
	}

	private boolean leave(Player p, String[] args) {
		return true;
	}

	private boolean kickvote(Player p, String[] args) {
		return true;
	}
}
