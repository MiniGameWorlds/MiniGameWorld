package com.minigameworld.minigameframes.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

import com.minigameworld.minigameframes.MiniGame;
import com.wbm.plugin.util.Counter;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.instance.TaskManager;

public class MiniGameTaskManager {
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
		this.waitingCounter = new Counter(this.minigame.getWaitingTime());
		this.finishCounter = new Counter(this.minigame.getTimeLimit());
	}

	public void runWaitingTask() {
		this.taskManager.runTaskTimer("_waitingTimer", 0, 20);
	}

	public void cancelWaitingTask() {
		this.taskManager.cancelTask("_waitingTimer");
	}

	public void runFinishTask() {
		this.taskManager.runTaskTimer("_finishTimer", 0, 20);
	}

	public void cancelFinishTask() {
		this.taskManager.cancelTask("_finishTimer");
	}

	public void registerBasicTasks() {
		// register waitingTimer task to taskManager
		this.taskManager.registerTask("_waitingTimer", new Runnable() {

			@Override
			public void run() {
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
				minigame.sendTitleToAllPlayers(time, "", 4, 12, 4);

				// play sound
				if (waitTime <= 3) {
					minigame.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT));
				}

				waitingCounter.removeCount(1);
			}
		});

		// register finishTimer task to taskManager
		this.taskManager.registerTask("_finishTimer", new Runnable() {

			@Override
			public void run() {
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
					minigame.sendTitleToAllPlayers("" + time, "", 4, 12, 4);

					// play sound
					minigame.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_COW_BELL));
				}

				finishCounter.removeCount(1);
			}
		});
	}

	public TaskManager getTaskManager() {
		return this.taskManager;
	}

	public int getLeftWaitingTime() {
		return this.waitingCounter.getCount();
	}

	public int getLeftFinishTime() {
		return this.finishCounter.getCount();
	}
}
