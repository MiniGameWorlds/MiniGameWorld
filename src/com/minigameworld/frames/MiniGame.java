package com.minigameworld.frames;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.events.minigame.MiniGameExceptionEvent;
import com.minigameworld.events.minigame.MiniGameFinishEvent;
import com.minigameworld.events.minigame.MiniGamePlayerExceptionEvent;
import com.minigameworld.events.minigame.MiniGameStartEvent;
import com.minigameworld.events.minigame.player.MiniGamePlayerJoinEvent;
import com.minigameworld.events.minigame.player.MiniGamePlayerLeaveEvent;
import com.minigameworld.frames.helpers.LocationManager;
import com.minigameworld.frames.helpers.MiniGameCustomOption;
import com.minigameworld.frames.helpers.MiniGameCustomOption.Option;
import com.minigameworld.frames.helpers.MiniGameDataManager;
import com.minigameworld.frames.helpers.MiniGameInventoryManager;
import com.minigameworld.frames.helpers.MiniGamePlayer;
import com.minigameworld.frames.helpers.MiniGameRank;
import com.minigameworld.frames.helpers.MiniGameSetting;
import com.minigameworld.frames.helpers.MiniGameSetting.GameFinishCondition;
import com.minigameworld.frames.helpers.MiniGameTaskManager;
import com.minigameworld.frames.helpers.MiniGameViewManager;
import com.minigameworld.frames.helpers.scoreboard.MiniGameScoreboardManager;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.event.GameEventListener;
import com.minigameworld.managers.party.Party;
import com.minigameworld.util.LangUtils;
import com.minigameworld.util.Messenger;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.ParticleTool;
import com.wbm.plugin.util.SoundTool;
import com.wbm.plugin.util.instance.TaskManager;

/**
 * <b>MiniGame class of all minigames</b> <br>
 * - Custom minigame frame can be made with extending this class<br>
 * - Message only send to same minigame players <br>
 */
public abstract class MiniGame implements GameEventListener {

	/**
	 * Basic data (e.g. title, location, play time ...)
	 */
	private MiniGameSetting setting;

	/**
	 * Settind data manager with config (using YamlManager)
	 */
	private MiniGameDataManager dataManager;

	/**
	 * options like BLOCK_BREAK, INVENTORY_SAVE (nested in custom data section)
	 */
	private MiniGameCustomOption customOption;

	/**
	 * Task manager
	 */
	private MiniGameTaskManager taskManager;

	/**
	 * Minigame player data (score, live)
	 */
	private List<MiniGamePlayer> players;

	/**
	 * Viewers manager
	 */
	private MiniGameViewManager viewManager;

	/**
	 * Scoreboard manager
	 */
	private MiniGameScoreboardManager scoreboardManager;

	/**
	 * Invenory manager
	 */
	private MiniGameInventoryManager invManager;

	/**
	 * Location manager (+ world)
	 */
	private LocationManager locationManager;

	/**
	 * Language messenger
	 */
	protected Messenger messenger;

	/**
	 * Register minigame tutorial
	 */
	protected abstract List<String> tutorial();

	/**
	 * Executed every time when game starts
	 */
	protected void initGame() {
	};

	/**
	 * Executed immediately after game started
	 */
	protected void onStart() {
	}

	/**
	 * Executed when finish delay starts
	 */
	protected void onFinishDelay() {
	}

	/**
	 * Executed after finish delay end and before the game finished (players
	 * remains)
	 */
	protected void onFinish() {
	}

	/**
	 * Called after the player joined after isolated from the outside<br>
	 * [IMPORTANT] Player state is already saved and now pure state
	 * 
	 * @param p Joined player
	 */
	public void onJoin(Player p) {
	}

	/**
	 * Called when the player leave this game before start<br>
	 * [IMPORTANT] Player state is still isolated from the outside (player state
	 * will be restored after this method)<br>
	 * [IMPORTANT] Only called before the game starts
	 * 
	 * 
	 * @param p Leaving player
	 */
	public void onLeave(Player p) {
	}

	/**
	 * Called after the player enter viewing after isolated from the outside<br>
	 * [IMPORTANT] Player state is already saved and now pure state
	 * 
	 * @param p Viewing player
	 */
	public void onView(Player p) {
	}

	/**
	 * Called before the player unview this game<br>
	 * [IMPORTANT] Player state is still isolated from the outside (player state
	 * will be restored after this method)
	 * 
	 * @param p Unviewing player
	 */
	public void onUnview(Player p) {
	}

	/**
	 * Called only in play state and when exception is passed<br>
	 * {@link Exception}
	 */
	protected void onException(MiniGameExceptionEvent exception) {
	}

	/**
	 * Init custom data with this method
	 */
	protected void initCustomData() {
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
	 * @param title       Used title in the server (different with class name)
	 * @param location    Playing location
	 * @param minPlayers  Minimum player count to play
	 * @param maxPlayers  Maximum player count to play
	 * @param playTime    Minigame playing time
	 * @param waitingTime Waiting time before join minigame
	 */
	protected MiniGame(String title, Location location, int minPlayers, int maxPlayers, int playTime, int waitingTime) {
		this.setting = new MiniGameSetting(title, location, minPlayers, maxPlayers, playTime, waitingTime);

		// [must setup once]
		this.setupMiniGame();
	}

	/**
	 * Base constructor<br>
	 * Location set to default (Bukkit.getWorld("world"))
	 * 
	 * @param title       Used title in the server (different with class name)
	 * @param minPlayers  Minimum player count to play
	 * @param maxPlayers  Maximum player count to play
	 * @param playTime    Minigame playing time
	 * @param waitingTime Waiting time before join minigame
	 */
	protected MiniGame(String title, int minPlayers, int maxPlayers, int playTime, int waitingTime) {
		this(title, Utils.getDefaultLocation(), minPlayers, maxPlayers, playTime, waitingTime);
	}

	/**
	 * Setup minigame just once
	 */
	private void setupMiniGame() {
		// setup player list
		this.players = new ArrayList<MiniGamePlayer>();

		// messenger
		this.messenger = new Messenger(LangUtils.path(MiniGame.class));

		// register basic tasks
		this.taskManager = new MiniGameTaskManager(this);
		this.taskManager.registerBasicTasks();
		this.dataManager = new MiniGameDataManager(this);
		this.locationManager = new LocationManager(this);

		// register tutorial
		setting().setTutorial(this.tutorial());

		// register custom data
		initCustomData();

		// custom option
		this.customOption = new MiniGameCustomOption(this);

		// setup view manager
		this.viewManager = new MiniGameViewManager(this);

		// setup inventory manager
		this.invManager = new MiniGameInventoryManager(this);

		// setup scoreboard manager
		this.scoreboardManager = new MiniGameScoreboardManager(this);
		this.scoreboardManager.registerDefaultUpdaters();
	}

	/**
	 * Init(reset) minigame settings<br>
	 * <b>[IMPORTANT]</b><br>
	 * - Initialize settings on every start
	 */
	public void initSettings() {
		initBaseSettings();

		// init implemented minigame setting values
		initGame();
	}

	/**
	 * Init(reset) base settings
	 */
	private void initBaseSettings() {
		this.players.clear();

		this.initTasks();

		// init scoreboard
		this.scoreboardManager.setDefaultScoreboard();
	}

	/*
	 * All time handlers
	 * - MiniGameCustomOption
	 * - InventoryManager
	 */

	/**
	 * Join player to minigame<br>
	 * 
	 * @param p Player who tries to join
	 * @return Result of try to join
	 */
	public boolean joinGame(Player p) {
		// call player join event (check event is cancelled)
		if (Utils.callEvent(new MiniGamePlayerJoinEvent(this, p))) {
			return false;
		}

		// init setting when first player join
		if (isEmpty()) {
			initSettings();
			this.taskManager.runWaitingTask();
			this.scoreboardManager.startScoreboardUpdateTask();
			this.locationManager.init();
		}

		// setup join settings
		onPlayerJoin(p);

		/* hook method
		 [IMPORTANT] must be called after the player state is isolated from the outside */
		onJoin(p);

		// joined
		return true;
	}

	/**
	 * Setup player join settings
	 * 
	 * @param p Joined player
	 */
	private void onPlayerJoin(Player p) {
		// add player to list
		addPlayer(p);

		// sound
		playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT);

		// notify info
		notifyInfo(p);
	}

	/**
	 * When a player tries to leave minigame <b>before start</b><br>
	 * [IMPORTANT] This method must be called by the
	 * {@link MiniGameManager#leaveGame(Player)} ONLY. (If player needs to be left
	 * in this MiniGame class, use {@link #onPlayerLeave(Player, String)})<br>
	 * 
	 * 
	 * @param p leaving player
	 * @return Result of try to leave
	 */
	public boolean leaveGame(Player p) {
		// call player leave event (check event is cancelled)
		if (Utils.callEvent(new MiniGamePlayerLeaveEvent(this, p))) {
			return false;
		}

		/* hook method
		 [IMPORTANT] must be called when the player state is still isolated from the outside */
		onLeave(p);

		onPlayerLeave(p, messenger.getMsg(p, "before-start"));

		// notify message to party members
		Party party = MiniGameWorld.create(MiniGameWorld.API_VERSION).partyManager().getPlayerParty(p);
		if (party.getSize() > 1) {
			party.sendMessages(p.getName() + " left " + coloredTitle() + " minigame with party");
		}

		return true;
	}

	/**
	 * Process when a player leaves the miniagme
	 * 
	 * @param p      Leaving player
	 * @param reason for leaveing
	 */
	private void onPlayerLeave(Player p, String reason) {
		if (!containsPlayer(p)) {
			return;
		}

		if (reason != null) {
			String msg = this.messenger.getMsg(p, "leave-message",
					new String[][] { { "player", p.getName() }, { "minigame", coloredTitle() },
							{ "player-count", "" + playerCount() }, { "max-player-count", "" + maxPlayers() },
							{ "reason", reason } });

			// notify other players to join the game
			if (Setting.ISOLATED_JOIN_QUIT_MESSAGE) {
				sendMessages(msg);
			} else {
				Utils.sendMsgs(msg);
			}
		}

		// remove player from minigame
		removePlayer(p);

		// send title
		String leaveMsg = this.messenger.getMsg(p, "leave");
		sendTitle(p, ChatColor.BOLD + leaveMsg, "");

		// sound
		playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT);

		// check game is empty
		if (isEmpty()) {
			initBaseSettings();
		}
	}

	/**
	 * Notify infomations to player
	 * 
	 * @param p Player to notify
	 */
	private void notifyInfo(Player p) {
		// print tutorial
		printInfo(p);

		// notify all players to join the game
		int needPlayersCount = minPlayers() - playerCount();
		String needPlayers = "";
		if (needPlayersCount > 0) {
			needPlayers = this.messenger.getMsg(p, "need-players", new String[][] {
					{ "need-player-count", "" + ChatColor.RED + needPlayersCount + ChatColor.RESET } });
		}

		String msg = this.messenger.getMsg(p, "join-message",
				new String[][] { { "player", p.getName() }, { "minigame", coloredTitle() },
						{ "player-count", "" + playerCount() }, { "max-player-count", "" + maxPlayers() } });
		msg += "\n" + needPlayers;

		if (Setting.ISOLATED_JOIN_QUIT_MESSAGE) {
			sendMessages(msg);
		} else {
			Utils.sendMsgs(msg);
		}

		// notify message to party members
		Party party = MiniGameWorld.create(MiniGameWorld.API_VERSION).partyManager().getPlayerParty(p);
		if (party.getSize() > 1) {
			party.sendMessages(p.getName() + " joined " + coloredTitle() + " minigame with party");
		}
	}

	/**
	 * Print tutorial to player
	 * 
	 * @param p Player to print tutorial
	 */
	private void printInfo(Player p) {
		p.sendMessage("");
		p.sendMessage(ChatColor.GREEN + "=================================");
		p.sendMessage("" + ChatColor.BOLD + this.coloredTitle());

		// print rule
		String ruleMsg = this.messenger.getMsg(p, "rule");
		p.sendMessage("\n" + ChatColor.BOLD + "[" + ruleMsg + "]");

		p.sendMessage("- "
				+ this.messenger.getMsg(p, "play-time-in-rule", new String[][] { { "play-time", "" + playTime() } }));

		// tutorial
		if (tutorials() != null) {
			for (String msg : tutorials()) {
				p.sendMessage("- " + msg);
			}
		}

		p.sendMessage(ChatColor.GREEN + "=================================");
	}

	/**
	 * Cancel all tasks and timer count (waitingTime task, playTimer task)
	 */
	private void initTasks() {
		this.taskManager.init();
	}

	/**
	 * Restart waiting timer (Used when "waiting players count" is under "min player
	 * count")
	 */
	private void restartWaitingTask() {
		this.taskManager.cancelWaitingTask();
		this.taskManager.runWaitingTask();
	}

	/**
	 * Start minigame after check min player count<br>
	 * Notify START to observers<br>
	 * <br>
	 * Execute "onStart()"
	 */
	public boolean startGame() {
		// check min player count
		if (playerCount() < minPlayers()) {
			int needPlayerCount = minPlayers() - playerCount();
			// send message
			sendMessages(ChatColor.RED + "Game can not start");
			this.messenger.sendMsg(players(), "need-players",
					new String[][] { { "need-player-count", "" + ChatColor.RED + needPlayerCount + ChatColor.RESET } });

			// restart waiting task
			restartWaitingTask();

			return false;
		}

		// call start event (check event is cancelled)
		if (Utils.callEvent(new MiniGameStartEvent(this))) {
			// restart waiting task
			restartWaitingTask();
			return false;
		}

		// start
		setting().setStarted(true);
		setting().setStartTime(LocalDateTime.now());

		// play sound
		playSounds(Setting.START_SOUND);

		// starting title
		players().forEach(p -> {
			sendTitle(p, this.messenger.getMsg(p, "start"), "", 4, 20 * 2, 4);
		});

		// onStart
		onStart();

		// cancel task
		this.taskManager.cancelWaitingTask();

		// start finishsTimer
		this.taskManager.runFinishTask();

		return true;
	}


	/**
	 * <b> [IMPORTANT] Use finishGame() for endpoint of a minigame, never run
	 * anything after finishGame()</b><br>
	 * Finish minigame<br>
	 * Notify FINISH to observers<br>
	 * 
	 * Execute "onFinish()" at first<br>
	 * 
	 */
	public void finishGame() {
		if (!isStarted()) {
			return;
		}

		finishDelay();

		int finishDelay = isEmpty() ? 0 : setting().getFinishDelay();

		if (finishDelay == 0) {
			// [IMPORTANT] to call finish() immediately, do not use task with 0 tick but
			// just call the method
			finish();
		} else {
			// start finish delay
			// must call finish() if finishDelay is 0
			Bukkit.getScheduler().runTaskLater(MiniGameWorldMain.getInstance(), () -> finish(), 20 * finishDelay);
		}
	}

	private void finishDelay() {
		Utils.debug("1");
		setting().setStarted(false);

		// play sound
		playSounds(Setting.FINISH_SOUND);

		players().forEach(p -> p.setGameMode(GameMode.SPECTATOR));

		setting().setFinishTime(LocalDateTime.now());

		onFinishDelay();
		
		printEndInfo();

		// [IMPORTANT] call after onFinishDelay()
		initTasks();
		Utils.debug("11");
	}

	private void finish() {
		Utils.debug("2");
		// onFinish
		onFinish();

		// save players for minigame finish event
		List<MiniGamePlayer> leavingPlayers = new ArrayList<>(players);

		// setup player
		players().forEach(p -> onPlayerLeave(p, null));

		// notify finish event to observers (after setup player leaving settings (e.g.
		// give reward(item) after state restored))

		// [IMPORTANT] restore removed leaving players's PlayerData for a while
		players.addAll(leavingPlayers);

		// call finish event
		Utils.callEvent(new MiniGameFinishEvent(MiniGame.this, leavingPlayers));

		players.clear();

		// remove self instance
		MiniGameManager.getInstance().removeGameInstance(MiniGame.this);
		Utils.debug("22");
	}

	/**
	 * Print scores
	 */
	private void printEndInfo() {
		if (isEmpty()) {
			return;
		}

		// send finish title
		players().forEach(p -> {
			p.sendMessage("");
			p.sendMessage(ChatColor.RED + "=================================");
			p.sendMessage("" + ChatColor.BOLD + this.coloredTitle());
			p.sendMessage("");

			sendTitle(p, this.messenger.getMsg(p, "finish"), "", 4, 20 * 2, 4);
		});

		// print score
		printScores();

		players().forEach(p -> {
			p.sendMessage(ChatColor.RED + "=================================");
		});

		// finish delay
		sendMessages("You can leave " + coloredTitle() + " after " + ChatColor.GREEN + setting.getFinishDelay()
				+ ChatColor.RESET + " seconds");
	}

	/**
	 * Print scores to all players<br>
	 * Can print format differently depending on game type
	 */
	protected void printScores() {
		players().forEach(p -> p.sendMessage(ChatColor.BOLD + "[" + this.messenger.getMsg(p, "score") + "]"));

		@SuppressWarnings("unchecked")
		List<MiniGamePlayer> rankList = (List<MiniGamePlayer>) rank();
		int rank = 1;
		ChatColor[] rankColors = { ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE };

		for (MiniGamePlayer ranking : rankList) {
			Player p = ranking.getPlayer();
			int score = ranking.getScore();

			// rank string with color
			String rankString = "[";
			if (rank <= 3) {
				rankString += rankColors[rank - 1];
			}
			rankString += rank + "" + ChatColor.RESET + "] ";

			sendMessages(rankString + p.getName() + ": " + ChatColor.GOLD + score, false);
			rank += 1;
		}

	}

	/**
	 * Get rank data
	 * 
	 * @return Ordered data
	 */
	public List<? extends MiniGameRank> rank() {
		Collections.sort(this.players);
		return this.players;
	}

	/**
	 * - Handle exception<br>
	 * - If <b>player exception</b><br>
	 * - calls {@link #onException(MiniGameExceptionEvent)}<br>
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
			Utils.debug(titleWithClassName() + " handles player exception (" + p.getName() + ")");
			Utils.debug("Reason: " + exception.getReason() + "\n");

			sendMessage(p, this.messenger.getMsg(p, "exception") + ": " + exception.getReason());

			// check player is a viewer
			if (viewManager().isViewing(p)) {
				viewManager().handleException(exception);
				return;
			}

			// pass exception
			if (isStarted()) {
				onException(exception);

				if (playerCount() <= 1) {
					// If there is only 1 player or 0, there will be no player after exception
					// processed
					// So should finish the game
					setting().setFinishDelay(0); // set finish delay to 0 to finish immediately
					finishGame();
				} else {
					// check GameFinishExceptionMode
					checkGameFinishCondition();
				}
			}

			// setup leaving settings
			/* [IMPORTANT] must after checkGameFinishCondition() (beacause onFinishDelay() and 
			 * onFinish() has to process player before game leave
			 */
			onPlayerLeave(p, exception.getReason());

		}

		// if the event is minigame exception or server exception
		else {
			// debug
			Utils.debug(titleWithClassName() + " handles exception");
			Utils.debug("Reason: " + exception.getReason() + "\n");

			players()
					.forEach(p -> sendMessage(p, this.messenger.getMsg(p, "exception") + ": " + exception.getReason()));

			// check player is a viewer
			viewManager().handleException(exception);

			// if started, finish game
			if (isStarted()) {
				// pass exception
				onException(exception);

				// finish game
				setting().setFinishDelay(0); // set finish delay to 0 to finish immediately
				finishGame();
			}

			// leave players
			players().forEach(p -> onPlayerLeave(p, exception.getReason()));

			// remove self instance
			MiniGameManager.getInstance().removeGameInstance(this);
		}
	}

	/**
	 * Check game finish condition
	 */
	public void checkGameFinishCondition() {
		GameFinishCondition condition = setting().getGameFinishCondition();
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
		if (isEmpty()) {
			needToFinish = true;
		}

		if (needToFinish) {
			finishGame();
		}
	}

	/**
	 * Check minigame has players
	 * 
	 * @return True if no players in minigame
	 */
	public boolean isEmpty() {
		return this.players().isEmpty();
	}

	/**
	 * Check minigame is full of players
	 * 
	 * @return True if players = max player count
	 */
	public boolean isFull() {
		return this.playerCount() == this.maxPlayers();
	}

	/**
	 * Check player is playing this minigame (not check player is viewing)<br>
	 * To check player is viewing this minigame, get {@link #viewManager()} and use
	 * {@link MiniGameViewManager#isViewing(Player)}
	 * 
	 * @param p Target player
	 * @return True if player is playing minigame
	 */
	public boolean containsPlayer(Player p) {
		return gamePlayer(p) != null;
	}

	/**
	 * Get copied player list
	 * 
	 * @return Copied Player list
	 */
	public List<Player> players() {
		List<Player> players = new ArrayList<>();
		this.players.forEach(pData -> players.add(pData.getPlayer()));
		return players;
	}

	/**
	 * Get playing players count
	 * 
	 * @return playing players count
	 */
	public int playerCount() {
		return this.players().size();
	}

	/**
	 * Add player to minigame list<br>
	 * [IMPORTANT] Save(Store) player state
	 * 
	 * @param p Joined player
	 */
	private void addPlayer(Player p) {
		// register player (score: 0, live: true)
		this.players.add(new MiniGamePlayer(this, p));
	}

	/**
	 * Remove player from minigame list<br>
	 * [IMPORTANT] Restore player state
	 * 
	 * @param p leaving player
	 */
	private MiniGamePlayer removePlayer(Player p) {
		MiniGamePlayer pData = gamePlayer(p);
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
		sendMessage(p, msg, true);
	}

	public void sendMessage(Player p, String msg, boolean prefix) {
		if (prefix) {
			msg = "[" + this.coloredTitle() + "] " + msg;
		}

		p.sendMessage(msg);
	}

	/**
	 * Send message to all players with minigame title prefix
	 * 
	 * @param msg message
	 */
	public void sendMessages(String msg) {
		sendMessages(msg, true);
	}

	public void sendMessages(String msg, boolean prefix) {
		players().forEach(p -> sendMessage(p, msg, prefix));
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
		sendTitle(p, title, subTitle, 3, 14, 3);
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
	public void sendTitles(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		this.players().forEach(p -> this.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut));
	}

	/**
	 * Send title to all players with minigame title prefix with default time
	 * settings
	 * 
	 * @param title    Title string
	 * @param subTitle Subtitle string
	 */
	public void sendTitles(String title, String subTitle) {
		sendTitles(title, subTitle, 3, 14, 3);
	}

	/**
	 * Play sound to a player
	 * 
	 * @param p     Listener
	 * @param sound to play
	 */
	public void playSound(Player p, Sound sound) {
		SoundTool.play(p, sound);
	}

	/**
	 * Play sound to all players
	 * 
	 * @param sound to play
	 */
	public void playSounds(Sound sound) {
		players().forEach(p -> playSound(p, sound));
	}

	/**
	 * Spawn particle around of the player
	 * 
	 * @param p        Player
	 * @param particle Particle type
	 * @param count    particle count
	 * @param speed    particle spreading speed
	 */
	public void particle(Player p, Particle particle, int count, double speed) {
		ParticleTool.spawn(p.getLocation(), particle, count, speed);
	}

	/**
	 * Spawn particle around of all the players
	 * 
	 * @param particle Particle type
	 * @param count    particle count
	 * @param speed    particle spreading speed
	 */
	public void particles(Particle particle, int count, double speed) {
		players().forEach(p -> particle(p, particle, count, speed));
	}

	/**
	 * Spawn particles around of the location
	 * 
	 * @param loc      Particle spawn location
	 * @param particle Particle type
	 * @param count    particle count
	 * @param speed    particle spreading speed
	 */
	public void particle(Location loc, Particle particle, int count, double speed) {
		ParticleTool.spawn(loc, particle, count, speed);
	}

	/**
	 * Get PlayerData
	 * 
	 * @param p Target player
	 * @return PlayerData of p
	 */
	public MiniGamePlayer gamePlayer(Player p) {
		for (MiniGamePlayer pData : this.players) {
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
	public List<MiniGamePlayer> gamePlayers() {
		return this.players;
	}

	/**
	 * Get score of player
	 * 
	 * @param p Target player
	 * @return Player's score
	 */
	public int score(Player p) {
		return this.gamePlayer(p).getScore();
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

		MiniGamePlayer pData = this.gamePlayer(p);
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
		this.players().forEach(p -> this.plusScore(p, amount));
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

		MiniGamePlayer pData = this.gamePlayer(p);
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
		this.players().forEach(p -> this.minusScore(p, amount));
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

		this.gamePlayer(p).setLive(live);
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

		return this.gamePlayer(p).isLive();
	}

	/**
	 * Get live players list
	 * 
	 * @return Live players list
	 */
	protected List<Player> livePlayers() {
		List<Player> livePlayers = new ArrayList<Player>();
		for (Player p : this.players()) {
			if (this.gamePlayer(p).isLive()) {
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
	protected int livePlayersCount() {
		return this.livePlayers().size();
	}

	/**
	 * Check {@link #livePlayersCount()} is less than
	 * {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 * 
	 * @return True if {@link #livePlayersCount()} is less than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isLessThanPlayersLive() {
		return livePlayersCount() < setting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Check {@link #livePlayersCount()} is more than
	 * {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 * 
	 * @return True if {@link #livePlayersCount()} is more than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isMoreThanPlayersLive() {
		return livePlayersCount() > setting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Check {@link #playerCount()} is less than
	 * {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 * 
	 * @return True if {@link #playerCount()} is less than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isLessThanPlayersLeft() {
		return this.playerCount() < setting().getGameFinishConditionPlayerCount();
	}

	/*
	 * MiniGameSetting getters
	 */

	/**
	 * Shortcut method
	 * 
	 * @return Minigame title
	 */
	public String title() {
		return setting().getTitle();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Colored minigame title
	 */
	public String coloredTitle() {
		ChatColor minigameColor = (ChatColor) customOption().get(Option.COLOR);
		return minigameColor + this.title() + ChatColor.RESET;
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame playing location
	 */
	public Location location() {
		return setting().getLocation().clone();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame waiting time
	 */
	public int waitingTime() {
		return setting().getWaitingTime();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame play time
	 */
	public int playTime() {
		return setting().getPlayTime();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame max player count
	 */
	public int minPlayers() {
		return setting().getMinPlayers();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame min player count
	 */
	public int maxPlayers() {
		return setting().getMaxPlayers();
	}

	/**
	 * Shortcut method
	 * 
	 * @return True if minigame is active
	 */
	public boolean isActive() {
		return setting().isActive();
	}

	/**
	 * Shortcut method
	 * 
	 * @return minigame id
	 */
	public String id() {
		return setting().getId();
	}

	/**
	 * Shortcut method
	 * 
	 * @return True if minigame has started
	 */
	public boolean isStarted() {
		return setting().isStarted();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame tutorial list
	 */
	public final List<String> tutorials() {
		return setting().getTutorial();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame custom data map
	 */
	public Map<String, Object> customData() {
		return setting().getCustomData();
	}

	/**
	 * Get minigame title with class name
	 * 
	 * @return title with class name
	 */
	public String titleWithClassName() {
		return title() + " [Class: " + className() + "]";
	}

	/**
	 * Get just class name
	 * 
	 * @return Simple class name
	 */
	public String className() {
		return getClass().getSimpleName();
	}

	/**
	 * Get Minigame setting
	 * 
	 * @return setting
	 */
	public MiniGameSetting setting() {
		return this.setting;
	}

	/**
	 * Get left waiting time (sec)
	 * 
	 * @return Left waiting time
	 */
	public int leftWaitingTime() {
		return this.taskManager.getLeftWaitingTime();
	}

	/**
	 * Get left play time (sec)
	 * 
	 * @return Left play time
	 */
	public int leftPlayTime() {
		return this.taskManager.getLeftPlayTime();
	}

	/**
	 * Get task manager
	 * 
	 * @return Task manager
	 */
	public TaskManager taskManager() {
		return this.taskManager.getTaskManager();
	}

	/**
	 * Get data manager
	 * 
	 * @return Data manager
	 */
	public MiniGameDataManager dataManager() {
		return this.dataManager;
	}

	/**
	 * Get location manager
	 * 
	 * @return Location manager
	 */
	public LocationManager locationManager() {
		return this.locationManager;
	}

	/**
	 * Get custom option
	 * 
	 * @return Custom option
	 */
	public MiniGameCustomOption customOption() {
		return this.customOption;
	}

	/**
	 * Get view manager
	 * 
	 * @return View manager
	 */
	public MiniGameViewManager viewManager() {
		return this.viewManager;
	}

	/**
	 * Get scoreboard manager
	 * 
	 * @return Scoreboard manager
	 */
	public MiniGameScoreboardManager scoreboardManager() {
		return this.scoreboardManager;
	}

	/**
	 * Get inventory manager
	 * 
	 * @return Inventory manager
	 */
	public MiniGameInventoryManager inventoryManager() {
		return this.invManager;
	}

	/**
	 * Get random player of all players
	 * 
	 * @return Random player
	 */
	protected Player randomPlayer() {
		int random = (int) (Math.random() * this.playerCount());
		return this.players().get(random);
	}

	/**
	 * Get player who has the highest score of all
	 * 
	 * @return Null if there are no players
	 */
	protected Player topPlayer() {
		List<MiniGamePlayer> sortedPlayers = gamePlayers().stream()
				.sorted(Comparator.comparing(MiniGamePlayer::getScore).reversed()).toList();
		if (sortedPlayers.isEmpty()) {
			return null;
		}

		return sortedPlayers.get(0).getPlayer();
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
	public String frameType() {
		return "MiniGame";
	}

	/**
	 * Check two games drived from the same template game or not
	 * 
	 * @param game Checking game
	 * @return True if two games are derived from the same template game
	 */
	public boolean isSameTemplate(MiniGame game) {
		return getClass().getName().equals(game.getClass().getName());
	}

	/**
	 * Compare with {@link #isSameTemplate(MiniGame)} and
	 * {@link MiniGameSetting#getId()}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof MiniGameAccessor) {
			return equals(((MiniGameAccessor) obj).minigame());
		} else if (getClass() == obj.getClass()) {
			MiniGame other = (MiniGame) obj;
			return isSameTemplate(other) && id().equals(other.id());
		}
		return false;
	}

	@Override
	public MiniGame minigame() {
		return this;
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
