package com.minigameworld.minigameframes;

import java.util.ArrayList;
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

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.minigameframes.helpers.MiniGameCustomOption;
import com.minigameworld.minigameframes.helpers.MiniGameData;
import com.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.minigameworld.minigameframes.helpers.MiniGamePlayerStateManager;
import com.minigameworld.minigameframes.helpers.MiniGameRankManager;
import com.minigameworld.minigameframes.helpers.MiniGameSetting;
import com.minigameworld.minigameframes.helpers.MiniGameTaskManager;
import com.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;
import com.minigameworld.minigameframes.helpers.MiniGameSetting.RankOrder;
import com.minigameworld.observer.MiniGameEventNotifier;
import com.minigameworld.observer.MiniGameObserver;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.instance.TaskManager;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;

public abstract class MiniGame implements MiniGameEventNotifier {
	/*
	 * MiniGame Class
	 */

	// Info
	private MiniGameSetting setting;

	// task manager
	private MiniGameTaskManager taskManager;

	// player data manager
	private MiniGamePlayerStateManager playerStateManager;

	// rank manager
	private MiniGameRankManager rankManager;

	// data
	private MiniGameData minigameData;

	// custom option
	private MiniGameCustomOption customOption;

	// check game has started
	private boolean started;

	// player data (score, live)
	private List<MiniGamePlayerData> players;

	// observer list
	private List<MiniGameObserver> observerList;

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
		// register basic tasks
		this.taskManager = new MiniGameTaskManager(this);
		this.taskManager.registerBasicTasks();

		this.observerList = new ArrayList<MiniGameObserver>();
		this.playerStateManager = new MiniGamePlayerStateManager();
		this.rankManager = new MiniGameRankManager(this);
		this.minigameData = new MiniGameData(this);

		// register tutorial
		this.getSetting().setTutorial(this.registerTutorial());

		// register custom data
		this.registerCustomData();

		// custom option
		this.customOption = new MiniGameCustomOption(this);
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

	protected void handleGameException(Player p, GameException exception, Object arg) {
	}

	protected void registerCustomData() {
	}

	private void initMiniGame() {
		this.started = false;

		if (this.players == null) {
			this.players = new ArrayList<MiniGamePlayerData>();
		} else {
			this.players.clear();
		}

		this.initTasks();

		// clear player data
		this.playerStateManager.clearAllPlayers();
	}

	private void initSettings() {
		this.initMiniGame();

		// init implemented minigame setting values
		this.initGameSettings();
	}

	// passed event from MiniGameManager
	public final void passEvent(Event event) {
		// pass event to custom option
		this.customOption.processEvent(event);

		// check exception events
		if (event instanceof PlayerQuitEvent) {
			this.handleException(((PlayerQuitEvent) event).getPlayer(), MiniGame.GameException.PLAYER_QUIT_SERVER,
					event);
		} else if (event instanceof AsyncChatEvent) {
			this.processChatting((AsyncChatEvent) event);
		}

		// process event when minigame started
		if (this.started) {
			this.processEvent(event);
		}
	}

	protected void processChatting(AsyncChatEvent e) {
		e.setCancelled(true);
		Player p = e.getPlayer();
		String msg = ((TextComponent) e.message()).content();
		this.getPlayers().forEach(all -> this.sendMessage(all, p.getName() + ": " + msg));
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

	public final boolean leaveGame(Player p) {
		// check
		// 1. game must not be started
		// 2. game waitingTime counter must be upper than 10

		if (this.started) {
			this.sendMessage(p, "You can't leave game(Reason: game already has started)");
			return false;
		}

		if (this.getLeftWaitingTime() <= Setting.MINIGAME_LEAVE_MIN_TIME) {
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

	private void notifyInfo(Player p) {
		// print tutorial
		this.printGameTutorial(p);

//		// notify other players to join the game
//		this.sendMessageToAllPlayers(p.getName() + " joined " + this.getTitle());
	}

	private void printGameTutorial(Player p) {
		p.sendMessage("");
		p.sendMessage("=================================");
		p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
		p.sendMessage("=================================");

		// print rule
		p.sendMessage("\n" + ChatColor.BOLD + "[Rule]");
		p.sendMessage("- Time Limit: " + this.getTimeLimit() + " sec");

		// tutorial
		if (this.getTutorial() != null) {
			for (String msg : this.getTutorial()) {
				p.sendMessage("- " + msg);
			}
		}
	}

	// stop and restart waiting task
	// use for waiting other player join when waitingTask ended
	private void initTasks() {
		this.taskManager.init();
	}

	private void startWaitingTimer() {
		// start game after waitingTimer
		this.getTaskManager().runTaskTimer("_waitingTimer", 0, 20);
	}

	protected void restartWaitingTask() {
		this.initTasks();
		this.startWaitingTimer();
	}

	public void runStartTasks() {
		// check min player count
		if (this.getPlayerCount() < this.getMinPlayerCount()) {
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
		this.getTaskManager().cancelTask("_waitingTimer");

		// start finishsTimer
		startFinishTimer();
	}

	private void startFinishTimer() {
		this.getTaskManager().runTaskTimer("_finishTimer", 0, 20);
	}

	public void runFinishTasks() {
		// runTaskBeforeFinish
		runTaskBeforeFinish();

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH));

		printEndInfo();

		// nofity finish event to observers (before remove players)
		this.notifyObservers(MiniGameEvent.FINISH);

		// runTaskAfterFinish (before initSetting())
		runTaskAfterFinish();

		// setup player
		this.getPlayers().forEach(p -> this.setupPlayerLeavingSettings(p, null));

		// initSeting ([IMPORTANT] call after done others)
		initSettings();

		// cancel finish task
		this.getTaskManager().cancelTask("_finishTimer");
	}

	private void printEndInfo() {
		if (this.isEmpty()) {
			return;
		}

		// title
		for (Player p : this.getPlayers()) {
			// break line
			p.sendMessage("");
			p.sendMessage("=================================");
			p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
			p.sendMessage("=================================");
		}

		// send finish title
		sendTitleToAllPlayers("FINISH", "", 4, 20 * 2, 4);

		// print score
		printScore();
	}

	// print scores in rank order
	// can print differently depending on game type
	protected void printScore() {
		BroadcastTool.sendMessage(this.getPlayers(), ChatColor.BOLD + "[Score]");

		List<Entry<Player, Integer>> entries = this.getRank(this.getPlayers());
		int rank = 1;
		for (Entry<Player, Integer> entry : entries) {
			Player p = entry.getKey();
			int score = entry.getValue();
			BroadcastTool.sendMessage(this.getPlayers(), "[" + rank + "] " + p.getName() + ": " + score);
			rank += 1;
		}

	}

	protected List<Entry<Player, Integer>> getRank(List<Player> players) {
		// return rank with MiniGame RankOrder setting
		RankOrder order = this.getSetting().getRankOrder();
		if (order == RankOrder.ASCENDING) {
			return this.rankManager.getAscendingScoreRanking(players);
		} else { // if (order == RankOrder.DESCENDING) {
			return this.rankManager.getDescendingScoreRanking(players);
		}
	}

	public enum GameException {
		PLAYER_QUIT_SERVER, SERVER_STOP;
	}

	// handle exception and notify to minigame mingame and observers
	public final void handleException(Player p, GameException exception, Object arg) {
		// info
		Utils.info("[" + p.getName() + "] handle exception: " + exception.name());

		// print reason when PLAYER_QUIT_SERVER
		if (exception == GameException.PLAYER_QUIT_SERVER) {
			PlayerQuitEvent event = (PlayerQuitEvent) arg;
			Utils.info("Quit: " + event.getReason().name());
		}

		// setup leaving settings
		this.setupPlayerLeavingSettings(p, exception.name());

		// notify EXCEPTION event to observers
		this.notifyObservers(MiniGameEvent.EXCEPTION);

		// pass exception to implemented minigame
		this.handleGameException(p, exception, arg);

		// check min player count
		if (!this.isMinPlayersLive()) {
			// send message
			this.sendMessageToAllPlayers("Game end: live players count is under the min player count");

			// end game
			this.endGame();
		}
	}

	private void setupPlayerWhenJoin(Player p) {
		// tp to game location
		p.teleport(this.getLocation());

		// save player data
		this.playerStateManager.savePlayerState(p);

		// make pure state
		this.playerStateManager.makePureState(p);
	}

	private void setupPlayerWhenLeave(Player p) {
		// tp to lobby
		p.teleport(MiniGameManager.getLobby());

		// restore player data
		this.playerStateManager.restorePlayerState(p);
	}

	public boolean isEmpty() {
		return this.getPlayers().isEmpty();
	}

	public boolean isFull() {
		return this.getPlayerCount() == this.getMaxPlayerCount();
	}

	public boolean containsPlayer(Player p) {
		return this.getPlayerData(p) != null;
	}

	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		this.players.forEach(pData -> players.add(pData.getPlayer()));
		return players;
	}

	public int getPlayerCount() {
		return this.getPlayers().size();
	}

	public boolean isStarted() {
		return this.started;
	}

	protected final void endGame() {
		this.runFinishTasks();
	}

	// private
	private void addPlayer(Player p) {
		// register player (score: 0, live: true)
		this.players.add(new MiniGamePlayerData(p));
	}

	// private
	private void removePlayer(Player p) {
		if (this.containsPlayer(p)) {
			this.players.remove(this.getPlayerData(p));
		}
	}

	public void sendMessage(Player p, String msg) {
		p.sendMessage(ChatColor.BOLD + "[" + this.getTitle() + "] " + ChatColor.WHITE + msg);
	}

	public void sendMessageToAllPlayers(String msg) {
		this.getPlayers().forEach(p -> this.sendMessage(p, msg));
	}

	public void sendTitle(Player p, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	public void sendTitle(Player p, String title, String subTitle) {
		p.sendTitle(title, subTitle, 4, 12, 4);
	}

	public void sendTitleToAllPlayers(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		this.getPlayers().forEach(p -> this.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut));
	}

	public void sendTitleToAllPlayers(String title, String subTitle) {
		this.getPlayers().forEach(p -> this.sendTitle(p, title, subTitle, 4, 12, 4));
	}

	/*
	 * MiniGamePlayerData
	 */
	public MiniGamePlayerData getPlayerData(Player p) {
		for (MiniGamePlayerData pData : this.players) {
			if (pData.isSamePlayer(p)) {
				return pData;
			}
		}
		return null;
	}

	public int getScore(Player p) {
		return this.getPlayerData(p).getScore();
	}

	protected void plusScore(Player p, int amount) {
		this.getPlayerData(p).plusScore(amount);
		// check scoreNotifying
		if ((boolean) this.customOption.getOption(Option.SCORE_NOTIFYING)) {
			this.sendMessage(p, ChatColor.GREEN + "+" + ChatColor.WHITE + amount);
		}
	}

	protected void plusEveryoneScore(int amount) {
		this.getPlayers().forEach(p -> this.plusScore(p, amount));
	}

	protected void minusScore(Player p, int amount) {
		this.getPlayerData(p).minusScore(amount);
		// check scoreNotifying
		if ((boolean) this.customOption.getOption(Option.SCORE_NOTIFYING)) {
			this.sendMessage(p, ChatColor.RED + "-" + ChatColor.WHITE + amount);
		}
	}

	protected void minusEveryoneScore(int amount) {
		this.getPlayers().forEach(p -> this.minusScore(p, amount));
	}

	protected void setLive(Player p, boolean live) {
		this.getPlayerData(p).setLive(live);

//		// check min player count
//		if (!this.isMinPlayersLive()) {
//			// send message
//			this.sendMessageToAllPlayers("Game end: live players count is under the min player count");
//
//			// end game
//			this.endGame();
//		}
	}

	protected boolean isLive(Player p) {
		return this.getPlayerData(p).isLive();
	}

	protected List<Player> getLivePlayers() {
		List<Player> livePlayers = new ArrayList<Player>();
		for (Player p : this.getPlayers()) {
			if (this.getPlayerData(p).isLive()) {
				livePlayers.add(p);
			}
		}
		return livePlayers;
	}

	protected int getLivePlayersCount() {
		return this.getLivePlayers().size();
	}

	protected boolean isMinPlayersLive() {
		return this.getLivePlayersCount() >= this.getMinPlayerCount();
	}

	/*
	 * MiniGameSetting getters
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

	public String getTitleWithClassName() {
		return this.getTitle() + " [Class: " + this.getClassName() + "]";
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	public MiniGameSetting getSetting() {
		return this.setting;
	}

	public int getLeftWaitingTime() {
		return this.taskManager.getLeftWaitingTime();
	}

	public int getLeftFinishTime() {
		return this.taskManager.getLeftFinishTime();
	}

	protected TaskManager getTaskManager() {
		return this.taskManager.getTaskManager();
	}

	public MiniGameRankManager getMiniGameRankManager() {
		return this.rankManager;
	}

	public MiniGameData getMiniGameData() {
		return this.minigameData;
	}

	public MiniGameCustomOption getCustomOption() {
		return this.customOption;
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
