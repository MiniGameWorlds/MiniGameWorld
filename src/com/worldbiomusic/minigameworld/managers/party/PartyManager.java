package com.worldbiomusic.minigameworld.managers.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * [Party System]<br>
 * - All member have equal permission (i.e. leader not exist)<br>
 * - No relation in the minigame<br>
 * <br>
 * 
 * [IMPORTANT]<br>
 * - Player always has party even alone<br>
 * - send message with {@link Party#sendMessage}<br>
 *
 */
public class PartyManager {

	private MiniGameManager miniGameManager;
	private List<Party> parties;

	public PartyManager(MiniGameManager miniGameManager) {
		this.miniGameManager = miniGameManager;
		this.parties = new ArrayList<>();
	}

	/**
	 * Creates party to player join(only need to execute when player join the
	 * server)
	 * 
	 * @param p Joined player
	 */
	public void createParty(Player p) {
		if (!this.hasParty(p)) {
			this.parties.add(new Party(p));
		}
	}

	/**
	 * Delets player party (only need to execute when player quit party or join
	 * other party)
	 * 
	 * @param p Party to delete of player
	 */
	public void deleteParty(Player p) {
		Party party = this.getPlayerParty(p);
		this.parties.remove(party);
	}

	/**
	 * Deletes personal party<br>
	 * Use this after player joined another player when solo party
	 * 
	 * @param p Player to delete party
	 */
	private void deletePersonalParty(Player p) {
		for (Party party : this.parties) {
			if (party.hasPlayer(p) && party.getSize() == 1) {
				this.parties.remove(party);
				return;
			}
		}
	}

	/**
	 * Check player has a party
	 * 
	 * @param p Player to check
	 * @return True if player has party
	 */
	public boolean hasParty(Player p) {
		// judge that player has a party if party's member are 2 or more players
		Party party = this.getPlayerParty(p);
		if (party == null) {
			return false;
		}
		return party.getSize() >= 2;
	}

	/**
	 * Gets player's party
	 * 
	 * @param p Target player
	 * @return Null if player doesn't have party, or return player's party instance
	 */
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

	/**
	 * Invites player to own party
	 * 
	 * @param inviter Player who sent invitation
	 * @param invitee Player to send invitation
	 */
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
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minigame party accept " + inviter.getName()));
			Party.sendMessage(invitee, msg);
		} else {
			Party.sendMessage(inviter, "You have already invited " + invitee.getName());
		}
	}

	/**
	 * Accepts invitation
	 * 
	 * @param invitee Player who get invitation from inviter
	 * @param inviter Player who sent invitation
	 */
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

	/**
	 * Rejects invitation
	 * 
	 * @param inviter Player who sent invitation
	 * @param invitee Player who get invitation from inviter
	 */
	public void rejectInvitation(Player inviter, Player invitee) {
		// check target player is online
		if (!this.checkPlayersOnline(inviter, invitee)) {
			return;
		}

		Party party = this.getPlayerParty(inviter);
		party.rejectInvitation(inviter, invitee);
	}

	/**
	 * Asks to join player's party
	 * 
	 * @param asker       Player who wants join party
	 * @param partyMember Player to ask
	 */
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
		msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minigame party allow " + asker.getName()));
		Party.sendMessage(partyMember, msg);
	}

	/**
	 * Allows asker can join own party
	 * 
	 * @param partyMember Player permit ask
	 * @param asker       Player to join party
	 */
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

	/**
	 * Leaves from party
	 * 
	 * @param member Player to leave
	 */
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

	/**
	 * Votes player to kick
	 * 
	 * @param reporter Player who kickvoted
	 * @param target   kickvoted player
	 */
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

	/**
	 * Send message to party members
	 * 
	 * @param p   Target player
	 * @param msg Message to send
	 */
	public void sendMessageToPlayerPartyMembers(Player p, String msg) {
		Party party = this.getPlayerParty(p);
		party.sendMessageToAllMembers(msg);
	}

	/**
	 * Print member list
	 * 
	 * @param p Target Player
	 */
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
			int kickVotingCount = party.getKickVotesCount(member);
			TextComponent playerList = new TextComponent(
					"\n- " + ChatColor.GREEN + ChatColor.UNDERLINE + member.getName() + ChatColor.WHITE);
			playerList.addExtra(" [kickvoted: " + ChatColor.RED + kickVotingCount + ChatColor.WHITE + "]");
			playerList.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder("Click to kickvote " + ChatColor.BOLD + member.getName()).create()));
			playerList.setClickEvent(
					new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minigame party kickvote " + member.getName()));

			msg.addExtra(playerList);
		}
		Party.sendMessage(p, msg);
	}

	/**
	 * Gets player's party member list
	 * 
	 * @param p Target player
	 * @return Null if player is offline, or return Member list
	 */
	public List<Player> getMembers(Player p) {
		// check target player is online
		if (!PlayerTool.isPlayerOnline(p)) {
			return null;
		}

		Party party = this.getPlayerParty(p);
		return party.getMembers();
	}

	/**
	 * Checks players are online each other
	 * 
	 * @param notifyPlayer Player to be notified
	 * @param targetPlayer Player to check online
	 * @return True if two players are online, or false
	 */
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

	/**
	 * Check party members can join minigame
	 * 
	 * @param p    Target player
	 * @param game MiniGame
	 * @return True if can join, or false
	 */
	public boolean canPartyJoin(Player p, MiniGame game) {
		if (!this.hasParty(p)) {
			return true;
		}

		List<Player> members = this.getMembers(p);
		int nonPlayingGameMemberCount = this.miniGameManager.getNonPlayingPlayerCount(members);
		int leftSeats = game.getMaxPlayerCount() - game.getPlayerCount();

		// check party size
		if (nonPlayingGameMemberCount > leftSeats) {
			Utils.sendMsg(p, "Party members are too many to join the game");
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
