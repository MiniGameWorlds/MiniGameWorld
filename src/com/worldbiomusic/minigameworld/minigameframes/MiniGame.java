package com.worldbiomusic.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.instance.TaskManager;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameDataManager;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerStateManager;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRankComparable;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting.GameFinishCondition;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting.RankOrder;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameTaskManager;
import com.worldbiomusic.minigameworld.observer.MiniGameEventNotifier;
import com.worldbiomusic.minigameworld.observer.MiniGameObserver;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

/**
 * <b>MiniGame class of all minigames</b> <br>
 * - Custom minigame frame can be made with extending this class<br>
 * - Message only send to same minigame players <br>
 */
public abstract class MiniGame implements MiniGameEventNotifier {

	/**
	 * Basic data (e.g. title, location, time limit ...)
	 */
	private MiniGameSetting setting;

	/**
	 * Settind data manager with config (using YamlManager)
	 */
	private MiniGameDataManager minigameDataManager;

	/**
	 * options like BLOCK_BREAK, INVENTORY_SAVE (nested in custom data section)
	 */
	private MiniGameCustomOption customOption;

	/**
	 * Task manager
	 */
	private MiniGameTaskManager minigameTaskManager;

	/**
	 * Player state manager (health, food level ...)
	 */
	private MiniGamePlayerStateManager playerStateManager;

	/**
	 * Whether game started or not
	 */
	private boolean started;

	/**
	 * Minigame player data (score, live)
	 */
	private List<MiniGamePlayerData> players;

	/**
	 * Third-party observer list
	 */
	private List<MiniGameObserver> observerList;

	/**
	 * Executed every time when game starts
	 */
	protected abstract void initGameSettings();

	/**
	 * Executed when event passed to minigame
	 */
	protected abstract void processEvent(Event event);

	/**
	 * Register minigame tutorial
	 */
	protected abstract List<String> registerTutorial();

	// Not necessary
	// protected void runTaskBeforeStart() {
	// };

	/**
	 * Executed immediately after game started
	 */
	protected void runTaskAfterStart() {
	}

	/**
	 * Executed before game finishes (players remains)
	 */
	protected void runTaskBeforeFinish() {
	}

	/**
	 * Executed after game finished (player doesn't remains)
	 */
	protected void runTaskAfterFinish() {
	}

	/**
	 * Executed when Minigame exception created<br>
	 * {@link Exception}
	 */
	protected void handleGameException(Player p, Exception exception) {
	}

	/**
	 * Register custom data with this method
	 */
	protected void registerCustomData() {
	}

	/**
	 * Load custom data with this method
	 */
	public void loadCustomData() {
	}

	/**
	 * Constructor with location
	 * 
	 * @param title          Used title in the server (different with class name)
	 * @param location       Playing location
	 * @param minPlayerCount Minimum player count to play
	 * @param maxPlayerCount Maximum player count to play
	 * @param timeLimit      Minigame playing time
	 * @param waitingTime    Waiting time before join minigame
	 */
	protected MiniGame(String title, Location location, int minPlayerCount, int maxPlayerCount, int timeLimit,
			int waitingTime) {
		this.setting = new MiniGameSetting(title, location, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);

		// [must setup once]
		this.setupMiniGame();

		// minigame setup
		this.initBaseSettings();
	}

	/**
	 * Base constructor<br>
	 * Location set to default (Bukkit.getWorld("world"))
	 * 
	 * @param title          Used title in the server (different with class name)
	 * @param minPlayerCount Minimum player count to play
	 * @param maxPlayerCount Maximum player count to play
	 * @param timeLimit      Minigame playing time
	 * @param waitingTime    Waiting time before join minigame
	 */
	protected MiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime) {
		this(title, new Location(Bukkit.getWorld("world"), 0, 4, 0), minPlayerCount, maxPlayerCount, timeLimit,
				waitingTime);
	}

	/**
	 * Setup minigame just once
	 */
	private void setupMiniGame() {
		// setup player list
		this.players = new ArrayList<MiniGamePlayerData>();

		// register basic tasks
		this.minigameTaskManager = new MiniGameTaskManager(this);
		this.minigameTaskManager.registerBasicTasks();

		this.observerList = new ArrayList<MiniGameObserver>();
		this.playerStateManager = new MiniGamePlayerStateManager(this);
		this.minigameDataManager = new MiniGameDataManager(this);

		// register tutorial
		this.getSetting().setTutorial(this.registerTutorial());

		// register custom data
		this.registerCustomData();

		// custom option
		this.customOption = new MiniGameCustomOption(this);
	}

	/**
	 * Init(reset) minigame settings<br>
	 * <b>[IMPORTANT]</b><br>
	 * - Executed every game ready to start or ended
	 * 
	 */
	private void initSettings() {
		this.initBaseSettings();

		// init implemented minigame setting values
		this.initGameSettings();
	}

	/**
	 * Init(reset) base settings
	 */
	private void initBaseSettings() {
		this.started = false;

		this.players.clear();

		this.initTasks();

		// clear player data
		this.playerStateManager.clearAllPlayers();
	}

	/**
	 * Pass event to minigame after processing custom option, exception, chat event
	 * 
	 * @param event Passed event from "MiniGameManager" after check related with
	 *              this minigame
	 */
	public final void passEvent(Event event) {
		// notify observers when event passed to the minigame
		notifyObservers(MiniGameEvent.EVENT_PASSED);

		// pass event to custom option
		this.customOption.processEvent(event);

		// chat event
		if (event instanceof AsyncPlayerChatEvent) {
			this.isProcessChatting((AsyncPlayerChatEvent) event);
		}

		// process event when minigame started
		if (this.started) {
			this.processEvent(event);
		}
	}

	/**
	 * Cancel chat event (set audiences only minigame players)<br>
	 * Pass event to processChatting() when "CHATTING" custom option is true
	 * 
	 * @param e Chat event
	 */
	private void isProcessChatting(AsyncPlayerChatEvent e) {
		// cancel event
		e.setCancelled(true);

		if ((boolean) this.getCustomOption().get(Option.CHATTING)) {
			this.processChatting(e);
		}
	}

	/**
	 * Change chat format for minigame frame
	 * 
	 * @param e Chat event
	 */
	protected void processChatting(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		this.getPlayers().forEach(all -> this.sendMessage(all, p.getName() + ": " + msg));
	}

	/**
	 * Join player to minigame<br>
	 * [Check List]<br>
	 * 1. active is true<br>
	 * 2. started is false<br>
	 * 3. not full<br>
	 * Teleport player to plyaing location
	 * 
	 * @param p Player who tries to join
	 * @return Result of try to join
	 */
	public final boolean joinGame(Player p) {
		if (!this.isActive()) {
			this.sendMessage(p, "Minigame is not active");
			return false;
		}

		if (this.started) {
			this.sendMessage(p, "Already started");
			return false;
		}

		if (this.isFull()) {
			this.sendMessage(p, "Player is full");
			return false;
		}

		// init setting when first player joins
		if (this.isEmpty()) {
			this.initSettings();
			this.minigameTaskManager.runWaitingTask();
		}

		// setup join settings
		this.setupPlayerJoinSettings(p);

		// join success
		return true;
	}

	/**
	 * Setup player join settings
	 * 
	 * @param p Joined player
	 */
	private void setupPlayerJoinSettings(Player p) {
		// setup player
		// [IMPORTANT] must be processed before "addPlayer()"
		this.setupPlayerWhenJoin(p);

		// add player to list
		this.addPlayer(p);

		// notify info
		this.notifyInfo(p);
	}

	/**
	 * Leave player from minigame<br>
	 * 1. not started<br>
	 * 2. left waiting is under 3 or equals<br>
	 * 
	 * @param p leaving player
	 * @return Result of try to leave
	 */
	public final boolean leaveGame(Player p) {
		// check
		// 1. game must not be started
		// 2. game waitingTime counter must be upper than 3

		if (this.started) {
			this.sendMessage(p, "You can't leave game(Reason: game already has started)");
			return false;
		}

		if (this.getLeftWaitingTime() <= Setting.MINIGAME_LEAVE_MIN_TIME) {
			this.sendMessage(p, "You can't leave game(Reason: game will start soon)");
			return false;
		}

		// send message everyone
		Utils.broadcast(p.getName() + " leaved " + this.getColoredTitle());

		this.setupPlayerLeavingSettings(p, "Before start");

		// check game is emtpy
		if (this.isEmpty()) {
			this.initBaseSettings();
		}

		return true;
	}

	/**
	 * [IMPORTANT] After this method, isEmpty() must be checked and run
	 * initSetting() (except for runFinishTask())
	 * 
	 * @param p      Leaving player
	 * @param reason Leaving reason
	 */
	private void setupPlayerLeavingSettings(Player p, String reason) {
		if (reason != null) {
			// notify other players to join the game
			this.sendMessageToAllPlayers(
					p.getName() + " leaved " + this.getColoredTitle() + "(Reason: " + reason + ")");
		}

		// remove player from minigame
		// [IMPORTANT] Must called before "setupPlayerWhenLeave()" to block cycle event
		// processing
		this.removePlayer(p);

		// setup player state
		this.setupPlayerWhenLeave(p);

	}

	/**
	 * Notify infomations to player
	 * 
	 * @param p Player to notify
	 */
	private void notifyInfo(Player p) {
		// print tutorial
		this.printGameTutorial(p);

		// notify all players to join the game
		Utils.broadcast(p.getName() + " joined " + this.getColoredTitle());
	}

	/**
	 * Print tutorial to player
	 * 
	 * @param p Player to print tutorial
	 */
	private void printGameTutorial(Player p) {
		p.sendMessage("");
		p.sendMessage(ChatColor.GREEN + "=================================");
		p.sendMessage("" + ChatColor.BOLD + this.getColoredTitle());

		// print rule
		p.sendMessage("\n" + ChatColor.BOLD + "[Rule]");
		p.sendMessage("- Time Limit: " + this.getTimeLimit() + " sec");

		// tutorial
		if (this.getTutorial() != null) {
			for (String msg : this.getTutorial()) {
				p.sendMessage("- " + msg);
			}
		}

		p.sendMessage(ChatColor.GREEN + "=================================");
	}

	/**
	 * Cancel all tasks and timer count (waitingTime task, finishTime task)
	 */
	private void initTasks() {
		this.minigameTaskManager.init();
	}

	/**
	 * Restart waiting timer (Used when "waiting players count" is under "min player
	 * count")
	 */
	private void restartWaitingTask() {
		this.initTasks();
		this.minigameTaskManager.runWaitingTask();
	}

	/**
	 * Start minigame after check min player count<br>
	 * Notify START to observers<br>
	 * <br>
	 * Execute "runTaskAfterStart()"
	 */
	public void startGame() {
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
		this.started = true;

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_END_PORTAL_SPAWN));

		// starting title
		sendTitleToAllPlayers("START", "", 4, 20 * 2, 4);

		// runTaskAfterStart
		runTaskAfterStart();

		// notify start event to observers
		this.notifyObservers(MiniGameEvent.START);

		// cancel task
		this.minigameTaskManager.cancelWaitingTask();

		// start finishsTimer
		this.minigameTaskManager.runFinishTask();
	}

	/**
	 * Finish minigame<br>
	 * Notify FINISH to observers<br>
	 * 
	 * Execute "runTaskBeforeFinish()" at first<br>
	 * Execute "runTaskAfterFinish" at last
	 * 
	 */
	public void finishGame() {
		if (!this.isStarted()) {
			return;
		}

		// [IMPORTANT] stop all active tasks immediately after finish
		initTasks();

		// runTaskBeforeFinish
		runTaskBeforeFinish();

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH));

		printEndInfo();

		// save players for minigame finish event
		List<MiniGamePlayerData> leavingPlayers = new ArrayList<>(this.players);

		// setup player
		this.getPlayers().forEach(p -> this.setupPlayerLeavingSettings(p, null));

		// notify finish event to observers (after setup player leaving settings (e.g.
		// give reward(item) after state restored))

		// [IMPORTANT] restore removed leaving players for a while
		this.players.addAll(leavingPlayers);
		this.notifyObservers(MiniGameEvent.FINISH);
		this.players.clear();

		// runTaskAfterFinish (before initSetting())
		runTaskAfterFinish();

		// initSeting ([IMPORTANT] call after others done)
		initBaseSettings();

		// cancel finish task
		this.minigameTaskManager.cancelFinishTask();
	}

	/**
	 * Print scores
	 */
	private void printEndInfo() {
		if (this.isEmpty()) {
			return;
		}

		// title
		for (Player p : this.getPlayers()) {
			// break line
			p.sendMessage("");
			p.sendMessage(ChatColor.RED + "=================================");
			p.sendMessage("" + ChatColor.BOLD + this.getColoredTitle());
			p.sendMessage("");
		}

		// send finish title
		sendTitleToAllPlayers("FINISH", "", 4, 20 * 2, 4);

		// print score
		printScore();

		this.getPlayers().forEach(p -> p.sendMessage(ChatColor.RED + "================================="));
	}

	/**
	 * Print scores to all players in rank order (custom option)<br>
	 * Can print format differently depending on game type
	 */
	protected void printScore() {
		BroadcastTool.sendMessage(this.getPlayers(), ChatColor.BOLD + "[Score]");

//		List<Entry<Player, Integer>> entries = this.getRank(this.players);
		@SuppressWarnings("unchecked")
		List<MiniGamePlayerData> rankList = (List<MiniGamePlayerData>) this.getRank();
		int rank = 1;
		ChatColor[] rankColors = { ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE };

		for (MiniGamePlayerData ranking : rankList) {
			Player p = ranking.getPlayer();
			int score = ranking.getScore();

			// rank string with color
			String rankString = "[";
			if (rank <= 3) {
				rankString += rankColors[rank - 1];
			}
			rankString += rank + "" + ChatColor.RESET + "] ";

			BroadcastTool.sendMessage(this.getPlayers(), rankString + p.getName() + ": " + ChatColor.GOLD + score);
			rank += 1;
		}

	}

	/**
	 * Get rank data with RankOrder (custom option)
	 * 
	 * @return Ordered data
	 * @see RankOrder
	 */
	public List<? extends MiniGameRankComparable> getRank() {
		Collections.sort(this.players);

		RankOrder order = this.getSetting().getRankOrder();
		if (order == RankOrder.DESCENDING) {
			Collections.reverse(this.players);
		}

		return this.players;
	}

	/**
	 * Minigame exceptions<br>
	 * <br>
	 * How to use "CUSTOM"<br>
	 * <br>
	 * 
	 * <b>Create exception</b><br>
	 * 
	 * <pre>
	 * public void processServerEvent(Player p) {
	 * 	MiniGame.Exception ex = MiniGame.Exception.CUSTOM;
	 * 	ex.setDetailedReason("SERVER_EVENT_TIME");
	 * 	MiniGameWorld mw = MiniGameWorld.create("1.x.x");
	 * 	mw.handleException(p, ex);
	 * }
	 * </pre>
	 * 
	 * <b>Handle exception</b><br>
	 * 
	 * <pre>
	 * &#64;Override
	 * protected void handleGameException(Player p, Exception exception, Object arg) {
	 * 	super.handleGameException(p, exception);
	 * 	if (exception == MiniGame.Exception.CUSTOM) {
	 * 		String detailedReason = exception.getDetailedReason();
	 * 		if (detailedReason.equals("SERVER_EVENT_TIME")) {
	 * 			// process somethings
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 *
	 */
	public enum Exception {
		PLAYER_QUIT_SERVER(""), CUSTOM("");

		private String detailedReason;
		private Object detailedObj;

		private Exception(String detailedReason) {
			this.detailedReason = detailedReason;
			this.detailedObj = null;
		}

		public String getDetailedReason() {
			return this.detailedReason;
		}

		public void setDetailedReason(String detailedReason) {
			this.detailedReason = detailedReason;
		}

		public Object getDetailedObj() {
			return detailedObj;
		}

		public void setDetailedObj(Object detailedObj) {
			this.detailedObj = detailedObj;
		}

	}

	/**
	 * - Handle exception<br>
	 * - Leave player<br>
	 * - Notify observers<br>
	 * - Check GameFinishCondition
	 * 
	 * @param p         Target player
	 * @param exception Minigame exception
	 */
	public final void handleException(Player p, Exception exception) {
		// info
		Utils.info("[" + p.getName() + "] handle exception: " + exception.name());

		// print reason
		Utils.info("Detailed reason: " + exception.getDetailedReason());

		// setup leaving settings
		this.setupPlayerLeavingSettings(p, exception.name());

		// notify EXCEPTION event to observers
		this.notifyObservers(MiniGameEvent.EXCEPTION);

		// pass exception to implemented minigame
		this.handleGameException(p, exception);

		// check GameFinishExceptionMode
		this.checkGameFinishCondition(this.getSetting().getGameFinishCondition());
	}

	/**
	 * Check game finish condition
	 * 
	 * @param condition Game finish condition
	 */
	protected void checkGameFinishCondition(GameFinishCondition condition) {
		boolean needToFinish = false;
		switch (condition) {
		case NONE:
			break;
		case MIN_PLAYERS_LIVE:
			needToFinish = !this.isMinPlayersLive();
			break;
		case MIN_PLAYERS_LEFT:
			needToFinish = !this.isMinPlayersLeft();
			break;
		}
		// must finish the game
		if (this.isEmpty()) {
			needToFinish = true;
		}
		if (needToFinish) {
			this.finishGame();
		}
	}

	/**
	 * Setup things when join (teleport, save state)
	 * 
	 * @param p Target player
	 */
	private void setupPlayerWhenJoin(Player p) {
		// save player data
		this.playerStateManager.savePlayerState(p);

		// make pure state
		this.playerStateManager.makePureState(p);

		// tp to game location
		p.teleport(this.getLocation());
	}

	/**
	 * Setup things when leave (teleport, save state)
	 * 
	 * @param p
	 */
	private void setupPlayerWhenLeave(Player p) {
		// restore player data
		this.playerStateManager.restorePlayerState(p);
	}

	/**
	 * Check minigame has players
	 * 
	 * @return True if no players in minigame
	 */
	public boolean isEmpty() {
		return this.getPlayers().isEmpty();
	}

	/**
	 * Check minigame is full of players
	 * 
	 * @return True if players = max player count
	 */
	public boolean isFull() {
		return this.getPlayerCount() == this.getMaxPlayerCount();
	}

	/**
	 * Check player is playing minigame
	 * 
	 * @param p Target player
	 * @return True if player is playing minigame
	 */
	public boolean containsPlayer(Player p) {
		return this.getPlayerData(p) != null;
	}

	/**
	 * Get playing player list
	 * 
	 * @return Player list
	 */
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		this.players.forEach(pData -> players.add(pData.getPlayer()));
		return players;
	}

	/**
	 * Get playing players count
	 * 
	 * @return playing players count
	 */
	public int getPlayerCount() {
		return this.getPlayers().size();
	}

	/**
	 * Check minigame has started
	 * 
	 * @return True if minigame already started
	 */
	public boolean isStarted() {
		return this.started;
	}

	/**
	 * Add player to minigame list
	 * 
	 * @param p Joined player
	 */
	private void addPlayer(Player p) {
		// register player (score: 0, live: true)
		this.players.add(new MiniGamePlayerData(this, p));
	}

	/**
	 * Remove player from minigame list
	 * 
	 * @param p Leaved player
	 */
	private void removePlayer(Player p) {
		if (this.containsPlayer(p)) {
			this.players.remove(this.getPlayerData(p));
		}
	}

	/**
	 * Send message to player with minigame title prefix
	 * 
	 * @param p   Audience
	 * @param msg message
	 */
	public void sendMessage(Player p, String msg) {
		p.sendMessage("[" + this.getColoredTitle() + "] " + msg);
	}

	/**
	 * Sendm message to all players with minigame title prefix
	 * 
	 * @param msg message
	 */
	public void sendMessageToAllPlayers(String msg) {
		this.getPlayers().forEach(p -> this.sendMessage(p, msg));
	}

	/**
	 * Send title with minigame title prefix
	 * 
	 * @param p        Audience
	 * @param title    Title string
	 * @param subTitle Subtitle string
	 * @param fadeIn   Fade in time (tick)
	 * @param stay     Stay time (tick)
	 * @param fadeOut  Fade out time (tick)
	 */
	public void sendTitle(Player p, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	/**
	 * Send title with minigame title prefix with default time settings
	 * 
	 * @param p        Audience
	 * @param title    Title string
	 * @param subTitle Subtitle string
	 */
	public void sendTitle(Player p, String title, String subTitle) {
		p.sendTitle(title, subTitle, 4, 12, 4);
	}

	/**
	 * Send title to all players with minigame title prefix
	 * 
	 * @param title    Title string
	 * @param subTitle Subtitle string
	 * @param fadeIn   Fade in time (tick)
	 * @param stay     Stay time (tick)
	 * @param fadeOut  Fade out time (tick)
	 */
	public void sendTitleToAllPlayers(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		this.getPlayers().forEach(p -> this.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut));
	}

	/**
	 * Send title to all players with minigame title prefix with default time
	 * settings
	 * 
	 * @param title    Title string
	 * @param subTitle Subtitle string
	 */
	public void sendTitleToAllPlayers(String title, String subTitle) {
		this.getPlayers().forEach(p -> this.sendTitle(p, title, subTitle, 4, 12, 4));
	}

	/**
	 * Get PlayerData
	 * 
	 * @param p Target player
	 * @return PlayerData of p
	 */
	public MiniGamePlayerData getPlayerData(Player p) {
		for (MiniGamePlayerData pData : this.players) {
			if (pData.isSamePlayer(p)) {
				return pData;
			}
		}
		return null;
	}

	/**
	 * Get all player data list
	 * 
	 * @return PlayerData list
	 */
	public List<MiniGamePlayerData> getPlayerDataList() {
		return this.players;
	}

	/**
	 * Get score of player
	 * 
	 * @param p Target player
	 * @return Player's score
	 */
	public int getScore(Player p) {
		return this.getPlayerData(p).getScore();
	}

	/**
	 * Plus player score
	 * 
	 * @param p      Target player
	 * @param amount Score amount
	 */
	protected void plusScore(Player p, int amount) {
		if (!containsPlayer(p)) {
			return;
		}

		MiniGamePlayerData pData = this.getPlayerData(p);
		pData.plusScore(amount);
		// check scoreNotifying
		if ((boolean) this.customOption.get(Option.SCORE_NOTIFYING)) {
			String totalScore = " (" + ChatColor.GOLD + pData.getScore() + ChatColor.RESET + ")";
			this.sendMessage(p, ChatColor.GREEN + "+" + ChatColor.RESET + amount + totalScore);
		}
	}

	/**
	 * Plus everyone's score
	 * 
	 * @param amount Score amount
	 */
	protected void plusEveryoneScore(int amount) {
		this.getPlayers().forEach(p -> this.plusScore(p, amount));
	}

	/**
	 * Minus player score
	 * 
	 * @param p      Target player
	 * @param amount Score amount
	 */
	protected void minusScore(Player p, int amount) {
		if (!containsPlayer(p)) {
			return;
		}

		MiniGamePlayerData pData = this.getPlayerData(p);
		pData.minusScore(amount);
		// check scoreNotifying
		if ((boolean) this.customOption.get(Option.SCORE_NOTIFYING)) {
			String totalScore = " (" + ChatColor.GOLD + pData.getScore() + ChatColor.RESET + ")";
			this.sendMessage(p, ChatColor.RED + "-" + ChatColor.RESET + amount + totalScore);
		}
	}

	/**
	 * Minus everyone's score
	 * 
	 * @param amount Score amount
	 */
	protected void minusEveryoneScore(int amount) {
		this.getPlayers().forEach(p -> this.minusScore(p, amount));
	}

	/**
	 * Mark player's live
	 * 
	 * @param p    Target player
	 * @param live False to mark death
	 */
	protected void setLive(Player p, boolean live) {
		if (!containsPlayer(p)) {
			return;
		}

		this.getPlayerData(p).setLive(live);
	}

	/**
	 * Check player is live
	 * 
	 * @param p Target player
	 * @return True if player is live
	 */
	protected boolean isLive(Player p) {
		if (!containsPlayer(p)) {
			return false;
		}

		return this.getPlayerData(p).isLive();
	}

	/**
	 * Get live players list
	 * 
	 * @return Live players list
	 */
	protected List<Player> getLivePlayers() {
		List<Player> livePlayers = new ArrayList<Player>();
		for (Player p : this.getPlayers()) {
			if (this.getPlayerData(p).isLive()) {
				livePlayers.add(p);
			}
		}
		return livePlayers;
	}

	/**
	 * Get live players count
	 * 
	 * @return Live players count
	 */
	protected int getLivePlayersCount() {
		return this.getLivePlayers().size();
	}

	/**
	 * Check <b>live players</b> is bigger than <b>min player count</b> or equals
	 * 
	 * @return True if "live players" >= "min player count"
	 */
	protected boolean isMinPlayersLive() {
		return this.getLivePlayersCount() >= this.getMinPlayerCount();
	}

	/**
	 * Check <b>remain players</b> is bigger than <b>min player count</b>
	 * 
	 * @return "remain players" >= "min player count"
	 */
	protected boolean isMinPlayersLeft() {
		return this.getPlayerCount() >= this.getMinPlayerCount();
	}

	/*
	 * MiniGameSetting getters
	 */

	/**
	 * Shortcut method
	 * 
	 * @return Minigame title
	 */
	public String getTitle() {
		return this.getSetting().getTitle();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Colored minigame title
	 */
	public String getColoredTitle() {
		ChatColor minigameColor = (ChatColor) this.getCustomOption().get(Option.COLOR);
		return minigameColor + this.getTitle() + ChatColor.RESET;
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame playing location
	 */
	public Location getLocation() {
		return this.getSetting().getLocation().clone();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame waiting time
	 */
	public int getWaitingTime() {
		return this.getSetting().getWaitingTime();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame playing time limit
	 */
	public int getTimeLimit() {
		return this.getSetting().getTimeLimit();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame max player count
	 */
	public int getMinPlayerCount() {
		return this.getSetting().getMinPlayerCount();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame min player count
	 */
	public int getMaxPlayerCount() {
		return this.getSetting().getMaxPlayerCount();
	}

	/**
	 * Shortcut method
	 * 
	 * @return True if minigame is active
	 */
	public boolean isActive() {
		return this.getSetting().isActive();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame tutorial list
	 */
	public List<String> getTutorial() {
		return this.getSetting().getTutorial();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame custom data map
	 */
	public Map<String, Object> getCustomData() {
		return this.getSetting().getCustomData();
	}

	/**
	 * Get minigame title with class name
	 * 
	 * @return title with class name
	 */
	public String getTitleWithClassName() {
		return this.getTitle() + " [Class: " + this.getClassName() + "]";
	}

	/**
	 * Get just class name
	 * 
	 * @return Simple class name
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Get Minigame setting
	 * 
	 * @return setting
	 */
	public MiniGameSetting getSetting() {
		return this.setting;
	}

	/**
	 * Get left waiting time (sec)
	 * 
	 * @return Left waiting time
	 */
	public int getLeftWaitingTime() {
		return this.minigameTaskManager.getLeftWaitingTime();
	}

	/**
	 * Get left finish time (sec)
	 * 
	 * @return Left finish time
	 */
	public int getLeftFinishTime() {
		return this.minigameTaskManager.getLeftFinishTime();
	}

	/**
	 * Get task manager
	 * 
	 * @return Task manager
	 */
	protected TaskManager getTaskManager() {
		return this.minigameTaskManager.getTaskManager();
	}

	/**
	 * Get data manager
	 * 
	 * @return Data manager
	 */
	public MiniGameDataManager getDataManager() {
		return this.minigameDataManager;
	}

	/**
	 * Get custom option
	 * 
	 * @return Custom option
	 */
	public MiniGameCustomOption getCustomOption() {
		return this.customOption;
	}

	/**
	 * Get random player of all players
	 * 
	 * @return Random player
	 */
	protected Player randomPlayer() {
		int random = (int) (Math.random() * this.getPlayerCount());
		return this.getPlayers().get(random);
	}

	public List<MiniGameObserver> getObserverList() {
		return this.observerList;
	}

	/**
	 * Gets minigame type (e.g. "Solo", "SoloBattle", "Team", "TeamBattle")<br>
	 * Override this method for custom type<br>
	 * 
	 * @return Minigame type
	 * @see SoloMiniGame, SoloBattleMiniGame, TeamMiniGame, TeamBattleMiniGame
	 */
	public String getType() {
		return "MiniGame";
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
				return this.getClass().getSimpleName().equals(((MiniGame) obj).getClassName());
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
