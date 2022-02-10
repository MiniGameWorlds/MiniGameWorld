package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameScoreboardUpdateEvent;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Setting;

/**
 * Scoreboard manager<br>
 * - Waiting scoreboard<br>
 * - Play scoreboard<br>
 * <br>
 * Manage only one scoreboard instance<br>
 */
public class MiniGameScoreboardManager {

	/**
	 * Current scoreboard type
	 */
	public enum ScoreboardType {
		IDLE, WAITING, PLAY
	}

	private MiniGame minigame;
	private Scoreboard scoreboard;
	private Objective sidebarObjective;

	private MiniGameScoreboardUpdater waitingScoreboardUpdater;
	private MiniGameScoreboardUpdater playScoreboardUpdater;

	public MiniGameScoreboardManager(MiniGame minigame) {
		this.minigame = minigame;

		// init scoreboard
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		// init SIDEBAR objective
		this.sidebarObjective = this.scoreboard.registerNewObjective("sidebar", "dummy", "Title");
		this.sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// register update scoreboard task
		registerScoreboardUpdateTask();
	}

	public void registerDefaultUpdaters() {
		this.waitingScoreboardUpdater = new MiniGameWaitingScoreboard(minigame);
		this.playScoreboardUpdater = new MiniGamePlayScoreboard(minigame);

	}

	/**
	 * Init scoreboard after minigame finished<br>
	 * Set to default waiting scoreboard frame
	 */
	public void setDefaultScoreboard() {
		resetAllPlayersScoreboard();
		this.waitingScoreboardUpdater.updateScoreboard();
	}

	private void registerScoreboardUpdateTask() {
		this.minigame.getTaskManager().registerTask("_update-scoreboard", () -> {
			updateScoreboard();
		});
	}

	public void startScoreboardUpdateTask() {
		// check scoreboard option is true && minigame is not empty
		if (!Setting.SCOREBOARD) {
			return;
		}

		// check minigame scoreboard setting option
		if (!this.minigame.getSetting().isScoreboardEnabled()) {
			return;
		}

		// start scoreboard update timer task
		this.minigame.getTaskManager().runTaskTimer("_update-scoreboard", 0, Setting.SCOREBOARD_UPDATE_DELAY);
	}

	private void updateScoreboard() {
		// reset scores in objective of scoreboard
		resetAllPlayersScoreboard();

		// update with current
		if (getCurrentScoreboardType() == ScoreboardType.PLAY) {
			this.playScoreboardUpdater.updateScoreboard();
		} else if (getCurrentScoreboardType() == ScoreboardType.WAITING) {
			this.waitingScoreboardUpdater.updateScoreboard();
		}

		// run hook method of MiniGame
		this.minigame.updateScoreboard();

		// call event
		Bukkit.getPluginManager().callEvent(new MiniGameScoreboardUpdateEvent(this.minigame));

		// MiniGame.removePlayer() access critical section with another thread(main)
		synchronized (this) {
			// set scoreboard to playing players & viewers
			this.minigame.getPlayers().forEach(p -> p.setScoreboard(scoreboard));
			this.minigame.getViewManager().getViewers().forEach(v -> v.setScoreboard(scoreboard));
		}
	}

	/**
	 * Change waiting scoreboard updater
	 * 
	 * @param updater Waiting updater
	 */
	public void setWaitingScoreboardUpdater(MiniGameScoreboardUpdater updater) {
		this.waitingScoreboardUpdater = updater;
	}

	/**
	 * Change play scoreboard updater
	 * 
	 * @param updater Play updater
	 */
	public void setPlayScoreboardUpdater(MiniGameScoreboardUpdater updater) {
		this.playScoreboardUpdater = updater;
	}

	public MiniGameScoreboardUpdater getWaitingScoreboardUpdater() {
		return waitingScoreboardUpdater;
	}

	public MiniGameScoreboardUpdater getPlayScoreboardUpdater() {
		return playScoreboardUpdater;
	}

	/**
	 * Get current scoreboard updater
	 * 
	 * @return Null if current scoreboard type is {@link ScoreboardType#IDLE}
	 */
	public MiniGameScoreboardUpdater getCurrentScoreboardUpdater() {
		if (getCurrentScoreboardType() == ScoreboardType.PLAY) {
			return getPlayScoreboardUpdater();
		} else if (getCurrentScoreboardType() == ScoreboardType.WAITING) {
			return getWaitingScoreboardUpdater();
		}
		return null;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public ScoreboardType getCurrentScoreboardType() {
		if (this.minigame.isEmpty()) {
			return ScoreboardType.IDLE;
		}

		return this.minigame.isStarted() ? ScoreboardType.PLAY : ScoreboardType.WAITING;
	}

	/**
	 * Remove all scores in every objective of scoreboard
	 */
	public void resetAllPlayersScoreboard() {
		this.scoreboard.getEntries().forEach(scoreboard::resetScores);
	}

}
