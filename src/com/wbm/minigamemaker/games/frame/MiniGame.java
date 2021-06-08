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

import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.minigamemaker.manager.RankManager;
import com.wbm.minigamemaker.util.BukkitTaskManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.Counter;
import com.wbm.plugin.util.PlayerTool;

public abstract class MiniGame {
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

	// rank manager
	protected RankManager rankM;

	// task manager
	private BukkitTaskManager taskManager;

	// abstract methods
	protected abstract void initGameSetting();

	protected abstract void processEvent(Event event);

	protected abstract List<String> getGameTutorialStrings();

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
		// 랭크 매니저
		this.rankM = new RankManager();
		this.taskManager = new BukkitTaskManager();
	}

	// 구현 선택사항 메소드들
//	protected void runTaskBeforeStart() {
	// 사용 필요 x
//	};

	protected void runTaskAfterStart() {
	};

	protected void runTaskBeforeFinish() {
	};

	protected void runTaskAfterFinish() {
	};

	protected void handleGameExeption(Player p, Exception exception, Object arg) {
	};

	protected void registerTasks() {
	}

	private void initMiniGame() {
		this.started = false;
		if (this.players == null) {
			this.players = new HashMap<>();
		} else {
			this.players.clear();
		}

		// taskManager의 모든 task stop
		this.taskManager.cancelAllTasks();

		// register task
		this.registerBasicTask();
		this.registerTasks();

		// timer counter
		this.waitingCounter = new Counter(this.getWaitingTime());
		this.finishCounter = new Counter(this.getTimeLimit());
	}

	private void registerBasicTask() {
		// register waitingTimer task to taskManager
		this.taskManager.registerTask("_waitingTimer", new BukkitRunnable() {

			@Override
			public void run() {
				// 카운트다운 끝
				int waitTime = waitingCounter.getCount();
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
				sendTitleToPlayers(time, "", 4, 12, 4);

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
					sendTitleToPlayers("" + time, "", 4, 12, 4);

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

	public void passEvent(Event event) {
		/*
		 * 넘겨받은 이벤트 처리
		 */

		// 미니게임 탈주 이벤트인지 검사
		if (event instanceof PlayerQuitEvent) {
			this.handleException(((PlayerQuitEvent) event).getPlayer(), MiniGame.Exception.PlayerQuitServer, event);
		}
		// 게임이 시작됬을 때 미니게임에 이벤트 전달
		else if (this.started) {
			this.processEvent(event);
		}
	}

	public void leaveGame(Player p) {
		// check
		// 1. game waitingTime counter must be upper than 10
		// 2. game must not be started

		if (this.waitingCounter.getCount() <= 10) {
			p.sendMessage("You can't leave game(Reason: game will start soon)");
			return;
		}

		if (this.started) {
			p.sendMessage("You can't leave game(Reason: game already has started)");
			return;
		}

		// leave
		this.setupPlayerLeavingSettings(p, "Before start");

		// check game is emtpy
		if (this.isEmpty()) {
			this.initSetting();
		}
	}

	private void setupPlayerLeavingSettings(Player p, String reason) {
		/*
		 * TODO: After this method, must check isEmpty() and run initSetting() (except
		 * for runFinishTask())
		 */
		if (reason != null) {
			// notify other players to join the game
			this.sendMessageToAllPlayers(p.getName() + " leaved " + this.getTitle() + "(Reason: " + reason + ")");
		}
		// remove player from minigame
		this.removePlayer(p);

		// setup player state
		this.setupPlayerWhenLeave(p);
	}

	public boolean joinGame(Player p) {
		// 처리 순서
		// 1.minigames.json에서 actived가 true인지
		// 2.이미 게임이 시작전인지
		// 3.게임 인원이 풀이 아닌지
		// -> 게임 참여 로직 처리

		if (!this.getActived()) {
			p.sendMessage(this.getTitle() + " is not active");
			return false;
		}

		if (this.started) {
			p.sendMessage(this.getTitle() + " already started");
			return false;
		}

		if (this.isFullPlayer()) {
			p.sendMessage("Player is full");
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

	protected void setupPlayerJoinSettings(Player p) {
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
		p.sendMessage("\n=================================");
		p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
		p.sendMessage("=================================");

		// print rule
		p.sendMessage(ChatColor.BOLD + "[Rule]");
		p.sendMessage("- Time Limit: " + this.getTimeLimit() + " sec");

		// tutorial
		if (this.getGameTutorialStrings() != null) {
			for (String msg : this.getGameTutorialStrings()) {
				p.sendMessage("- " + msg);
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
			if (!this.isFullPlayer()) {
				// send message
				this.sendMessageToAllPlayers("Game didn't started: game needs full players");

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
		sendTitleToPlayers("START", "", 4, 20 * 2, 4);

		// finishTimer 시작
		startFinishTimer();

		// runTaskAfterStart
		runTaskAfterStart();

		// 태스크 종료
		this.taskManager.cancelTask("_waitingTimer");
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

	protected void printEndInfo() {
		// title
		for (Player p : this.getPlayers()) {
			p.sendMessage("\n=================================");
			p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
			p.sendMessage("=================================");
		}

		// 종료 알리기
		sendTitleToPlayers("FINISH", "", 4, 20 * 2, 4);

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
		List<Entry<Player, Integer>> entries = this.rankM.getDescendingSortedList(this.players);
		int rank = 1;
		for (Entry<Player, Integer> entry : entries) {
			Player p = entry.getKey();
			int score = entry.getValue();
			this.sendMessageToAllPlayers("[" + rank + "] " + p.getName() + ": " + score);
			rank += 1;
		}

	}

	private boolean isEmpty() {
		return this.getPlayers().isEmpty();
	}

	private boolean isFullPlayer() {
		int currentPlayerCount = this.getPlayers().size();
		int maxPlayerCount = this.getMaxPlayerCount();
		return currentPlayerCount == maxPlayerCount;
	}

	public boolean containsPlayer(Player p) {
		return this.players.containsKey(p);
	}

	public List<Player> getPlayers() {
		List<Player> allPlayer = new ArrayList<Player>();
		for (Player p : this.players.keySet()) {
			allPlayer.add(p);
		}
		return allPlayer;
	}

	public int getPlayerCount() {
		return this.getPlayers().size();
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

	protected void sendMessageToAllPlayers(String msg) {
		for (Player p : getPlayers()) {
			p.sendMessage(msg);
		}
	}

	protected void sendTitleToPlayers(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		for (Player p : getPlayers()) {
			p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
		}
	}

	protected int getScore(Player p) {
		return this.players.get(p);
	}

	protected void plusScore(Player p, int score) {
		int previousScore = this.players.get(p);
		this.players.put(p, previousScore + score);
		// scoreNotifying 메세지 전송
		if (this.setting.isScoreNotifying()) {
			p.sendMessage("[" + this.getTitle() + "] +" + score);
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
			p.sendMessage("[" + this.getTitle() + "] -" + score);
		}
	}

	protected void minusEveryoneScore(int score) {
		this.getPlayers().forEach(p -> this.minusScore(p, score));
	}

	public enum Exception {
		PlayerQuitServer, ServerDown;
	}

	public final void handleException(Player p, Exception exception, Object arg) {
		/*
		 * [예외 상황 관리]
		 * 
		 * - 플레이어가 미니게임에서 예외상황으로 인해 게임 플레이가 불가능 할 떄 호출되는 메서드
		 * 
		 * - PlayerQuitEvent에서 Reason 체크
		 * 
		 * - 현재: 게임도중 서버 나가는 예외 처리
		 * 
		 * - 마지막에 각 게임에게 예외 발생 매소드로 알림
		 */

		// info
		BroadcastTool.info("[" + p.getName() + "] handle exception: " + exception.name());

		// PlayerQuitServer일 때 이유 출력
		if (exception == Exception.PlayerQuitServer) {
			PlayerQuitEvent event = (PlayerQuitEvent) arg;
			BroadcastTool.info("Quit: " + event.getReason().name());
		}

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

		// 각 게임에게 예외 발생 매소드로 처리 알림 (예. task 중지)
		this.handleGameExeption(p, exception, arg);
	}

	private void setupPlayerWhenJoin(Player p) {
		// 게임룸 위치로 tp
		p.teleport(this.getLocation());

		// player inventory clear
		p.getInventory().clear();

		// 플레이어 상태 초기화
		PlayerTool.makePureState(p);
	}

	private void setupPlayerWhenLeave(Player p) {
		// player tp
		MiniGameManager minigameM = MiniGameManager.getInstance();
		Location serverSpawn = minigameM.getServerSpawn();
		p.teleport(serverSpawn);

		// player inventory clear
		p.getInventory().clear();

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
		return this.getSetting().getLocation();
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

	public boolean getActived() {
		return this.getSetting().isActived();
	}

	public boolean isSettingFixed() {
		return this.getSetting().isSettingFixed();
	}

	public void setAttributes(String title, Location location, int maxPlayerCount, int waitingTime, int timeLimit,
			boolean actived, boolean settingFixed) {
		this.setting.setTitle(title);
		this.setting.setLocation(location);
		this.setting.setMaxPlayerCount(maxPlayerCount);
		this.setting.setWaitingTime(waitingTime);
		this.setting.setTimeLimit(timeLimit);
		this.setting.setActived(actived);
		this.setting.setSettingFixed(settingFixed);
	}

	protected void checkAttributes() {
		/*
		 * frame MiniGame class에서 조건 추가적으로 검사, final로 선언
		 */
		// title
		if (this.getTitle().length() <= 0) {
			BroadcastTool.warn(this.getTitleWithClassName() + ": title must be at least 1 character");
		}
		// timeLimit
		if (this.getTimeLimit() <= 0) {
			BroadcastTool.warn(this.getTitleWithClassName() + ": timeLimit must be at least 1 sec");
		}
	}

	public String getTitleWithClassName() {
		return this.getTitle() + "[Class: " + this.getClassName() + "]";
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	protected MiniGameSetting getSetting() {
		return this.setting;
	}

	public int getLeftWaitTime() {
		// 기다리는 남은 시간 리턴 (sec)
		return this.waitingCounter.getCount();
	}

	public int getLeftFinishTime() {
		// 끝나기까지 남은 시간 리턴 (sec)
		return this.finishCounter.getCount();
	}

	public String getEveryoneNameString() {
		String members = "";
		for (Player p : this.getPlayers()) {
			members += p.getName() + ", ";
		}
		// 마지막 ", " 제거
		members = members.substring(0, members.length() - 2);
		return members;
	}

	protected BukkitTaskManager getTaskManager() {
		return this.taskManager;
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
