package com.worldbiomusic.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRankResult;
import com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard.MiniGameScoreboardSidebarUpdater;

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

		// Even one player can play game
		getSetting().setGameFinishConditionPlayerCount(1);

		// set custom team scoreboard updater
		getScoreboardManager().setPlayScoreboardUpdater(new TeamMiniGameScoreboardUpdater(this));
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
	public List<? extends MiniGameRankResult> getRank() {
		List<MiniGameRankResult> list = new ArrayList<>();
		if (getPlayerCount() > 0) {
			list.add(new OneTeam(getPlayers(), getTeamScore()));
		}
		return list;
	}

	@Override
	public String getFrameType() {
		return "Team";
	}

	class OneTeam implements MiniGameRankResult {
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

class TeamMiniGameScoreboardUpdater extends MiniGameScoreboardSidebarUpdater {

	public TeamMiniGameScoreboardUpdater(MiniGame minigame) {
		super(minigame);
	}

	@Override
	public void updateScoreboard() {
		super.updateScoreboard();

		Objective sidebarObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		// team score
		TeamMiniGame game = (TeamMiniGame) this.minigame;
		String teamScoreStr = "Team Score: " + ChatColor.GOLD + ChatColor.BOLD + game.getTeamScore();
		Score teamScore = sidebarObjective.getScore(teamScoreStr);
		teamScore.setScore(sidebarScoreLine--);

		// empty line
		addEmptyLineToSiderbar();

		// left time
		String leftTimeStr = "Time left: " + ChatColor.RED + ChatColor.BOLD + minigame.getLeftFinishTime();
		Score leftTime = sidebarObjective.getScore(leftTimeStr);
		leftTime.setScore(this.sidebarScoreLine--);
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
