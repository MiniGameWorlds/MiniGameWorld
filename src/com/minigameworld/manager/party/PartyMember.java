package com.minigameworld.manager.party;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PartyMember {
	private Player player;
	private Set<UUID> kickVoted;

	public PartyMember(Player p) {
		this.player = p;
		this.kickVoted = new HashSet<>();
	}

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

	public void cancelKickVote(Player reporter) {
		UUID uuid = reporter.getUniqueId();
		this.kickVoted.remove(uuid);
	}

	public int getKickVoteCount() {
		return this.kickVoted.size();
	}

	public Player getPlayer() {
		return this.player;
	}
}
