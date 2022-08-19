package com.minigameworld.frames.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

import com.minigameworld.frames.MiniGame;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.instance.Counter;
import com.wbm.plugin.util.instance.TaskManager;

public class MiniGameTaskManager {
	public static final String WAITING_TIMER_NAME = "_waiting-timer";
	public static final String PLAY_TIMER_NAME = "_play-timer";

	// task manager
	private TaskManager taskManager;
	// timer counter
	private Counter waitingCounter, finishCounter;

	private MiniGame minigame;

	public MiniGameTaskManager(MiniGame minigame) {
		this.minigame = minigame;
		this.taskManager = new TaskManager();
	}

	public void init() {
		// stop all tasks
		this.taskManager.cancelAllTasks();

		// timer counter
		this.waitingCounter = new Counter(this.minigame.waitingTime() + 1);
		this.finishCounter = new Counter(this.minigame.playTime() + 1);
	}

	public void runWaitingTask() {
		// check timer is set to -1 (infinite)
		if (this.minigame.waitingTime() == -1) {
			return;
		}

		this.waitingCounter = new Counter(this.minigame.waitingTime() + 1);
		this.taskManager.runTaskTimer(WAITING_TIMER_NAME, 0, 20);
	}

	public void cancelWaitingTask() {
		this.taskManager.cancelTask(WAITING_TIMER_NAME);
	}

	public void runFinishTask() {
		// check timer is set to -1 (infinite)
		if (this.minigame.playTime() == -1) {
			return;
		}

		this.finishCounter = new Counter(this.minigame.playTime() + 1);
		this.taskManager.runTaskTimer(PLAY_TIMER_NAME, 0, 20);
	}

	public void cancelFinishTask() {
		this.taskManager.cancelTask(PLAY_TIMER_NAME);
	}

	public void registerBasicTasks() {
		// register waitingTimer task to taskManager
		this.taskManager.registerTask(WAITING_TIMER_NAME, new Runnable() {

			@Override
			public void run() {
				waitingCounter.removeCount(1);

				int waitTime = waitingCounter.getCount();

				// end count down
				if (waitTime <= 0) {
					minigame.startGame();
					return;
				}

				// count down title
				String time = "" + waitTime;
				if (waitTime == 3) {
					time = ChatColor.YELLOW + time;
				} else if (waitTime == 2) {
					time = ChatColor.GOLD + time;
				} else if (waitTime == 1) {
					time = ChatColor.RED + time;
				}
				minigame.sendTitles(time, "", 4, 12, 4);

				// play sound
				if (waitTime <= 3) {
					minigame.players().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT));
				}
			}
		});

		// register play timer task to taskManager
		this.taskManager.registerTask(PLAY_TIMER_NAME, new Runnable() {

			@Override
			public void run() {
				finishCounter.removeCount(1);

				int leftTime = finishCounter.getCount();
				if (leftTime <= 0) {
					minigame.finishGame();
					return;
				} else if (leftTime <= 10) {
					// title 3, 2, 1
					String time = "" + leftTime;
					if (leftTime == 3) {
						time = ChatColor.YELLOW + time;
					} else if (leftTime == 2) {
						time = ChatColor.GOLD + time;
					} else if (leftTime == 1) {
						time = ChatColor.RED + time;
					}
					minigame.sendTitles(time, "", 4, 12, 4);

					// play sound
					minigame.players().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_COW_BELL));
				}
			}
		});
	}

	public TaskManager getTaskManager() {
		return this.taskManager;
	}

	public int getLeftWaitingTime() {
		return this.waitingCounter.getCount();
	}

	public int getLeftPlayTime() {
		return this.finishCounter.getCount();
	}
}
