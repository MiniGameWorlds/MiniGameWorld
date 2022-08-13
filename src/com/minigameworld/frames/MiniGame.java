package com.minigameworld.frames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.api.MwUtil;
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
import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.managers.event.GameEvent.State;
import com.minigameworld.managers.event.GameEventListener;
import com.minigameworld.managers.party.Party;
import com.minigameworld.util.LangUtils;
import com.minigameworld.util.Messenger;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.PlayerTool;
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
	 * Executed before game finishes (players remains)
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
	 * Executed when Minigame exception created<br>
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
		this.getSetting().setTutorial(this.tutorial());

		// register custom data
		this.initCustomData();

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
		this.initBaseSettings();

		// init implemented minigame setting values
		initGame();
	}

	/**
	 * Init(reset) base settings
	 */
	private void initBaseSettings() {
		getSetting().setStarted(false);

		this.players.clear();

		this.initTasks();

		// init scoreboard
		this.scoreboardManager.setDefaultScoreboard();
	}

	/*
	 * Waiting handlers
	 * - onEntityDamaged()
	 * - onFoodLevelChange()
	 */
	@GameEvent(state = State.WAIT)
	protected void onPlayerDamaged(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@GameEvent(state = State.WAIT)
	protected void onPlayerHungerChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	/*
	 * All time handlers
	 * - onChat()
	 * - MiniGameCustomOption
	 * - InventoryManager
	 */
	/**
	 * Send message to playing players only, if {@link Setting.ISOLATED_CHAT} is
	 * true<br>
	 * 
	 * @param e Chat event
	 */
	@GameEvent(state = State.ALL)
	protected void onChat(AsyncPlayerChatEvent e) {
		if (Setting.ISOLATED_CHAT) {
			// send chat message to the same game players and also viewers only
			Set<Player> recipients = e.getRecipients();
			recipients.removeAll(recipients.stream().filter(r -> !MwUtil.isInGame(r)).toList());
		}
	}

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
		Party party = MiniGameWorld.create(MiniGameWorld.API_VERSION).getPartyManager().getPlayerParty(p);
		if (party.getSize() > 1) {
			party.sendMessages(p.getName() + " left " + getColoredTitle() + " minigame with party");
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
		if (reason != null) {
			String msg = this.messenger.getMsg(p, "leave-message",
					new String[][] { { "player", p.getName() }, { "minigame", getColoredTitle() },
							{ "player-count", "" + getPlayerCount() }, { "max-player-count", "" + getMaxPlayers() },
							{ "reason", reason } });

			// notify other players to join the game
			if (Setting.ISOLATED_JOIN_QUIT_MESSAGE) {
				sendMessages(msg);
			} else {
				Utils.sendMsgToEveryone(msg);
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
		int needPlayersCount = getMinPlayers() - getPlayerCount();
		String needPlayers = "";
		if (needPlayersCount > 0) {
			needPlayers = this.messenger.getMsg(p, "need-players", new String[][] {
					{ "need-player-count", "" + ChatColor.RED + needPlayersCount + ChatColor.RESET } });
		}

		String msg = this.messenger.getMsg(p, "join-message",
				new String[][] { { "player", p.getName() }, { "minigame", getColoredTitle() },
						{ "player-count", "" + getPlayerCount() }, { "max-player-count", "" + getMaxPlayers() } });
		msg += "\n" + needPlayers;

		if (Setting.ISOLATED_JOIN_QUIT_MESSAGE) {
			sendMessages(msg);
		} else {
			Utils.sendMsgToEveryone(msg);
		}

		// notify message to party members
		Party party = MiniGameWorld.create(MiniGameWorld.API_VERSION).getPartyManager().getPlayerParty(p);
		if (party.getSize() > 1) {
			party.sendMessages(p.getName() + " joined " + getColoredTitle() + " minigame with party");
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
		p.sendMessage("" + ChatColor.BOLD + this.getColoredTitle());

		// print rule
		String ruleMsg = this.messenger.getMsg(p, "rule");
		p.sendMessage("\n" + ChatColor.BOLD + "[" + ruleMsg + "]");

		p.sendMessage("- " + this.messenger.getMsg(p, "play-time-in-rule",
				new String[][] { { "play-time", "" + getPlayTime() } }));

		// tutorial
		if (getTutorial() != null) {
			for (String msg : this.getTutorial()) {
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
		if (getPlayerCount() < getMinPlayers()) {
			int needPlayerCount = getMinPlayers() - getPlayerCount();
			// send message
			sendMessages(ChatColor.RED + "Game can not start");
			this.messenger.sendMsg(getPlayers(), "need-players",
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
		getSetting().setStarted(true);

		// play sound
		getPlayers().forEach(p -> PlayerTool.playSound(p, Setting.START_SOUND));

		// starting title
		getPlayers().forEach(p -> {
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

		// [IMPORTANT] stop all active tasks immediately after finish
		initTasks();

		// onFinish
		onFinish();

		// play sound
		getPlayers().forEach(p -> PlayerTool.playSound(p, Setting.FINISH_SOUND));

		printEndInfo();

		// save players for minigame finish event
		List<MiniGamePlayer> leavingPlayers = new ArrayList<>(this.players);

		// setup player
		getPlayers().forEach(p -> onPlayerLeave(p, null));

		// notify finish event to observers (after setup player leaving settings (e.g.
		// give reward(item) after state restored))

		// [IMPORTANT] restore removed leaving players's PlayerData for a while
		this.players.addAll(leavingPlayers);

		// call finish event
		Utils.callEvent(new MiniGameFinishEvent(this, leavingPlayers));

		this.players.clear();

		// remove self instance
		MiniGameManager.getInstance().removeGameInstance(this);
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
		getPlayers().forEach(p -> {
			sendTitle(p, this.messenger.getMsg(p, "finish"), "", 4, 20 * 2, 4);
		});

		// print score
		printScores();

		this.getPlayers().forEach(p -> p.sendMessage(ChatColor.RED + "================================="));
	}

	/**
	 * Print scores to all players<br>
	 * Can print format differently depending on game type
	 */
	protected void printScores() {
		getPlayers().forEach(p -> p.sendMessage(ChatColor.BOLD + "[" + this.messenger.getMsg(p, "score") + "]"));

		@SuppressWarnings("unchecked")
		List<MiniGamePlayer> rankList = (List<MiniGamePlayer>) this.getRank();
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
	public List<? extends MiniGameRank> getRank() {
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
			Utils.debug(getTitleWithClassName() + " handles player exception (" + p.getName() + ")");
			Utils.debug("Reason: " + exception.getReason() + "\n");

			sendMessage(p, this.messenger.getMsg(p, "exception") + ": " + exception.getReason());

			// check player is a viewer
			if (getViewManager().isViewing(p)) {
				getViewManager().handleException(exception);
				return;
			}

			// setup leaving settings
			this.onPlayerLeave(p, exception.getReason());

			// pass exception
			if (isStarted()) {
				this.onException(exception);

				// check GameFinishExceptionMode
				this.checkGameFinishCondition();
			}
		}

		// if the event is minigame exception or server exception
		else {
			// debug
			Utils.debug(getTitleWithClassName() + " handles exception");
			Utils.debug("Reason: " + exception.getReason() + "\n");

			getPlayers()
					.forEach(p -> sendMessage(p, this.messenger.getMsg(p, "exception") + ": " + exception.getReason()));

			// check player is a viewer
			getViewManager().handleException(exception);

			// if started, finish game
			if (isStarted()) {
				// pass exception
				onException(exception);

				// finish game
				finishGame();
			}

			// [IMPORTANT] don't use finishGame() here
			// leave players
			getPlayers().forEach(p -> onPlayerLeave(p, exception.getReason()));

			// remove self instance
			MiniGameManager.getInstance().removeGameInstance(this);
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
			finishGame();
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
		return this.getPlayerCount() == this.getMaxPlayers();
	}

	/**
	 * Check player is playing this minigame (not check player is viewing)<br>
	 * To check player is viewing this minigame, get {@link #getViewManager()} and
	 * use {@link MiniGameViewManager#isViewing(Player)}
	 * 
	 * @param p Target player
	 * @return True if player is playing minigame
	 */
	public boolean containsPlayer(Player p) {
		return this.getGamePlayer(p) != null;
	}

	/**
	 * Get copied player list
	 * 
	 * @return Copied Player list
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
		MiniGamePlayer pData = this.getGamePlayer(p);
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
			msg = "[" + this.getColoredTitle() + "] " + msg;
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
		getPlayers().forEach(p -> sendMessage(p, msg, prefix));
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
		sendTitle(p, title, subTitle, 4, 12, 4);
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
		this.getPlayers().forEach(p -> this.sendTitle(p, title, subTitle, fadeIn, stay, fadeOut));
	}

	/**
	 * Send title to all players with minigame title prefix with default time
	 * settings
	 * 
	 * @param title    Title string
	 * @param subTitle Subtitle string
	 */
	public void sendTitles(String title, String subTitle) {
		sendTitles(title, subTitle, 4, 12, 4);
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
		getPlayers().forEach(p -> playSound(p, sound));
	}

	/**
	 * Get PlayerData
	 * 
	 * @param p Target player
	 * @return PlayerData of p
	 */
	public MiniGamePlayer getGamePlayer(Player p) {
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
	public List<MiniGamePlayer> getGamePlayers() {
		return this.players;
	}

	/**
	 * Get score of player
	 * 
	 * @param p Target player
	 * @return Player's score
	 */
	public int getScore(Player p) {
		return this.getGamePlayer(p).getScore();
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

		MiniGamePlayer pData = this.getGamePlayer(p);
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

		MiniGamePlayer pData = this.getGamePlayer(p);
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

		this.getGamePlayer(p).setLive(live);
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

		return this.getGamePlayer(p).isLive();
	}

	/**
	 * Get live players list
	 * 
	 * @return Live players list
	 */
	protected List<Player> getLivePlayers() {
		List<Player> livePlayers = new ArrayList<Player>();
		for (Player p : this.getPlayers()) {
			if (this.getGamePlayer(p).isLive()) {
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
	public int getMinPlayers() {
		return this.getSetting().getMinPlayers();
	}

	/**
	 * Shortcut method
	 * 
	 * @return Minigame min player count
	 */
	public int getMaxPlayers() {
		return this.getSetting().getMaxPlayers();
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
	 * @return minigame id
	 */
	public String id() {
		return this.getSetting().getId();
	}

	/**
	 * Shortcut method
	 * 
	 * @return True if minigame has started
	 */
	public boolean isStarted() {
		return getSetting().isStarted();
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
		return this.taskManager.getLeftWaitingTime();
	}

	/**
	 * Get left play time (sec)
	 * 
	 * @return Left play time
	 */
	public int getLeftPlayTime() {
		return this.taskManager.getLeftPlayTime();
	}

	/**
	 * Get task manager
	 * 
	 * @return Task manager
	 */
	public TaskManager getTaskManager() {
		return this.taskManager.getTaskManager();
	}

	/**
	 * Get data manager
	 * 
	 * @return Data manager
	 */
	public MiniGameDataManager getDataManager() {
		return this.dataManager;
	}

	/**
	 * Get location manager
	 * 
	 * @return Location manager
	 */
	public LocationManager getLocationManager() {
		return this.locationManager;
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
	 * Get inventory manager
	 * 
	 * @return Inventory manager
	 */
	public MiniGameInventoryManager getInventoryManager() {
		return this.invManager;
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
	 * Get player who has the highest score of all
	 * 
	 * @return Null if there are no players
	 */
	protected Player topPlayer() {
		List<MiniGamePlayer> sortedPlayers = getGamePlayers().stream()
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
	public String getFrameType() {
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
