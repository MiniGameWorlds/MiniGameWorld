package com.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.manager.playerdata.MiniGamePlayerDataManager;
import com.minigameworld.observer.MiniGameEventNotifier;
import com.minigameworld.observer.MiniGameObserver;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.Counter;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.SortTool;
import com.wbm.plugin.util.instance.BukkitTaskManager;

public abstract class MiniGame implements MiniGameEventNotifier {
	/*
	 * MiniGame Class
	 */

	// Info
	private MiniGameSetting setting;

	// check game has started
	private boolean started;

	// timer counter
	private Counter waitingCounter, finishCounter;

	// <player, score>
	private Map<Player, Integer> players;

	// task manager
	private BukkitTaskManager taskManager;

	// observer list
	private List<MiniGameObserver> observerList;

	// player data manager
	private MiniGamePlayerDataManager playerDataManager;

	// abstract methods
	protected abstract void initGameSettings();

	protected abstract void processEvent(Event event);

	protected abstract List<String> registerTutorial();

	// base constructor
	protected MiniGame(String title, Location location, int minPlayerCount, int maxPlayerCount, int timeLimit,
			int waitingTime) {
		this.setting = new MiniGameSetting(title, location, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);

		// [must setup once]
		this.setupMiniGame();

		// minigame setup
		this.initMiniGame();
	}

	// base location constructor
	protected MiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime) {
		this(title, new Location(Bukkit.getWorld("world"), 0, 4, 0), minPlayerCount, maxPlayerCount, timeLimit,
				waitingTime);
	}

	private void setupMiniGame() {
		this.taskManager = new BukkitTaskManager();
		this.observerList = new ArrayList<MiniGameObserver>();
		this.playerDataManager = new MiniGamePlayerDataManager();

		// register tutorial
		this.getSetting().setTutorial(this.registerTutorial());

		// register custom data
		this.registerCustomData();
	}

	/*
	 * overriding methods
	 */

	// protected void runTaskBeforeStart() {
	// not necessary
	// };

	protected void runTaskAfterStart() {
	}

	protected void runTaskBeforeFinish() {
	}

	protected void runTaskAfterFinish() {
	}

	protected void handleGameException(Player p, Exception exception, Object arg) {
	}

	protected void registerTasks() {
	}

	protected void registerCustomData() {
	}

	private void initMiniGame() {
		this.started = false;
		if (this.players == null) {
			this.players = new HashMap<>();
		} else {
			this.players.clear();
		}

		this.initTasks();

		// clear player data
		this.playerDataManager.clearData();
	}

	private void registerBasicTask() {
		// register waitingTimer task to taskManager
		this.taskManager.registerTask("_waitingTimer", new BukkitRunnable() {

			@Override
			public void run() {
				int waitTime = waitingCounter.getCount();

				// end count down
				if (waitTime <= 0) {
					runStartTasks();
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
				sendTitleToAllPlayers(time, "", 4, 12, 4);

				// play sound
				if (waitTime <= 3) {
					getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_BIT));
				}

				waitingCounter.removeCount(1);
			}
		});

		// register finishTimer task to taskManager
		this.taskManager.registerTask("_finishTimer", new BukkitRunnable() {

			@Override
			public void run() {
				int leftTime = finishCounter.getCount();
				if (leftTime <= 0) {
					runFinishTasks();
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
					sendTitleToAllPlayers("" + time, "", 4, 12, 4);

					// play sound
					getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_NOTE_BLOCK_COW_BELL));
				}

				finishCounter.removeCount(1);
			}
		});
	}

	private void initSettings() {
		this.initMiniGame();

		// init implemented minigame setting values
		this.initGameSettings();
	}

	// passed event from MiniGameManager
	public final void passEvent(Event event) {

		// check exception event
		if (event instanceof PlayerQuitEvent) {
			this.handleException(((PlayerQuitEvent) event).getPlayer(), MiniGame.Exception.PLAYER_QUIT_SERVER, event);
		}
		// process event when minigame started
		if (this.started) {
			this.processEvent(event);
		}
	}

	public final boolean leaveGame(Player p) {
		// check
		// 1. game must not be started
		// 2. game waitingTime counter must be upper than 10

		if (this.started) {
			this.sendMessage(p, "You can't leave game(Reason: game already has started)");
			return false;
		}

		if (this.waitingCounter.getCount() <= Setting.MINIGAME_LEAVE_MIN_TIME) {
			this.sendMessage(p, "You can't leave game(Reason: game will start soon)");
			return false;
		}

		this.setupPlayerLeavingSettings(p, "Before start");

		// check game is emtpy
		if (this.isEmpty()) {
			this.initSettings();
		}

		return true;
	}

	/*
	 * [IMPORTANT] After this method, isEmpty() must be checked and run initSetting() (except
	 * for runFinishTask())
	 */
	private void setupPlayerLeavingSettings(Player p, String reason) {
		if (reason != null) {
			// notify other players to join the game
			this.sendMessageToAllPlayers(p.getName() + " leaved " + this.getTitle() + "(Reason: " + reason + ")");
		}

		// setup player state
		this.setupPlayerWhenLeave(p);

		// remove player from minigame
		this.removePlayer(p);
	}

	public final boolean joinGame(Player p) {
		/*
		 * Logic sequence
		 * 1. check active
		 * 2. check started
		 * 3. check full
		 */

		if (!this.isActive()) {
			this.sendMessage(p, "game is not active");
			return false;
		}

		if (this.started) {
			this.sendMessage(p, "already started");
			return false;
		}

		if (this.isFull()) {
			this.sendMessage(p, "Player is full");
			return false;
		}

		// init setting when first player joins
		if (this.isEmpty()) {
			this.initSettings();
			this.startWaitingTimer();
		}

		// setup join settings
		this.setupPlayerJoinSettings(p);

		// join success
		return true;
	}

	// settings related with player
	private void setupPlayerJoinSettings(Player p) {
		// add player to list
		this.addPlayer(p);

		// setup player
		this.setupPlayerWhenJoin(p);

		// notify info
		this.notifyInfo(p);
	}

	private void notifyInfo(Player p) {
		// print tutorial
		this.printGameTutorial(p);

		// notify other players to join the game
		this.sendMessageToAllPlayers(p.getName() + " joined " + this.getTitle());
	}

	private void printGameTutorial(Player p) {
		p.sendMessage("");
		this.sendMessage(p, "=================================");
		this.sendMessage(p, "" + ChatColor.GREEN + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
		this.sendMessage(p, "=================================");

		// print rule
		this.sendMessage(p, ChatColor.BOLD + "[Rule]");
		this.sendMessage(p, "- Time Limit: " + this.getTimeLimit() + " sec");

		// tutorial
		if (this.getTutorial() != null) {
			for (String msg : this.getTutorial()) {
				this.sendMessage(p, "- " + msg);
			}
		}
	}

	// stop and restart waiting task
	// use for waiting other player join when waitingTask ended
	private void initTasks() {
		// stop all tasks
		this.taskManager.cancelAllTasks();

		// register task
		this.registerTasks();
		this.registerBasicTask();

		// timer counter
		this.waitingCounter = new Counter(this.getWaitingTime());
		this.finishCounter = new Counter(this.getTimeLimit());
	}

	private void startWaitingTimer() {
		// start game after waitingTimer
		this.taskManager.runTaskTimer("_waitingTimer", 0, 20);
	}

	protected void restartWaitingTask() {
		this.initTasks();
		this.startWaitingTimer();
	}

	private void runStartTasks() {
		// check min player count
		if (!this.checkMinPlayerCountRemains()) {
			int needPlayerCount = this.getMinPlayerCount() - this.getPlayerCount();
			// send message
			this.sendMessageToAllPlayers("Game can't start: need " + needPlayerCount + " more player(s) to start");

			// restart waiting task
			this.restartWaitingTask();

			return;
		}

		// start
		started = true;

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_END_PORTAL_SPAWN));

		// starting title
		sendTitleToAllPlayers("START", "", 4, 20 * 2, 4);

		// runTaskAfterStart
		runTaskAfterStart();

		// notify start event to observers
		this.notifyObservers(MiniGameEvent.START);

		// cancel task
		this.taskManager.cancelTask("_waitingTimer");

		// start finishsTimer
		startFinishTimer();
	}

	private void startFinishTimer() {
		this.taskManager.runTaskTimer("_finishTimer", 0, 20);
	}

	private void runFinishTasks() {
		// runTaskBeforeFinish
		runTaskBeforeFinish();

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH));

		printEndInfo();

		// nofity finish event to observers (before remove players)
		this.notifyObservers(MiniGameEvent.FINISH);

		// setup player
		this.getPlayers().forEach(p -> this.setupPlayerLeavingSettings(p, null));

		// runTaskAfterFinish (before initSetting())
		runTaskAfterFinish();

		// initSeting ([IMPORTANT] call after done others)
		initSettings();

		// cancel finish task
		this.taskManager.cancelTask("_finishTimer");
	}

	private void printEndInfo() {
		// title
		for (Player p : this.getPlayers()) {
			// break line
			p.sendMessage("");
			this.sendMessage(p, "=================================");
			this.sendMessage(p, "" + ChatColor.RED + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
			this.sendMessage(p, "=================================");
		}

		// send finish title
		sendTitleToAllPlayers("FINISH", "", 4, 20 * 2, 4);

		// print score
		printScore();
	}

	protected final void endGame() {
		this.runFinishTasks();
	}

	// can print differently depending on game type
	protected void printScore() {
		// print scores in descending order
		this.sendMessageToAllPlayers(ChatColor.BOLD + "[Score]");
		List<Entry<Player, Integer>> entries = SortTool.getDescendingSortedList(this.players);
		int rank = 1;
		for (Entry<Player, Integer> entry : entries) {
			Player p = entry.getKey();
			int score = entry.getValue();
			this.sendMessageToAllPlayers("[" + rank + "] " + p.getName() + ": " + score);
			rank += 1;
		}

	}

	public boolean isEmpty() {
		return this.getPlayers().isEmpty();
	}

	public boolean isFull() {
		return this.getPlayerCount() == this.getMaxPlayerCount();
	}

	public boolean containsPlayer(Player p) {
		return this.players.containsKey(p);
	}

	public List<Player> getPlayers() {
		// copy
		return new ArrayList<Player>(this.players.keySet());
	}

	public int getPlayerCount() {
		return this.getPlayers().size();
	}

	public boolean isStarted() {
		return this.started;
	}

	// private
	private void addPlayer(Player p) {
		// register player with 0 score
		this.players.put(p, 0);
	}

	// private
	private void removePlayer(Player p) {
		this.players.remove(p);
	}

	protected void sendMessage(Player p, String msg) {
		p.sendMessage(ChatColor.BOLD + "[" + this.getTitle() + "] " + ChatColor.WHITE + msg);
	}

	protected void sendMessageToAllPlayers(String msg) {
		this.getPlayers().forEach(p -> this.sendMessage(p, msg));
	}

	protected void sendTitle(Player p, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	protected void sendTitleToAllPlayers(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		this.getPlayers().forEach(p -> this.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut));
	}

	public int getScore(Player p) {
		return this.players.get(p);
	}

	protected void plusScore(Player p, int score) {
		int previousScore = this.players.get(p);
		this.players.put(p, previousScore + score);
		// check scoreNotifying
		if (this.setting.isScoreNotifying()) {
			this.sendMessage(p, ChatColor.GREEN + "+" + ChatColor.WHITE + score);
		}
	}

	protected void plusEveryoneScore(int score) {
		this.getPlayers().forEach(p -> this.plusScore(p, score));
	}

	protected void minusScore(Player p, int score) {
		int previousScore = this.players.get(p);
		this.players.put(p, previousScore - score);
		// check scoreNotifying
		if (this.setting.isScoreNotifying()) {
			this.sendMessage(p, ChatColor.RED + "-" + ChatColor.WHITE + score);
		}
	}

	protected void minusEveryoneScore(int score) {
		this.getPlayers().forEach(p -> this.minusScore(p, score));
	}

	public enum Exception {
		PLAYER_QUIT_SERVER, SERVER_STOP;
	}

	// handle exception and notify to minigame mingame and observers
	public final void handleException(Player p, Exception exception, Object arg) {
		// info
		Utils.info("[" + p.getName() + "] handle exception: " + exception.name());

		// print reason when PLAYER_QUIT_SERVER
		if (exception == Exception.PLAYER_QUIT_SERVER) {
			PlayerQuitEvent event = (PlayerQuitEvent) arg;
			Utils.info("Quit: " + event.getReason().name());
		}

		// setup leaving settings
		this.setupPlayerLeavingSettings(p, exception.name());

		// pass exception to implemented minigame
		this.handleGameException(p, exception, arg);

		// check min player count
		if (!this.checkMinPlayerCountRemains()) {
			// send message
			this.sendMessageToAllPlayers("Game end: game needs more players to play");

			// end game
			this.endGame();
		}

		// notify EXCEPTION event to observers
		this.notifyObservers(MiniGameEvent.EXCEPTION);

		// init settings if empty
		if (this.isEmpty()) {
			this.initSettings();
		}
	}

	private void setupPlayerWhenJoin(Player p) {
		// tp to game location
		p.teleport(this.getLocation());

		// save player data
		this.playerDataManager.savePlayerData(p);

		// make pure state
		this.playerDataManager.makePureState(p);
	}

	private void setupPlayerWhenLeave(Player p) {
		// tp to lobby
		p.teleport(MiniGameManager.getLobby());

		// restore player data
		this.playerDataManager.restorePlayerData(p);
	}

	/*
	 * getters
	 */
	public String getTitle() {
		return this.getSetting().getTitle();
	}

	public Location getLocation() {
		return this.getSetting().getLocation().clone();
	}

	public int getWaitingTime() {
		return this.getSetting().getWaitingTime();
	}

	public int getTimeLimit() {
		return this.getSetting().getTimeLimit();
	}

	public int getMinPlayerCount() {
		return this.getSetting().getMinPlayerCount();
	}

	protected boolean checkMinPlayerCountRemains() {
		return this.getPlayerCount() >= this.getMinPlayerCount();
	}

	public int getMaxPlayerCount() {
		return this.getSetting().getMaxPlayerCount();
	}

	public boolean isActive() {
		return this.getSetting().isActive();
	}

	public boolean isSettingFixed() {
		return this.getSetting().isSettingFixed();
	}

	public List<String> getTutorial() {
		return this.getSetting().getTutorial();
	}

	public Map<String, Object> getCustomData() {
		return this.getSetting().getCustomData();
	}

	/*
	 * check attributes are valid 
	 */
	protected void checkAttributes() {
		// title
		if (this.getTitle().length() <= 0) {
			Utils.warning(this.getTitleWithClassName() + ": title must be at least 1 character");
		}

		// timeLimit
		if (this.getTimeLimit() <= 0) {
			Utils.warning(this.getTitleWithClassName() + ": timeLimit must be at least 1 sec");
		}
	}

	public String getTitleWithClassName() {
		return this.getTitle() + "[Class: " + this.getClassName() + "]";
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	public MiniGameSetting getSetting() {
		return this.setting;
	}

	public int getLeftWaitingTime() {
		return this.waitingCounter.getCount();
	}

	public int getLeftFinishTime() {
		return this.finishCounter.getCount();
	}

	public String getEveryoneName() {
		String members = "";
		for (Player p : this.getPlayers()) {
			members += p.getName() + ", ";
		}
		// remove last ", "
		members = members.substring(0, members.length() - 2);
		return members;
	}

	public List<Entry<Player, Integer>> getScoreRanking() {
		return SortTool.getDescendingSortedList(this.players);
	}

	protected BukkitTaskManager getTaskManager() {
		return this.taskManager;
	}

	protected Player randomPlayer() {
		int random = (int) (Math.random() * this.getPlayerCount());
		return this.getPlayers().get(random);
	}

	@Override
	public void registerObserver(MiniGameObserver observer) {
		if (!this.observerList.contains(observer)) {
			this.observerList.add(observer);
		}
	}

	@Override
	public void unregisterObserver(MiniGameObserver observer) {
		this.observerList.remove(observer);
	}

	@Override
	public void notifyObservers(MiniGameEvent event) {
		this.observerList.forEach(obs -> obs.update(event, new MiniGameAccessor(this)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof MiniGame) {
				return this.getTitle().equals(((MiniGame) obj).getTitle());
			}

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
