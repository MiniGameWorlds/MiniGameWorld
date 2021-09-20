package com.minigameworld.manager.party;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;

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
			}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 20 * Setting.PARTY_INVITE_TIMEOUT);

			return true;
		}

		return false;
	}

	public void acceptInvitation(Player invitee) {
		UUID uuid = invitee.getUniqueId();
		if (this.invitees.contains(uuid)) {
			this.invitees.remove(uuid);

			this.members.add(new PartyMember(invitee));

			// message
			this.sendMessageToAllMembers(invitee.getName() + " joined party");
			Utils.sendMsg(invitee, "You joined the party");
		} else {
			Utils.sendMsg(invitee, "You don't have a invitation for the party");
		}
	}

	public void rejectInvitation(Player inviter, Player invitee) {
		UUID uuid = invitee.getUniqueId();
		if (this.invitees.contains(uuid)) {
			this.invitees.remove(uuid);

			// message
			Utils.sendMsg(inviter, invitee.getName() + " rejected to join your party");
			Utils.sendMsg(invitee, "You rejected to join " + inviter.getName() + "'s party");
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
		// kick vote & message
		if (this.getPartyMember(target).kickVote(reporter)) {
			Utils.sendMsg(reporter, "You reported " + target.getName());
			Utils.sendMsg(target, "You reported by party member");
		}

		// check majority
		return this.checkKickVoteMajority(target);
	}

	public boolean checkKickVoteMajority(Player p) {
		PartyMember member = this.getPartyMember(p);
		int votedCount = member.getKickVoting();

		// e.g. 3 of 6, 3 of 7
		int majorityCount = this.getSize() / 2;
		boolean isMajority = votedCount > majorityCount;
		if (isMajority) {
			this.removeKickVotingByPlayer(p);
			this.members.remove(member);
			Utils.sendMsg(p, "You kicked from your party");
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

	public void sendMessageToAllMembers(String msg) {
		this.members.forEach(m -> Utils.sendMsg(m.getPlayer(), msg));
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
		}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 20 * Setting.PARTY_ASK_TIMEOUT);
	}

	public boolean allowToJoin(Player asker) {
		UUID uuid = asker.getUniqueId();
		if (this.askers.contains(uuid)) {
			this.askers.remove(uuid);

			this.members.add(new PartyMember(asker));

			// message
			Utils.sendMsg(asker, "You joined the party");
			this.sendMessageToAllMembers(asker.getName() + " joined party");
			return true;
		}
		return false;
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
