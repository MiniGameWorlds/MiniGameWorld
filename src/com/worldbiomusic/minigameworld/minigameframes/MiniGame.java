package com.worldbiomusic.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.instance.TaskManager;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameEventPassEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameFinishEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGamePlayerExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameStartEvent;
import com.worldbiomusic.minigameworld.customevents.player.MiniGamePlayerJoinEvent;
import com.worldbiomusic.minigameworld.customevents.player.MiniGamePlayerLeaveEvent;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameDataManager;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRankResult;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting.GameFinishCondition;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameTaskManager;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameViewManager;
import com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard.MiniGameScoreboardManager;
import com.worldbiomusic.minigameworld.util.LangUtils;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

/**
 * <b>MiniGame class of all minigames</b> <br>
 * - Custom minigame frame can be made with extending this class<br>
 * - Message only send to same minigame players <br>
 */
public abstract class MiniGame {

	/**
	 * Basic data (e.g. title, location, play time ...)
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
	 * Whether game started or not
	 */
	private boolean started;

	/**
	 * Minigame player data (score, live)
	 */
	private List<MiniGamePlayerData> players;

	/**
	 * Viewers manager
	 */
	private MiniGameViewManager viewManager;

	/**
	 * Scoreboard manager
	 */
	private MiniGameScoreboardManager scoreboardManager;

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
	protected void handleGameException(MiniGameExceptionEvent exception) {
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
	 * Update(fix) scoreboard<br>
	 * Hook method
	 */
	public void updateScoreboard() {
	}

	/**
	 * Constructor with location
	 * 
	 * @param title          Used title in the server (different with class name)
	 * @param location       Playing location
	 * @param minPlayerCount Minimum player count to play
	 * @param maxPlayerCount Maximum player count to play
	 * @param playTime       Minigame playing time
	 * @param waitingTime    Waiting time before join minigame
	 */
	protected MiniGame(String title, Location location, int minPlayerCount, int maxPlayerCount, int playTime,
			int waitingTime) {
		this.setting = new MiniGameSetting(title, location, minPlayerCount, maxPlayerCount, playTime, waitingTime);

		// [must setup once]
		this.setupMiniGame();

		// Init settings on every start
		this.initBaseSettings();
	}

	/**
	 * Base constructor<br>
	 * Location set to default (Bukkit.getWorld("world"))
	 * 
	 * @param title          Used title in the server (different with class name)
	 * @param minPlayerCount Minimum player count to play
	 * @param maxPlayerCount Maximum player count to play
	 * @param playTime       Minigame playing time
	 * @param waitingTime    Waiting time before join minigame
	 */
	protected MiniGame(String title, int minPlayerCount, int maxPlayerCount, int playTime, int waitingTime) {
		this(title, new Location(Bukkit.getWorld("world"), 0, 4, 0), minPlayerCount, maxPlayerCount, playTime,
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

		this.minigameDataManager = new MiniGameDataManager(this);

		// register tutorial
		this.getSetting().setTutorial(this.registerTutorial());

		// register custom data
		this.registerCustomData();

		// custom option
		this.customOption = new MiniGameCustomOption(this);

		// setup view manager
		this.viewManager = new MiniGameViewManager(this);

		// setup scoreboard manager
		this.scoreboardManager = new MiniGameScoreboardManager(this);
		this.scoreboardManager.registerDefaultUpdaters();
	}

	/**
	 * Init(reset) minigame settings<br>
	 * <b>[IMPORTANT]</b><br>
	 * - Initialize settings on every start
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

		// init scoreboard
		this.scoreboardManager.setDefaultScoreboard();
	}

	/**
	 * Pass event to minigame after processing custom option, exception, chat event
	 * 
	 * @param event Passed event from "MiniGameManager" after check related with
	 *              this minigame
	 */
	public final void passEvent(Event event) {
		// pass event to custom option
		this.customOption.processEvent(event);

		// chat event
		if (event instanceof AsyncPlayerChatEvent) {
			this.processChat((AsyncPlayerChatEvent) event);
		}

		// process event when minigame started
		if (this.started) {
			// call pass event (only synchronous)
			if (!event.isAsynchronous()) {
				MiniGameEventPassEvent passEvent = new MiniGameEventPassEvent(this, event);
				Bukkit.getServer().getPluginManager().callEvent(passEvent);
				// check cancelled
				if (passEvent.isCancelled()) {
					return;
				}
			}

			// pass event to process
			this.processEvent(event);
		} else {
			processEventWhileWaiting(event);
		}
	}

	/**
	 * Processes events while players are waiting for start
	 * 
	 * @param event Passed Event
	 */
	private void processEventWhileWaiting(Event event) {
		if (event instanceof EntityDamageEvent) {
			// prevent player hurts
			EntityDamageEvent e = (EntityDamageEvent) event;
			e.setCancelled(true);
		} else if (event instanceof FoodLevelChangeEvent) {
			// prevent player hunger changes
			FoodLevelChangeEvent e = (FoodLevelChangeEvent) event;
			e.setCancelled(true);
		}
	}

	/**
	 * Send message to playing players only, if {@link Setting.ISOLATED_CHAT} is
	 * true<br>
	 * 
	 * @param e Chat event
	 */
	private void processChat(AsyncPlayerChatEvent e) {
		if (Setting.ISOLATED_CHAT) {
			Set<Player> recipients = e.getRecipients();
			recipients.removeAll(recipients.stream().filter(r -> !containsPlayer(r)).toList());
		}
	}

	/**
	 * Join player to minigame<br>
	 * [Check List]<br>
	 * 1. active is true<br>
	 * 2. started is false<br>
	 * 3. not full<br>
	 * Teleport player to minigame location
	 * 
	 * @param p Player who tries to join
	 * @return Result of try to join
	 */
	public boolean joinGame(Player p) {
		// call player join event
		MiniGamePlayerJoinEvent joinEvent = new MiniGamePlayerJoinEvent(this, p);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		// check event is cancelled
		if (joinEvent.isCancelled()) {
			return false;
		}

		// init setting when first player join
		if (this.isEmpty()) {
			this.initSettings();
			this.minigameTaskManager.runWaitingTask();
			this.scoreboardManager.startScoreboardUpdateTask();
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
		// add player to list
		addPlayer(p);

		// tp to game location
		// [IMPORTANT] must call after save player state (joinedLocation included)
		p.teleport(this.getLocation());

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
	public boolean leaveGame(Player p) {
		// call player leave event
		MiniGamePlayerLeaveEvent leaveEvent = new MiniGamePlayerLeaveEvent(this, p);
		Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
		// check event is cancelled
		if (leaveEvent.isCancelled()) {
			return false;
		}

		this.setupPlayerLeavingSettings(p, "Before start");

		return true;
	}

	/**
	 * Process when a player leaves the miniagme
	 * 
	 * @param p      Leaving player
	 * @param reason Leaving reason
	 */
	private void setupPlayerLeavingSettings(Player p, String reason) {
		if (reason != null) {
			String msg = p.getName() + " leaved " + this.getColoredTitle() + " (" + reason + ")";

			if (!isStarted()) {
				msg += " (" + getPlayerCount() + "/" + getMaxPlayerCount() + ")";
			}

			// notify other players to join the game
			if (Setting.ISOLATED_JOIN_QUIT_MESSAGE) {
				this.sendMessageToAllPlayers(msg);
			} else {
				Utils.sendMsgToEveryone(msg);
			}
		}

		// remove player from minigame
		this.removePlayer(p);

		// send title
		sendTitle(p, ChatColor.BOLD + "Leave", "");

		// check game is emtpy
		if (this.isEmpty()) {
			this.initBaseSettings();
		}
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
		int needPlayersCount = getMinPlayerCount() - getPlayerCount();
		String needPlayers = "";
		if (needPlayersCount > 0) {
			needPlayers = "need more " + ChatColor.RED + needPlayersCount + ChatColor.RESET + " players";
		}

		String msg = LangUtils.getMsg(p, LangUtils.path(MiniGame.class) + "join-message", false,
				new String[][] { { "player", p.getName() }, { "minigame", getColoredTitle() },
						{ "player-count", "" + getPlayerCount() }, { "max-player-count", "" + getMaxPlayerCount() } });
		msg += "\n" + needPlayers;

		if (Setting.ISOLATED_JOIN_QUIT_MESSAGE) {
			sendMessageToAllPlayers(msg);
		} else {
			Utils.sendMsgToEveryone(msg);
		}
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
		String ruleMsg = LangUtils.getMsg(p, LangUtils.path(MiniGame.class) + "rule", false, null);
		p.sendMessage("\n" + ChatColor.BOLD + "[" + ruleMsg + "]");

		LangUtils.sendMsg(p, LangUtils.path(MiniGame.class) + "play-time-in-rule", false,
				new String[][] { { "play-time", "" + getPlayTime() } });

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
		this.minigameTaskManager.cancelWaitingTask();
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

		// call start event
		MiniGameStartEvent startEvent = new MiniGameStartEvent(this);
		Bukkit.getPluginManager().callEvent(startEvent);
		// check start event is cancelled
		if (startEvent.isCancelled()) {
			// restart waiting task
			this.restartWaitingTask();

			return;
		}

		// start
		this.started = true;

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Setting.START_SOUND));

		// starting title
		sendTitleToAllPlayers("START", "", 4, 20 * 2, 4);

		// runTaskAfterStart
		runTaskAfterStart();

		// cancel task
		this.minigameTaskManager.cancelWaitingTask();

		// start finishsTimer
		this.minigameTaskManager.runFinishTask();
	}

	/**
	 * <b> [IMPORTANT] Use finishGame() for endpoint of a minigame, never run
	 * anything after finishGame()</b><br>
	 * Finish minigame<br>
	 * Notify FINISH to observers<br>
	 * 
	 * Execute "runTaskBeforeFinish()" at first<br>
	 * Execute "runTaskAfterFinish" at last
	 * 
	 */
	public void finishGame() {
		if (!isStarted()) {
			return;
		}

		// [IMPORTANT] stop all active tasks immediately after finish
		initTasks();

		// runTaskBeforeFinish
		runTaskBeforeFinish();

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Setting.FINISH_SOUND));

		printEndInfo();

		// save players for minigame finish event
		List<MiniGamePlayerData> leavingPlayers = new ArrayList<>(this.players);

		// setup player
		this.getPlayers().forEach(p -> this.setupPlayerLeavingSettings(p, null));

		// notify finish event to observers (after setup player leaving settings (e.g.
		// give reward(item) after state restored))

		// [IMPORTANT] restore removed leaving players's PlayerData for a while
		this.players.addAll(leavingPlayers);
		// call finish event
		Bukkit.getPluginManager().callEvent(new MiniGameFinishEvent(this));
		this.players.clear();

		// runTaskAfterFinish (before initSetting())
		runTaskAfterFinish();
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
	 * Print scores to all players<br>
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
	 * Get rank data
	 * 
	 * @return Ordered data
	 */
	public List<? extends MiniGameRankResult> getRank() {
		Collections.sort(this.players);
		return this.players;
	}

	/**
	 * - Handle exception<br>
	 * - If <b>player exception</b><br>
	 * - calls {@link handleGameException}<br>
	 * - player will leave the game <br>
	 * - checks {@link GameFinishCondition}<br>
	 * <br>
	 * - If <b>server exception</b><br>
	 * - minigame will be finished<br>
	 * 
	 * @param exception Minigame exception
	 * @see MiniGameExceptionEvent
	 */
	public final void handleException(MiniGameExceptionEvent exception) {
		if (exception instanceof MiniGamePlayerExceptionEvent) {
			MiniGamePlayerExceptionEvent e = (MiniGamePlayerExceptionEvent) exception;
			Player p = e.getPlayer();

			// debug
			Utils.debug(getTitleWithClassName() + " handles player exception (" + p.getName() + ")");
			Utils.debug("Reason: " + exception.getReason() + "\n");

			sendMessage(p, "Exception: " + exception.getReason());

			// check player is a viewer
			if (getViewManager().isViewing(p)) {
				getViewManager().handleException(exception);
				return;
			}

			// setup leaving settings
			this.setupPlayerLeavingSettings(p, exception.getReason());

			// pass exception
			if (isStarted()) {
				this.handleGameException(exception);
			}

			// check GameFinishExceptionMode
			this.checkGameFinishCondition();
		}

		// check event is minigame or server exception
		else {
			// debug
			Utils.debug(getTitleWithClassName() + " handles exception");
			Utils.debug("Reason: " + exception.getReason() + "\n");

			sendMessageToAllPlayers("Exception: " + exception.getReason());

			// check player is a viewer
			getViewManager().handleException(exception);

			// if started, finish game
			if (isStarted()) {
				// pass exception
				this.handleGameException(exception);
				// finish game
				finishGame();
			}
			// if not started, leave waiting players
			else {
				// setup leaving settings
				getPlayers().forEach(p -> setupPlayerLeavingSettings(p, exception.getReason()));
			}
		}
	}

	/**
	 * Check game finish condition
	 */
	public void checkGameFinishCondition() {
		GameFinishCondition condition = this.getSetting().getGameFinishCondition();
		boolean needToFinish = false;

		switch (condition) {
		case NONE:
			break;
		case LESS_THAN_PLAYERS_LIVE:
			needToFinish = isLessThanPlayersLive();
			break;
		case MORE_THAN_PLAYERS_LIVE:
			needToFinish = isMoreThanPlayersLive();
			break;
		case LESS_THAN_PLAYERS_LEFT:
			needToFinish = isLessThanPlayersLeft();
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
	 * Add player to minigame list<br>
	 * [IMPORTANT] Save(Store) player state
	 * 
	 * @param p Joined player
	 */
	private void addPlayer(Player p) {
		// register player (score: 0, live: true)
		this.players.add(new MiniGamePlayerData(this, p));
	}

	/**
	 * Remove player from minigame list<br>
	 * [IMPORTANT] Restore player state
	 * 
	 * @param p Leaved player
	 */
	private MiniGamePlayerData removePlayer(Player p) {
		MiniGamePlayerData pData = this.getPlayerData(p);
		// restore player state
		pData.getState().restorePlayerState();

		// remove from the list
		this.players.remove(pData);

		return pData;
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

//	public void sendMessage(Player p, String msg, boolean languageSupport) {
//		if (languageSupport) {
//			String msgKey = msg;
//			LangUtils.sendMsg(p, msgKey, false);
//		} else {
//			p.sendMessage("[" + this.getColoredTitle() + "] " + msg);
//		}
//	}

	/**
	 * Sendm message to all players with minigame title prefix
	 * 
	 * @param msg message
	 */
	public void sendMessageToAllPlayers(String msg) {
		this.getPlayers().forEach(p -> sendMessage(p, msg));
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
	 * Check {@link #getLivePlayersCount()} is less than
	 * {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 * 
	 * @return True if {@link #getLivePlayersCount()} is less than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isLessThanPlayersLive() {
		return getLivePlayersCount() < getSetting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Check {@link #getLivePlayersCount()} is more than
	 * {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 * 
	 * @return True if {@link #getLivePlayersCount()} is more than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isMoreThanPlayersLive() {
		return getLivePlayersCount() > getSetting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Check {@link #getPlayerCount()} is less than
	 * {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 * 
	 * @return True if {@link #getPlayerCount()} is less than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isLessThanPlayersLeft() {
		return this.getPlayerCount() < getSetting().getGameFinishConditionPlayerCount();
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
	 * @return Minigame play time
	 */
	public int getPlayTime() {
		return this.getSetting().getPlayTime();
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
	public TaskManager getTaskManager() {
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
	 * Get view manager
	 * 
	 * @return View manager
	 */
	public MiniGameViewManager getViewManager() {
		return this.viewManager;
	}

	/**
	 * Get scoreboard manager
	 * 
	 * @return Scoreboard manager
	 */
	public MiniGameScoreboardManager getScoreboardManager() {
		return this.scoreboardManager;
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

	/**
	 * Gets minigame frame type (e.g. "Solo", "SoloBattle", "Team",
	 * "TeamBattle")<br>
	 * Override this method for custom frame minigame class<br>
	 * 
	 * @return Minigame frame type
	 * @see SoloMiniGame
	 * @see SoloBattleMiniGame
	 * @see TeamMiniGame
	 * @see TeamBattleMiniGame
	 */
	public String getFrameType() {
		return "MiniGame";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof MiniGameAccessor) {
			return ((MiniGameAccessor) obj).equals(this);
		} else if (getClass() == obj.getClass()) {
			return this.getClassName().equals(((MiniGame) obj).getClassName());
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
