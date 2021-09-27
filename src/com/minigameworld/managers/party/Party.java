package com.minigameworld.managers.party;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.util.Setting;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Party {
	private Set<PartyMember> members;
	private Set<UUID> invitees;
	private Set<UUID> askers;

	public Party(Player p) {
		this.members = new HashSet<>();
		this.members.add(new PartyMember(p));

		this.invitees = new HashSet<>();
		this.askers = new HashSet<>();
	}

	public List<Player> getMembers() {
		List<Player> players = new ArrayList<>();
		this.members.forEach(member -> players.add(member.getPlayer()));
		return players;
	}

	public int getSize() {
		return this.members.size();
	}

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

	public void rejectInvitation(Player inviter, Player invitee) {
		UUID uuid = invitee.getUniqueId();
		if (this.invitees.contains(uuid)) {
			this.invitees.remove(uuid);

			// message
			Party.sendMessage(inviter, invitee.getName() + " rejected to join your party");
			Party.sendMessage(invitee, "You rejected to join " + inviter.getName() + "'s party");
		}
	}

	public boolean hasPlayer(Player target) {
		return this.getMembers().contains(target);
	}

	private PartyMember getPartyMember(Player p) {
		for (PartyMember member : this.members) {
			if (member.getPlayer().equals(p)) {
				return member;
			}
		}
		return null;
	}

	public boolean kickVote(Player reporter, Player target) {
		if (!this.hasPlayer(target)) {
			Party.sendMessage(reporter, target.getName() + " is not your party");
			return false;
		}

		// kick vote & message
		if (this.getPartyMember(target).kickVote(reporter)) {
			PartyMember member = this.getPartyMember(target);
			this.members.remove(member);
			Party.sendMessage(reporter, "You kick voted " + target.getName());
			Party.sendMessage(target, "You are kick voted by party member");
		} else {
			Party.sendMessage(reporter, "You already kickvoted " + target.getName());
		}

		// check majority
		return this.checkKickVoteMajority(target);
	}

	public boolean checkKickVoteMajority(Player p) {
		PartyMember member = this.getPartyMember(p);
		int votedCount = member.getKickVoteCount();

		// e.g. 3 of 6, 3 of 7
		int majorityCount = this.getSize() / 2;
		boolean isMajority = votedCount > majorityCount;
		if (isMajority) {
			this.removeKickVotingByPlayer(p);
			this.members.remove(member);
			Party.sendMessage(p, "You kicked from your party");
			return true;
		}
		return false;
	}

	private void removeKickVotingByPlayer(Player p) {
		this.members.forEach(m -> m.cancelKickVote(p));
	}

	public void leave(Player p) {
		this.removeKickVotingByPlayer(p);
		PartyMember member = this.getPartyMember(p);
		this.members.remove(member);
		this.sendMessageToAllMembers(p.getName() + " leave a party");
	}

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

	public int getKickVoteCount(Player p) {
		if (this.hasPlayer(p)) {
			return this.getPartyMember(p).getKickVoteCount();
		}
		return -1;
	}

	public static void sendMessage(Player p, String msg) {
		p.sendMessage("" + ChatColor.YELLOW + ChatColor.BOLD + "[Party] " + ChatColor.WHITE + msg);
	}

	@SuppressWarnings("deprecation")
	public static void sendMessage(Player p, BaseComponent compo) {
		TextComponent msg = new TextComponent("" + ChatColor.YELLOW + ChatColor.BOLD + "[Party] " + ChatColor.WHITE);
		msg.addExtra(compo);
		p.spigot().sendMessage(msg);
	}

	public void sendMessageToAllMembers(String msg) {
		this.members.forEach(m -> Party.sendMessage(m.getPlayer(), msg));
	}

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
