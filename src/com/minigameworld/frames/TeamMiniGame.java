package com.minigameworld.frames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.minigameworld.frames.helpers.MiniGameRank;
import com.minigameworld.frames.helpers.scoreboard.MiniGameScoreboardSidebarUpdater;
import com.wbm.plugin.util.PlayerTool;

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

	public TeamMiniGame(String title, int minPlayers, int maxPlayers, int playTime, int waitingTime) {
		super(title, minPlayers, maxPlayers, playTime, waitingTime);

		// Even one player can play game
		setting().setGameFinishConditionPlayerCount(1);

		// set custom team scoreboard updater
		scoreboardManager().setPlayScoreboardUpdater(new TeamMiniGameScoreboardUpdater(this));
	}

	/**
	 * Gets team score
	 * 
	 * @return Team score
	 */
	protected int getTeamScore() {
		return this.score(this.randomPlayer());
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
	protected void printScores() {
		String allPlayersName = PlayerTool.getPlayersNameString(this.players(), ",");
		players().forEach(p -> {
			p.sendMessage(ChatColor.BOLD + "[" + this.messenger.getMsg(p, "score") + "]");
			sendMessage(p, this.messenger.getMsg(p, "team") + "(" + allPlayersName + ")" + ": " + ChatColor.GOLD
					+ getTeamScore(), false);
		});
	}

	@Override
	public List<? extends MiniGameRank> rank() {
		List<MiniGameRank> list = new ArrayList<>();
		if (playerCount() > 0) {
			list.add(new OneTeam(players(), getTeamScore()));
		}
		return list;
	}

	@Override
	public String frameType() {
		return "Team";
	}

	class OneTeam implements MiniGameRank {
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
		String leftTimeStr = "Time left: " + ChatColor.RED + ChatColor.BOLD + minigame.leftPlayTime();
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
