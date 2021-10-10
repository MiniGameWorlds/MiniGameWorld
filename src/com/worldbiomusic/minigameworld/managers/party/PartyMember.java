package com.worldbiomusic.minigameworld.managers.party;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Party member which have data related with party
 *
 */
public class PartyMember {
	private Player player;

	/**
	 * Kickvote reporter list
	 */
	private Set<UUID> kickVoted;

	/**
	 * Subject
	 * 
	 * @param p Player
	 */
	public PartyMember(Player p) {
		this.player = p;
		this.kickVoted = new HashSet<>();
	}

	/**
	 * Kicks vote player
	 * 
	 * @param reporter Player who kick voted
	 * @return True if this player is kick voted
	 */
	public boolean kickVote(Player reporter) {
		UUID uuid = reporter.getUniqueId();

		if (!this.kickVoted.contains(uuid)) {
			this.kickVoted.add(uuid);

			// reporter always remove kick voting when leave a party
//			// remove timer task
//			new BukkitRunnable() {
//				@Override
//				public void run() {
//					cancelKickVote(reporter);
//				}
//			}.runTaskLater(MiniGameWorldMain.getInstance(), 20 * 60);

			return true;
		}
		return false;
	}

	/**
	 * Cancels kick vote from player
	 * 
	 * @param reporter
	 */
	public void cancelKickVote(Player reporter) {
		UUID uuid = reporter.getUniqueId();
		this.kickVoted.remove(uuid);
	}

	/**
	 * Gets kick votes count
	 * 
	 * @return Amount of kick votes
	 */
	public int getKickVotesCount() {
		return this.kickVoted.size();
	}

	/**
	 * Gets player
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return this.player;
	}
}
