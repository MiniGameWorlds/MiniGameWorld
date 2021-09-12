package com.wbm.minigamemaker.games.frame;

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

import com.wbm.minigamemaker.manager.PlayerInvManager;
import com.wbm.minigamemaker.observer.MiniGameEventNotifier;
import com.wbm.minigamemaker.observer.MiniGameObserver;
import com.wbm.minigamemaker.util.Utils;
import com.wbm.minigamemaker.wrapper.MiniGameAccessor;
import com.wbm.minigamemaker.wrapper.MiniGameMaker;
import com.wbm.plugin.util.BukkitTaskManager;
import com.wbm.plugin.util.Counter;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.SortTool;

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

	// inv manager
	private PlayerInvManager invManager;

	// abstract methods
	protected abstract void initGameSetting();

	protected abstract void processEvent(Event event);

	protected abstract List<String> registerTutorial();

	// base constructor
	protected MiniGame(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.setting = new MiniGameSetting(title, location, maxPlayerCount, timeLimit, waitingTime);

		// [must setup once]
		this.setupMiniGame();

		// minigame setup
		this.initMiniGame();
	}

	// base location constructor
	protected MiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		this(title, new Location(Bukkit.getWorld("world"), 0, 4, 0), maxPlayerCount, timeLimit, waitingTime);
	}

	private void setupMiniGame() {
		this.taskManager = new BukkitTaskManager();
		this.observerList = new ArrayList<MiniGameObserver>();
		this.invManager = new PlayerInvManager();

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

		// stop all tasks
		this.taskManager.cancelAllTasks();

		// register task
		this.registerTasks();
		this.registerBasicTask();

		// timer counter
		this.waitingCounter = new Counter(this.getWaitingTime());
		this.finishCounter = new Counter(this.getTimeLimit());
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

				// 플레이어들에게 카운트 다운 타이틀
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
					// 10초 이하 남았을 때 알리기
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

	private void initSetting() {
		this.initMiniGame();

		// 하위 미니게임 세팅 값 설정
		this.initGameSetting();

	}

	public final void passEvent(Event event) {
		/*
		 * 넘겨받은 이벤트 처리
		 */

		// 미니게임 예외 이벤트인지 검사
		if (event instanceof PlayerQuitEvent) {
			this.handleException(((PlayerQuitEvent) event).getPlayer(), MiniGame.Exception.PLAYER_QUIT_SERVER, event);
		}
		// 게임이 시작됬을 때 미니게임에 이벤트 전달
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

		if (this.waitingCounter.getCount() <= 10) {
			this.sendMessage(p, "You can't leave game(Reason: game will start soon)");
			return false;
		}

		// leave
		this.setupPlayerLeavingSettings(p, "Before start");

		// check game is emtpy
		if (this.isEmpty()) {
			this.initSetting();
		}

		return true;
	}

	private void setupPlayerLeavingSettings(Player p, String reason) {
		/*
		 * TODO: After this method, isEmpty() must be checked and run initSetting() (except
		 * for runFinishTask())
		 */
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
		// 처리 순서
		// 1.minigames.yml에서 active가 true인지
		// 2.이미 게임이 시작전인지
		// 3.게임 인원이 풀이 아닌지
		// -> 게임 참여 로직 처리

		if (!this.isActive()) {
			this.sendMessage(p, " game is not active");
			return false;
		}

		if (this.started) {
			this.sendMessage(p, " already started");
			return false;
		}

		if (this.isFull()) {
			this.sendMessage(p, " Player is full");
			return false;
		}

		// 처음 들어온 사람일 경우 세팅초기화후에 타이머 시작
		if (this.isEmpty()) {
			this.initSetting();
			this.startWaitingTimer();
		}

		// player 세팅
		setupPlayerJoinSettings(p);

		// 성공적으로 joinGame
		return true;
	}

	private void setupPlayerJoinSettings(Player p) {
		/*
		 * initSetting 이미 했으므로 플레이어관련한것만 세팅
		 */
		// 인원에 추가
		this.addPlayer(p);

		// setup player
		this.setupPlayerWhenJoin(p);

		// info 전달
		this.notifyInfo(p);
	}

	private void notifyInfo(Player p) {
		// player에게 튜토리얼 전달
		this.printGameTutorial(p);

		// notify other players to join the game
		this.sendMessageToAllPlayers(p.getName() + " joined " + this.getTitle());
	}

	private void printGameTutorial(Player p) {
		/*
		 * 튜토리얼
		 */
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

	private void startWaitingTimer() {
		/*
		 * waitingTime동안 기다린 후에 게임 시작
		 */
		this.taskManager.runTaskTimer("_waitingTimer", 0, 20);
	}

	private void runStartTasks() {
		/*
		 * forcePlayerCount 검사: 현재 참여 플레이어수가 maxPlayerCount와 같지않으면 모든 플레이어 leaveGame()
		 * 실행
		 */
		if (this.getSetting().isForcePlayerCount()) {
			// check player isn't full
			if (!this.isFull()) {
				// send message
				this.sendMessageToAllPlayers("Game cancelled: needs full players");

				// leave all players
				this.getPlayers().forEach(p -> this.setupPlayerLeavingSettings(p, null));
				// init setting
				this.initSetting();

				return;
			}
		}

		// 활성화
		started = true;

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.BLOCK_END_PORTAL_SPAWN));

		// 시작 타이틀
		sendTitleToAllPlayers("START", "", 4, 20 * 2, 4);

		// runTaskAfterStart
		runTaskAfterStart();

		// notify start event to observers
		this.notifyObservers(MiniGameEvent.START);

		// 태스크 종료
		this.taskManager.cancelTask("_waitingTimer");

		// finishTimer 시작
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
		// 이 다음에 runTaskAfterFinish()가 실행되기 떄문에, initSetting()하면 안됨!
		this.getPlayers().forEach(p -> this.setupPlayerLeavingSettings(p, null));

		// runTaskAfterFinish
		runTaskAfterFinish();

		// initSeting
		initSetting();

		// 태스크 종료
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

		// 종료 알리기
		sendTitleToAllPlayers("FINISH", "", 4, 20 * 2, 4);

		// print score
		printScore();
	}

	protected final void endGame() {
		this.runFinishTasks();
	}

	// 게임 유형에 따라 다르게 출력되야 할 필요 있음
	protected void printScore() {
		// 스코어 결과 score기준 내림차순으로 출력
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
		int currentPlayerCount = this.getPlayers().size();
		int maxPlayerCount = this.getMaxPlayerCount();
		return currentPlayerCount == maxPlayerCount;
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

	/*
	 * MiniGame의 players는 미니게임이 시작한 후부터 끝나기 전까지 모든 플레이어를 변하지 않고 끝까지 가지고 있는 변수여야 하기
	 * 때문에 addPlayer()와 removePlayer()를 private으로 선언한다
	 */
	private void addPlayer(Player p) {
		// 0점 으로 플레이어 등록
		this.players.put(p, 0);
	}

	private void removePlayer(Player p) {
		this.players.remove(p);
	}

	protected void sendMessage(Player p, String msg) {
		p.sendMessage("[" + this.getTitle() + "] " + msg);
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
		// scoreNotifying 메세지 전송
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
		// scoreNotifying 메세지 전송
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

	public final void handleException(Player p, Exception exception, Object arg) {
		/*
		 * [예외 상황 관리]
		 * 
		 * - 플레이어가 미니게임에서 예외상황으로 인해 게임 플레이가 불가능 할 떄 호출되는 메서드
		 * - 마지막에 각 게임에게 예외 발생 매소드로 알림
		 *
		 * # 종류
		 * - PlayerQuitEvent에서 Reason 체크
		 * - 게임도중 서버 나가는 예외 처리
		 * 
		 */

		// info
		Utils.log("[" + p.getName() + "] handle exception: " + exception.name());

		// PlayerQuitServer일 때 이유 출력
		if (exception == Exception.PLAYER_QUIT_SERVER) {
			PlayerQuitEvent event = (PlayerQuitEvent) arg;
			Utils.log("Quit: " + event.getReason().name());
		}

		// 플레이중인 미니게임에게 예외 발생 매소드로 처리 알림 (예. task 중지, inv 초기화 등)
		this.handleGameException(p, exception, arg);

		// setup leaving settings
		this.setupPlayerLeavingSettings(p, exception.name());

		// check forcePlayerCount
		if (this.getSetting().isForcePlayerCount()) {
			// send message
			this.sendMessageToAllPlayers("Game end: game needs full players");

			// 모든 사람 강퇴
			this.getPlayers().forEach(other -> this.setupPlayerLeavingSettings(other, null));
		}

		// 미니게임에 남은 사람이 없으면 미니게임 세팅 초기화
		if (this.isEmpty()) {
			this.initSetting();
		}
	}

	private void setupPlayerWhenJoin(Player p) {
		// 게임룸 위치로 tp
		p.teleport(this.getLocation());

		// 1. save player inventory
		// 2. clear player inventory
		this.invManager.savePlayerInv(p);
		p.getInventory().clear();

		// 플레이어 상태 초기화
		PlayerTool.makePureState(p);
	}

	private void setupPlayerWhenLeave(Player p) {
		// player tp
		Location serverSpawn = MiniGameMaker.create().getServerSpawn();
		p.teleport(serverSpawn);

		// 1. clear player inventory
		// 2. restore player inventory
		p.getInventory().clear();
		this.invManager.restorePlayerInv(p);

		// 플레이어 상태 초기화
		PlayerTool.makePureState(p);
	}

	/*
	 * 자주 쓰이는 세팅값은 getter 추가
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

	protected void checkAttributes() {
		/*
		 * frame MiniGame class에서 조건 추가적으로 검사, final로 선언
		 */
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
		// 기다리는 남은 시간 리턴 (sec)
		return this.waitingCounter.getCount();
	}

	public int getLeftFinishTime() {
		// 끝나기까지 남은 시간 리턴 (sec)
		return this.finishCounter.getCount();
	}

	public String getEveryoneName() {
		String members = "";
		for (Player p : this.getPlayers()) {
			members += p.getName() + ", ";
		}
		// 마지막 ", " 제거
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
