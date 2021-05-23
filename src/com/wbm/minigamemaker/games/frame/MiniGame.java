package com.wbm.minigamemaker.games.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.minigamemaker.Main;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.Counter;
import com.wbm.plugin.util.PlayerTool;

import net.md_5.bungee.api.ChatColor;

public abstract class MiniGame {
	/*
	 * 플레이어 서버 나갈 때 예외처리
	 */

	protected class Setting {
		/*
		 * 미니게임 설정값 관리 클래스
		 */
		// 파일 관리 o
		private String title;
		// 파일 관리 o
		// 기본값: new Location(Bukkit.getWorld("world"), 0, 4, 0)
		private Location location;
		// 파일 관리 o
		private int maxPlayerCount;
		// 파일 관리 o
		private int waitingTime;
		// 파일 관리 o
		private int timeLimit;
		// 파일 관리 o
		// 기본값: true
		private boolean actived;
		// 파일 관리 x
		// 기본값: false
		private boolean settingFixed;
		// 파일 관리 x
		// 기본값: false
		private boolean scoreNotifying;

		public Setting(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
			this.title = title;
			this.location = location;
			this.maxPlayerCount = maxPlayerCount;
			this.waitingTime = waitingTime;
			this.timeLimit = timeLimit;

			this.actived = true;
			this.settingFixed = false;
			this.scoreNotifying = false;
		}

		public void setSettingFixed(boolean settingFixed) {
			this.settingFixed = settingFixed;
		}

		public void setScoreNotifying(boolean scoreNotifying) {
			this.scoreNotifying = scoreNotifying;
		}

		private void setTitle(String title) {
			this.title = title;
		}

		private void setLocation(Location location) {
			this.location = location;
		}

		private void setMaxPlayerCount(int maxPlayerCount) {
			this.maxPlayerCount = maxPlayerCount;
		}

		private void setWaitingTime(int waitingTime) {
			this.waitingTime = waitingTime;
		}

		private void setTimeLimit(int timeLimit) {
			this.timeLimit = timeLimit;
		}

		private void setActived(boolean actived) {
			this.actived = actived;
		}

		/*
		 * getter는 public해도 상관없음
		 */
		public String getTitle() {
			return title;
		}

		public Location getLocation() {
			return location;
		}

		public int getMaxPlayerCount() {
			return maxPlayerCount;
		}

		public int getWaitingTime() {
			return waitingTime;
		}

		public int getTimeLimit() {
			return timeLimit;
		}

		public boolean isActived() {
			return actived;
		}

		public boolean isSettingFixed() {
			return settingFixed;
		}

		public boolean isScoreNotifying() {
			return scoreNotifying;
		}
	}

	// 미니게임 정보
	private Setting setting;

	// 게임이 카운트 다운이 끝나고 실제로 시작한지 여부
	private boolean started;

	// 각종 타이머 태스크
	private BukkitTask waitingTimer, finishTimer;

	// 타이머 관련 시간 카운터
	private Counter waitingCounter, finishCounter;

	// <참여중인 플레이어들, 플레이어 점수>
	private Map<Player, Integer> players;

	// 랭크 매니저
	protected RankManager rankM;

	// abstract
	protected abstract void initGameSetting();

	protected abstract void processEvent(Event event);

	protected abstract List<String> getGameTutorialStrings();

	// 기본 생성자
	protected MiniGame(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.setting = new Setting(title, location, maxPlayerCount, timeLimit, waitingTime);
		this.setupMiniGame();

		// [한번만 초기화 되야하는 것들]
		// 랭크 매니저
		this.rankM = new RankManager();
	}

	// location 기본값 생성자
	protected MiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		this(title, new Location(Bukkit.getWorld("world"), 0, 4, 0), maxPlayerCount, timeLimit, waitingTime);
	}

	// 구현 선택사항 메소드들
//	protected void runTaskBeforeStart() {
//	};

	protected void runTaskAfterStart() {
	};

	protected void runTaskBeforeFinish() {
	};

	protected void runTaskAfterFinish() {
	};

	protected void handleGameExeption(Player p, Exception exception, Object arg) {
	};

	private void setupMiniGame() {
		this.started = false;
		this.stopAllTask();
		if (this.players == null) {
			this.players = new HashMap<>();
		} else {
			this.players.clear();
		}
		// counter
		this.waitingCounter = new Counter(this.getWaitingTime());
		this.finishCounter = new Counter(this.getTimeLimit());
	}

	private void initSetting() {
		this.setupMiniGame();

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

	private void stopAllTask() {
		/*
		 * 모든 태스크 중지
		 */
		if (this.waitingTimer != null) {
			this.waitingTimer.cancel();
		}

		if (this.finishTimer != null) {
			this.finishTimer.cancel();
		}
	}

	public boolean joinGame(Player p) {
		// 처리 순서
		// 1.이미 다른 미니게임에 참여중이 아닌지
		// 2.actived가 true인지
		// 3.이미 게임이 시작전인지
		// 4.게임 인원이 풀이 아닌지
		// -> 게임 참여 로직 처리

		MiniGameManager minigameM = MiniGameManager.getInstance();
		boolean isPlayingOtherMiniGame = minigameM.getPlayingGame(p) != null;
		if (isPlayingOtherMiniGame) {
			p.sendMessage("You already joined other minigame");
			return false;
		}

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
		setupPlayerSettings(p);

		// 성공적으로 joinGame
		return true;
	}

	protected void setupPlayerSettings(Player p) {
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
	}

	private void printGameTutorial(Player p) {
		/*
		 * 튜토리얼
		 */
		p.sendMessage("=================================");
		p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + this.getTitle() + ChatColor.WHITE);
		p.sendMessage("=================================");

		// print rule
		p.sendMessage("");
		p.sendMessage(ChatColor.BOLD + "[Rule]");
		p.sendMessage("Time Limit: " + this.getTimeLimit() + " sec");

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
		this.waitingTimer = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {

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
		}, 0, 20);
	}

	private void runStartTasks() {
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
		if (this.waitingTimer != null) {
			waitingTimer.cancel();
		}
	}

	private void startFinishTimer() {
		this.finishTimer = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {

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
		}, 0, 20);
	}

	private void runFinishTasks() {
		// runTaskBeforeFinish
		runTaskBeforeFinish();

		// play sound
		this.getPlayers().forEach(p -> PlayerTool.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH));

		printEndInfo();

		// setup player
		for (Player p : getPlayers()) {
			setupPlayerWhenExit(p);
		}

		// runTaskAfterFinish
		runTaskAfterFinish();

		// initSeting
		initSetting();

		// 태스크 종료
		if (this.finishTimer != null) {
			finishTimer.cancel();
		}
	}

	protected void printEndInfo() {
		// title
		for (Player p : this.getPlayers()) {
			p.sendMessage("=================================");
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
		this.sendMessageToAllPlayers("[Score]");
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
		return (currentPlayerCount >= maxPlayerCount);
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

	enum Exception {
		PlayerQuitServer, ServerDown;
	}

	public final void handleException(Player p, Exception exception, Object arg) {
		/*
		 * [예외 상황 관리]
		 * 
		 * - 나중에 MiniGame.Exception enum으로 세분화해서 관리 예정
		 * 
		 * - PlayerQuitEvent에서 Reason 체크
		 * 
		 * 현재: 게임도중 서버 나가는 예외 처리
		 * 
		 * 마지막에 각 게임에게 예외 발생 매소드로 알림
		 */

		// log
		BroadcastTool.info("[" + p.getName() + "] handle exception: " + exception.name());

		// PlayerQuitServer일 때 이유 출력
		if (exception == Exception.PlayerQuitServer) {
			PlayerQuitEvent event = (PlayerQuitEvent) arg;
			BroadcastTool.info("Quit reason: " + event.getReason().name());
		}

		// player 삭제
		this.removePlayer(p);

		// setup player
		this.setupPlayerWhenExit(p);

		if (this.isEmpty()) {
			// 미니게임에 남은 사람이 없으면 미니게임 세팅 초기화
			this.initSetting();
		} else {
			// 미니게임에 퇴장한 플레이어를 남은 인원에게 알리기
			this.sendMessageToAllPlayers(p.getName() + " quit " + this.getTitle());
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

	private void setupPlayerWhenExit(Player p) {
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
		return this.getTitle() + "(ClassName: " + this.getClassName() + ")";
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	protected Setting getSetting() {
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
