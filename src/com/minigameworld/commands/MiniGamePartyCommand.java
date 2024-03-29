package com.minigameworld.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minigameworld.managers.party.PartyManager;

public class MiniGamePartyCommand {
	private PartyManager partyManager;

	public MiniGamePartyCommand(PartyManager partyManager) {
		this.partyManager = partyManager;
	}

	public boolean party(CommandSender sender, String[] args) throws Exception {
		/*
		 * - /mg party invite <player(invitee)>
		 * - /mg party accept <player(inviter)>
		 * - /mg party ask <player(member)>
		 * - /mg party allow <player<asker>)
		 * - /mg party leave
		 * - /mg party kickvote <player(member)>
		 * - /mg party msg <message>
		 * - /mg party list
		 */

		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return true;
		}
		Player p = (Player) sender;

		String menu = args[1];
		switch (menu) {
		case "invite":
			return this.invite(p, args);
		case "accept":
			return this.accept(p, args);
		case "ask":
			return this.ask(p, args);
		case "allow":
			return this.allow(p, args);
		case "leave":
			return this.leave(p, args);
		case "kickvote":
			return this.kickvote(p, args);
		case "msg":
			return this.msg(p, args);
		case "list":
			return this.list(p, args);
		}

		return true;
	}

	private boolean invite(Player p, String[] args) throws Exception {
		String inviteePlayerName = args[2];
		Player invitee = Bukkit.getPlayer(inviteePlayerName);
		this.partyManager.invitePlayer(p, invitee);
		return true;
	}

	private boolean accept(Player p, String[] args) throws Exception {
		String inviterPlayerName = args[2];
		Player inviter = Bukkit.getPlayer(inviterPlayerName);
		this.partyManager.acceptInvitation(p, inviter);
		return true;
	}

	private boolean ask(Player p, String[] args) throws Exception {
		String memberPlayerName = args[2];
		Player member = Bukkit.getPlayer(memberPlayerName);
		this.partyManager.ask(p, member);
		return true;
	}

	private boolean allow(Player p, String[] args) throws Exception {
		String askerName = args[2];
		Player asker = Bukkit.getPlayer(askerName);
		this.partyManager.allow(p, asker);
		return true;
	}

	private boolean leave(Player p, String[] args) throws Exception {
		this.partyManager.leaveParty(p);
		return true;
	}

	private boolean kickvote(Player p, String[] args) throws Exception {
		String targetPlayerName = args[2];
		Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
		this.partyManager.kickVote(p, targetPlayer);
		return true;
	}

	private boolean msg(Player p, String[] args) throws Exception {
		String msg = "";
		for (int i = 2; i < args.length; i++) {
			msg += args[i];
			if (i < args.length - 1) {
				msg += " ";
			}
		}
		this.partyManager.sendMessageToPlayerParty(p, p.getName() + ": " + msg);
		return true;
	}

	private boolean list(Player p, String[] args) throws Exception {
		this.partyManager.printList(p);
		return true;
	}

}
