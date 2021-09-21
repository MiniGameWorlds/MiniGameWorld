package com.minigameworld.manager.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.PlayerTool;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PartyManager {
	/*
	 * [IMPORTANT]
	 * - send message with "Party.sendMessage()"
	 */

	private List<Party> parties;

	public PartyManager() {
		this.parties = new ArrayList<>();
	}

	public void createParty(Player p) {
		if (!this.hasParty(p)) {
			this.parties.add(new Party(p));
		}
	}

	public void deleteParty(Player p) {
		Party party = this.getPlayerParty(p);
		this.parties.remove(party);
	}

	private void deletePersonalParty(Player p) {
		for (Party party : this.parties) {
			if (party.hasPlayer(p) && party.getSize() == 1) {
				this.parties.remove(party);
				return;
			}
		}
	}

	private boolean hasParty(Player p) {
		// judge that player has a party if party's member are 2 or more players
		Party party = this.getPlayerParty(p);
		if (party == null) {
			return false;
		}
		return party.getSize() >= 2;
	}

	private Party getPlayerParty(Player p) {
		/*
		 * [IMPORTANT] player must always have party ( = not need to check has party ) 
		 */
		for (Party party : this.parties) {
			if (party.hasPlayer(p)) {
				return party;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public void invitePlayer(Player inviter, Player invitee) {
		// check target player is online
		if (!this.checkPlayersOnline(inviter, invitee)) {
			return;
		}

		// check invitee has alredy party
		if (this.hasParty(invitee)) {
			Party.sendMessage(inviter, invitee.getName() + " already has a party");
			return;
		}

		// invite
		Party party = this.getPlayerParty(inviter);
		if (party.invitePlayer(invitee)) {
			Party.sendMessage(inviter, "Invitation has been sent to " + invitee.getName());
			Party.sendMessage(invitee, inviter.getName() + " sent a party invitation");

			// message
			TextComponent msg = new TextComponent("Invitation from " + inviter.getName());
			msg.setColor(ChatColor.GREEN);
			msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("Click to join the party").create()));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mg party accept " + inviter.getName()));
			Party.sendMessage(invitee, msg);
		} else {
			Party.sendMessage(inviter, "You have already invited " + invitee.getName());
		}
	}

	public void acceptInvitation(Player invitee, Player inviter) {
		// check target player is online
		if (!this.checkPlayersOnline(inviter, invitee)) {
			return;
		}

		Party party = this.getPlayerParty(inviter);
		if (party.acceptInvitation(invitee)) {
			this.deletePersonalParty(invitee);
		}
	}

	public void rejectInvitation(Player inviter, Player invitee) {
		// check target player is online
		if (!this.checkPlayersOnline(inviter, invitee)) {
			return;
		}

		Party party = this.getPlayerParty(inviter);
		party.rejectInvitation(inviter, invitee);
	}

	public void ask(Player asker, Player partyMember) {
		// check target player is online
		if (!this.checkPlayersOnline(asker, partyMember)) {
			return;
		}

		if (this.hasParty(asker)) {
			Party.sendMessage(asker, "You already have a party");
			return;
		}

		Party party = this.getPlayerParty(partyMember);
		party.askToJoin(asker);

		// message
		Party.sendMessage(asker, "Send asking to " + partyMember.getName());
		Party.sendMessage(partyMember, asker.getName() + " asks you to join your party");
	}

	public void allow(Player partyMember, Player asker) {
		// check target player is online
		if (!this.checkPlayersOnline(partyMember, asker)) {
			return;
		}

		if (this.hasParty(asker)) {
			Party.sendMessage(partyMember, asker.getName() + " already has a party");
			return;
		}

		Party party = this.getPlayerParty(partyMember);
		if (party.allowToJoin(asker)) {
			this.deletePersonalParty(asker);
		} else {
			Party.sendMessage(partyMember, asker.getName() + " didn't ask or time has too passed");
		}
	}

	public void leaveParty(Player member) {
		if (this.hasParty(member)) {
			Party party = this.getPlayerParty(member);
			party.leave(member);

			// msg
			Party.sendMessage(member, "You leaved a party");

			// make a new party for member
			this.createParty(member);
		} else {
			Party.sendMessage(member, "you don't have a party to leave");
		}
	}

	public void kickVote(Player reporter, Player target) {
		// check target player is online
		if (!this.checkPlayersOnline(reporter, target)) {
			return;
		}

		Party party = this.getPlayerParty(reporter);
		if (party.kickVote(reporter, target)) {
			// make a new party for kicked member
			this.createParty(target);
		}
	}

	public void messageToEveryone(Player p, String msg) {
		// check target player is online
		if (!PlayerTool.isPlayerOnline(p)) {
			return;
		}

		Party party = this.getPlayerParty(p);
		party.sendMessageToAllMembers("<" + p.getName() + "> " + msg);
	}

	public void list(Player p) {
		// check target player is online
		if (!PlayerTool.isPlayerOnline(p)) {
			return;
		}

		Party party = this.getPlayerParty(p);
		String listMsg = "";
		for (Player member : party.getMembers()) {
			listMsg += "\n- " + member.getName();
		}
		Party.sendMessage(p, listMsg);
	}

	private boolean checkPlayersOnline(Player notifyPlayer, Player targetPlayer) {
		// check nofify player
		if (!PlayerTool.isPlayerOnline(notifyPlayer)) {
			return false;
		}

		// check target player
		if (!PlayerTool.isPlayerOnline(targetPlayer)) {
			Party.sendMessage(notifyPlayer, "That player is not online");
			return false;
		}
		return true;
	}
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
