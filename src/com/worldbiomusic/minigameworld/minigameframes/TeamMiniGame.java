package com.worldbiomusic.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRankComparable;

/**
 * <b>[Info]</b><br>
 * - Minigame frame players cooperate with each other<br>
 * - min player count: 1 or more <br>
 * - all players have the same score <br>
 * - team util methods <br>
 * 
 * <b>[Rule]</b><br>
 * - must use "plusEveryoneScore()" or "minusEveryoneScore()" for team score
 * 
 */
public abstract class TeamMiniGame extends MiniGame {

	public TeamMiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);
	}

	/**
	 * Gets team score
	 * 
	 * @return Team score
	 */
	protected int getTeamScore() {
		return this.getScore(this.randomPlayer());
	}

	/**
	 * Plus team score
	 * 
	 * @param amount Amount to plus
	 */
	protected void plusTeamScore(int amount) {
		this.plusEveryoneScore(amount);
	}

	/**
	 * Minus team score
	 * 
	 * @param amount Amount to minus
	 */
	protected void minusTeamScore(int amount) {
		this.minusEveryoneScore(amount);
	}

	@Override
	protected void printScore() {
		BroadcastTool.sendMessage(this.getPlayers(), ChatColor.BOLD + "[Score]");

		String allPlayersName = PlayerTool.getPlayersNameString(this.getPlayers(), ",");
		BroadcastTool.sendMessage(this.getPlayers(),
				"Team(" + allPlayersName + ")" + ": " + ChatColor.GOLD + getTeamScore());
	}

	@Override
	public List<? extends MiniGameRankComparable> getRank() {
		List<MiniGameRankComparable> list = new ArrayList<>();
		list.add(new OneTeam(getPlayers(), getTeamScore()));
		return list;
	}

	@Override
	public String getType() {
		return "Team";
	}

	class OneTeam implements MiniGameRankComparable {
		List<Player> players;
		int score;

		public OneTeam(List<Player> players, int score) {
			this.players = players;
			this.score = score;
		}

		@Override
		public List<Player> getPlayers() {
			return this.players;
		}

		@Override
		public int getScore() {
			return this.score;
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
