package com.minigameworld.managers.party;

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

	public boolean hasParty(Player p) {
		// judge that player has a party if party's member are 2 or more players
		Party party = this.getPlayerParty(p);
		if (party == null) {
			return false;
		}
		return party.getSize() >= 2;
	}

	public Party getPlayerParty(Player p) {
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

		// check same player
		if (inviter.equals(invitee)) {
			Party.sendMessage(inviter, "You can't invite yourself");
			return;
		}

		// invite
		Party party = this.getPlayerParty(inviter);
		if (party.invitePlayer(invitee)) {
			Party.sendMessage(inviter, "Invitation has been sent to " + invitee.getName());

			// clickable chat
			TextComponent msg = new TextComponent("Invitation from " + inviter.getName());
			msg.setColor(ChatColor.GREEN);
			msg.setUnderlined(true);
			msg.setBold(true);
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

	@SuppressWarnings("deprecation")
	public void ask(Player asker, Player partyMember) {
		// check target player is online
		if (!this.checkPlayersOnline(asker, partyMember)) {
			return;
		}

		if (this.hasParty(asker)) {
			Party.sendMessage(asker, "You already have a party");
			return;
		}

		// check same player
		if (asker.equals(partyMember)) {
			Party.sendMessage(asker, "You can't ask yourself");
			return;
		}

		Party party = this.getPlayerParty(partyMember);
		party.askToJoin(asker);

		// message
		Party.sendMessage(asker, "Send asking to " + partyMember.getName());

		// clickable chat
		TextComponent msg = new TextComponent("Asking from " + asker.getName());
		msg.setColor(ChatColor.GREEN);
		msg.setBold(true);
		msg.setUnderlined(true);
		msg.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to allow to join").create()));
		msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mg party allow " + asker.getName()));
		Party.sendMessage(partyMember, msg);
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

		// check same Player
		if (reporter.equals(target)) {
			Party.sendMessage(reporter, "You can't kickvote yourself");
			return;
		}

		Party party = this.getPlayerParty(reporter);
		if (party.kickVote(reporter, target)) {
			// make a new party for kicked member
			this.createParty(target);
		}
	}

	public void sendMessageToPlayerPartyMembers(Player p, String msg) {
		Party party = this.getPlayerParty(p);
		party.sendMessageToAllMembers(msg);
	}

	@SuppressWarnings("deprecation")
	public void printList(Player p) {
		// check target player is online
		if (!PlayerTool.isPlayerOnline(p)) {
			return;
		}

		Party party = this.getPlayerParty(p);

		// clickable chat
		TextComponent msg = new TextComponent("");
		for (Player member : party.getMembers()) {
			int kickVotingCount = party.getKickVoteCount(member);
			TextComponent playerList = new TextComponent(
					"\n- " + ChatColor.GREEN + ChatColor.UNDERLINE + member.getName() + ChatColor.WHITE);
			playerList.addExtra(" [kickvoted: " + ChatColor.RED + kickVotingCount + ChatColor.WHITE + "]");
			playerList.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("Click to kickvote " + ChatColor.BOLD + member.getName()).create()));
			playerList.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mg party kickvote " + member.getName()));

			msg.addExtra(playerList);
		}
		Party.sendMessage(p, msg);
	}

	public List<Player> getMembers(Player p) {
		// check target player is online
		if (!PlayerTool.isPlayerOnline(p)) {
			return null;
		}

		Party party = this.getPlayerParty(p);
		return party.getMembers();
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
