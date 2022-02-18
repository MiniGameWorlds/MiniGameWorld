package com.worldbiomusic.minigameworld.managers.party;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Setting;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Party which manage members, invitees, askers
 *
 */
public class Party {
	private Set<PartyMember> members;
	private Set<UUID> invitees;
	private Set<UUID> askers;

	/**
	 * Make player's party
	 * 
	 * @param p Participant
	 */
	public Party(Player p) {
		this.members = new HashSet<>();
		this.members.add(new PartyMember(p));

		this.invitees = new HashSet<>();
		this.askers = new HashSet<>();
	}

	/**
	 * Gets member list
	 * 
	 * @return Player list
	 */
	public List<Player> getMembers() {
		List<Player> players = new ArrayList<>();
		this.members.forEach(member -> players.add(member.getPlayer()));
		return players;
	}

	/**
	 * Gets member list
	 * 
	 * @param p Target player
	 * @return PartyMember list
	 */
	public PartyMember getPartyMember(Player p) {
		for (PartyMember member : this.members) {
			if (member.getPlayer().equals(p)) {
				return member;
			}
		}
		return null;
	}

	/**
	 * Gets size of party members
	 * 
	 * @return
	 */
	public int getSize() {
		return this.members.size();
	}

	/**
	 * Invites player to this party
	 * 
	 * @param invitee Player to invite
	 * @return True if invitation sended to invitee
	 */
	public boolean invitePlayer(Player invitee) {
		UUID uuid = invitee.getUniqueId();
		if (!this.invitees.contains(uuid)) {
			this.invitees.add(uuid);

			// remove invitee timer task
			new BukkitRunnable() {
				@Override
				public void run() {
					invitees.remove(uuid);
				}
			}.runTaskLater(MiniGameWorldMain.getInstance(), 20 * Setting.PARTY_INVITE_TIMEOUT);

			return true;
		}

		return false;
	}

	/**
	 * Accept invitation
	 * 
	 * @param invitee Player who accept
	 * @return True if party have invitation for invitee
	 */
	public boolean acceptInvitation(Player invitee) {
		UUID uuid = invitee.getUniqueId();
		if (this.invitees.contains(uuid)) {
			this.invitees.remove(uuid);

			this.members.add(new PartyMember(invitee));

			// message
			this.sendMessageToAllMembers(invitee.getName() + " joined party");
			return true;
		} else {
			Party.sendMessage(invitee, "You don't have a invitation for the party");
			return false;
		}
	}

	/**
	 * Rejects invitation
	 * 
	 * @param inviter Player who invited invitee
	 * @param invitee Player who rejects
	 */
	public void rejectInvitation(Player inviter, Player invitee) {
		UUID uuid = invitee.getUniqueId();
		if (this.invitees.contains(uuid)) {
			this.invitees.remove(uuid);

			// message
			Party.sendMessage(inviter, invitee.getName() + " rejected to join your party");
			Party.sendMessage(invitee, "You rejected to join " + inviter.getName() + "'s party");
		}
	}

	/**
	 * Check party has the player
	 * 
	 * @param target Target player
	 * @return True if party has player
	 */
	public boolean hasPlayer(Player target) {
		return this.getMembers().contains(target);
	}

	/**
	 * Votes player to kick
	 * 
	 * @param reporter Player who kickvoted
	 * @param target   Kickvoted player
	 * @return True if target player is kicked from party
	 */
	public boolean kickVote(Player reporter, Player target) {
		if (!this.hasPlayer(target)) {
			Party.sendMessage(reporter, target.getName() + " is not your party");
			return false;
		}

		// kick vote & message
		if (this.getPartyMember(target).kickVote(reporter)) {
			Party.sendMessage(reporter, "You kick voted " + target.getName());
			Party.sendMessage(target, "You are kick voted by party member");
		} else {
			Party.sendMessage(reporter, "You already kickvoted " + target.getName());
		}

		// check majority
		return this.checkKickVoteMajority(target);
	}

	/**
	 * Checks kick vote count majority
	 * 
	 * @param p Checked player
	 * @return True if majoritys, or not
	 */
	public boolean checkKickVoteMajority(Player p) {
		PartyMember member = this.getPartyMember(p);
		int votedCount = member.getKickVotesCount();

		// e.g. 3 of 6, 3 of 7
		int majorityCount = this.getSize() / 2;
		boolean isMajority = votedCount > majorityCount;
		if (isMajority) {
			this.removeKickVotesByPlayer(p);
			this.members.remove(member);
			Party.sendMessage(p, "You kicked from your party");
			return true;
		}
		return false;
	}

	/**
	 * Removes kick votes by a player
	 * 
	 * @param p Player who kick voted
	 */
	private void removeKickVotesByPlayer(Player p) {
		this.members.forEach(m -> m.cancelKickVote(p));
	}

	/**
	 * Leaves party
	 * 
	 * @param p Player to leave
	 */
	public void leave(Player p) {
		this.removeKickVotesByPlayer(p);
		PartyMember member = this.getPartyMember(p);
		this.members.remove(member);
		this.sendMessageToAllMembers(p.getName() + " leave a party");
	}

	/**
	 * Asks party to join
	 * 
	 * @param asker Player who asked
	 */
	public void askToJoin(Player asker) {
		UUID uuid = asker.getUniqueId();
		this.askers.add(uuid);

		// remove 1 min later
		new BukkitRunnable() {
			@Override
			public void run() {
				askers.remove(uuid);
			}
		}.runTaskLater(MiniGameWorldMain.getInstance(), 20 * Setting.PARTY_ASK_TIMEOUT);
	}

	/**
	 * Allows asker to join the party
	 * 
	 * @param asker Player who asked
	 * @return True if asker joined the party
	 */
	public boolean allowToJoin(Player asker) {
		UUID uuid = asker.getUniqueId();
		if (this.askers.contains(uuid)) {
			this.askers.remove(uuid);

			this.members.add(new PartyMember(asker));

			// message
			Party.sendMessage(asker, "You joined the party");
			this.sendMessageToAllMembers(asker.getName() + " joined party");
			return true;
		}
		return false;
	}

	/**
	 * Gets kick votes count of player
	 * 
	 * @param p Checking player
	 * @return Amount of kick votes
	 */
	public int getKickVotesCount(Player p) {
		if (this.hasPlayer(p)) {
			return this.getPartyMember(p).getKickVotesCount();
		}
		return -1;
	}
	
	/**
	 * Check party members can join minigame
	 * 
	 * @param game MiniGame
	 * @return True if can join, or false
	 */
	public boolean canJoinMiniGame(MiniGame game) {
		List<Player> notInMiniGameMembers = MiniGameWorldUtils.getNotInMiniGamePlayers(getMembers());
		
		// check party size
		int leftSeats = game.getMaxPlayerCount() - game.getPlayerCount();
		if (notInMiniGameMembers.size() > leftSeats) {
			return false;
		}

		return true;
	}

	/**
	 * Sends message to member
	 * 
	 * @param p   Audience
	 * @param msg Message to send
	 */
	public static void sendMessage(Player p, String msg) {
		p.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + "[Party] " + ChatColor.RESET + msg);
	}

	/**
	 * Sends message with Component
	 * 
	 * @param p     Audience
	 * @param compo Component to send
	 */
	public static void sendMessage(Player p, BaseComponent compo) {
		TextComponent msg = new TextComponent("" + ChatColor.YELLOW + ChatColor.BOLD + "[Party] " + ChatColor.RESET);
		msg.addExtra(compo);
		p.spigot().sendMessage(msg);
	}

	/**
	 * Sends message to all members
	 * 
	 * @param msg Message to send
	 */
	public void sendMessageToAllMembers(String msg) {
		this.members.forEach(m -> Party.sendMessage(m.getPlayer(), msg));
	}

	/**
	 * Sends message to all members with Component
	 * 
	 * @param compo Component to send
	 */
	public void sendMessageToAllMembers(BaseComponent compo) {
		this.members.forEach(m -> Party.sendMessage(m.getPlayer(), compo));
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
//
//
//
