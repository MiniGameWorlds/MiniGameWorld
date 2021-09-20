package com.minigameworld.manager.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.minigameworld.util.Utils;

public class PartyManager {
	/*
	 * - /mg party invite <player(invitee)>
	 * - /mg party accept <player(inviter)>
	 * - /mg party ask <player(member)>
	 * - /mg party allow <player<asker>)
	 * - /mg party leave
	 * - /mg party kickvote <player(member)>
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

	private boolean hasParty(Player p) {
		// judge that player has a party if party's member are 2 or more players
		Party party = this.getPlayerParty(p);
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

	public void invitePlayer(Player inviter, Player invitee) {
		// check invitee has alredy party
		if (this.hasParty(invitee)) {
			Utils.sendMsg(inviter, invitee.getName() + " already has a party");
			return;
		}

		// invite
		Party party = this.getPlayerParty(inviter);
		if (party.invitePlayer(invitee)) {
			Utils.sendMsg(inviter, "Invitation has been sent to " + invitee.getName());
		} else {
			Utils.sendMsg(inviter, "You have already invited " + invitee.getName());
		}
	}

	public void acceptInvitation(Player inviter, Player invitee) {
		Party party = this.getPlayerParty(inviter);
		party.acceptInvitation(invitee);
	}

	public void rejectInvitation(Player inviter, Player invitee) {
		Party party = this.getPlayerParty(inviter);
		party.rejectInvitation(inviter, invitee);
	}

	public void ask(Player asker, Player partyMember) {
		if (this.hasParty(asker)) {
			Utils.sendMsg(asker, "You already have a party");
			return;
		}

		Party party = this.getPlayerParty(partyMember);
		party.askToJoin(asker);

		// message
		Utils.sendMsg(asker, "Send asking to " + partyMember.getName());
		Utils.sendMsg(partyMember, asker.getName() + " asks you to join your party");
	}

	public void allow(Player partyMember, Player asker) {
		if (this.hasParty(asker)) {
			Utils.sendMsg(partyMember, asker.getName() + " already has a party");
			return;
		}

		Party party = this.getPlayerParty(partyMember);
		if (!party.allowToJoin(asker)) {
			Utils.sendMsg(partyMember, asker.getName() + " didn't ask or time has too passed");
		}
	}

	public void leaveParty(Player member) {
		if (this.hasParty(member)) {
			Party party = this.getPlayerParty(member);
			party.leave(member);

			// make a new party for member
			this.createParty(member);
		} else {
			Utils.sendMsg(member, "you don't have a party to leave");
		}
	}

	public void kickVote(Player reporter, Player target) {
		Party party = this.getPlayerParty(reporter);
		if (!party.hasPlayer(target)) {
			Utils.sendMsg(reporter, target.getName() + " is not your party");
		}

		if (party.kickVote(reporter, target)) {
			// make a new party for kicked member
			this.createParty(target);
		}
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
