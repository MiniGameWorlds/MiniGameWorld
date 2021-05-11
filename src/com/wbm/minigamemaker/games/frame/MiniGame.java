package com.wbm.minigamemaker.games.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

	// 미니게임 정보
	private String title;
	private Location location; // 기본값: new Location(Bukkit.getWorld("world"), 0, 4, 0)
	private int maxPlayerCount;
	private int waitingTime;
	private int timeLimit;
	private boolean actived; // 기본값: true
	private boolean settingFixed; // 기본값: false

	// 게임이 카운트 다운이 끝나고 실제로 시작한지 여부
	private boolean started;

	// 각종 타이머 태스크
	private BukkitTask waitingTimerTask, finishTimerTask;

	// <참여중인 플레이어들, 플레이어 점수>
	private Map<Player, Integer> players;

	// 랭크 매니저
	RankManager rankM;

	// abstract
	protected abstract void initGameSetting();

	protected abstract void processEvent(Event event);

	protected abstract void runTaskAfterStart();

	protected abstract void runTaskAfterFinish();

	protected abstract List<String> getGameTutorialStrings();

	protected abstract void handleGameExeption(Player p);

	// 기본 생성자
	public MiniGame(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.setAttributes(title, location, maxPlayerCount, waitingTime, timeLimit, true, false);
		this.rankM = new RankManager();
	}

	// location 기본값 생성자
	public MiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		this(title, new Location(Bukkit.getWorld("world"), 0, 4, 0), maxPlayerCount, timeLimit, waitingTime);
	}

	private void initSetting() {
		this.started = false;
		this.stopAllTask();
		if (this.players == null) {
			this.players = new HashMap<>();
		} else {
			this.players.clear();
		}

		// 하위 미니게임 세팅 값 설정
		this.initGameSetting();
	}

	public void passEvent(Event event) {
		/*
		 * 넘겨받은 이벤트 처리
		 */

		// 미니게임 탈주 이벤트인지 검사
		if (event instanceof PlayerQuitEvent) {
			this.handleException((PlayerQuitEvent) event);
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
		if (this.waitingTimerTask != null) {
			this.waitingTimerTask.cancel();
		}

		if (this.finishTimerTask != null) {
			this.finishTimerTask.cancel();
		}
	}

	public boolean joinGame(Player p) {
		// 처리 순서
		// 1.actived가 true인가
		// 2.이미 게임이 시작전인가
		// 3.게임 인원이 풀이 아닌가
		// -> 게임 참여 로직 처리
		if (!this.actived) {
			p.sendMessage(this.title + " is not active");
			return false;
		}

		if (this.started) {
			p.sendMessage(this.title + " already started");
			return false;
		}

		if (this.isFullPlayer()) {
			p.sendMessage("Player is full");
			return false;
		}

		// 처음 들어온 사람일 경우 세팅초기화후에 타이머 시작
		if (this.isEmpty()) {
			this.initSetting();
			this.startWaitingTimerTask();
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
		p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + this.title + ChatColor.WHITE);
		p.sendMessage("=================================");

		// print rule
		p.sendMessage("");
		p.sendMessage(ChatColor.BOLD + "[Rule]");
		p.sendMessage("Time Limit: " + this.timeLimit + " sec");
		for (String msg : this.getGameTutorialStrings()) {
			p.sendMessage(msg);
		}
	}

	private void startWaitingTimerTask() {
		/*
		 * waitingTime동안 기다린 후에 게임 시작
		 */
		Counter timer = new Counter(this.waitingTime);

		this.waitingTimerTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				// 카운트다운 끝
				if (timer.getCount() <= 0) {
					// 활성화
					started = true;

					// 시작 타이틀
					sendTitleToPlayers("START", "", 4, 20 * 2, 4);

					// runTaskAfterStart
					runTaskAfterStart();

					// finishTask 시작
					startFinishTimerTask();

					// 태스크 종료
					waitingTimerTask.cancel();

					return;
				}

				// 플레이어들에게 카운트 다운 타이틀
				sendTitleToPlayers("" + timer.getCount(), "", 4, 12, 4);
				timer.removeCount(1);
			}
		}, 0, 20);
	}

	private void startFinishTimerTask() {
		Counter timer = new Counter(this.timeLimit);
		this.finishTimerTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				int leftTime = timer.getCount();
				if (leftTime <= 0) {
					// 종료 알리기
					sendTitleToPlayers("FINISH", "", 4, 20 * 2, 4);

					// runTaskAfterFinish 시작
					runTaskAfterFinish();

					// print score
					printScore();

					// setup player
					for (Player p : getPlayers()) {
						setupPlayerWhenExit(p);
					}

					// initSeting
					initSetting();

					// 태스크 종료
					finishTimerTask.cancel();

					return;
				} else if (leftTime <= 10) {
					// 10초 이하 남았을 때 알리기
					sendTitleToPlayers("" + leftTime, "", 4, 12, 4);
				}

				timer.removeCount(1);
			}
		}, 0, 20);
	}

	// 게임 유형에 따라 다르게 출력되야 할 필요 있음
	protected void printScore() {
		// 스코어 결과 score기준 내림차순으로 출력
		this.sendMessageToAllPlayers("[Score]");
		List<Entry<Player, Integer>> entries = this.rankM.getDescendingSortedList(this.players);
		for (Entry<Player, Integer> entry : entries) {
			Player p = entry.getKey();
			int score = entry.getValue();
			this.sendMessageToAllPlayers(p.getName() + ": " + score);
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

	public int getMaxPlayerCount() {
		return this.maxPlayerCount;
	}

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
	}

	protected void minusScore(Player p, int score) {
		int previousScore = this.players.get(p);
		this.players.put(p, previousScore - score);
	}

	public void handleException(PlayerQuitEvent event) {
		/*
		 * [예외 관리]
		 * 
		 * - 나중에 MiniGame.Exception enum으로 세분화해서 관리 예정
		 * 
		 * - PlayerQuitEvent에서 Reason 체크
		 * 
		 * 현재: 게임도중 서버 나가는 예외 처리
		 * 
		 * 마지막에 각 게임에게 예외 발생 매소드로 알림
		 */

		// player
		Player p = event.getPlayer();

		// log
		BroadcastTool.info("[" + p.getName() + "] handle exception: " + event.getReason().name());

		// player 삭제
		this.removePlayer(p);

		// setup player
		this.setupPlayerWhenExit(p);

		if (this.isEmpty()) {
			// 미니게임에 남은 사람이 없으면 미니게임 세팅 초기화
			this.initSetting();
		} else {
			// 미니게임에 남은 사람이 있으면 남은 인원에게 알리기
			this.sendMessageToAllPlayers(p.getName() + " quit " + this.title);
		}

		// 각 게임에게 예외 발생 매소드로 처리 알림 (예. task 중지)
		this.handleGameExeption(p);
	}

	private void setupPlayerWhenJoin(Player p) {
		// 게임룸 위치로 tp
		p.teleport(this.location);

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

	public String getTitle() {
		return title;
	}

	public Location getLocation() {
		return location;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public boolean getActived() {
		return this.actived;
	}

	public boolean getSettingFixed() {
		return this.settingFixed;
	}

	// 구현한 미니게임 클래스에서 사용가능
	protected void setSettingFixed(boolean settingFixed) {
		this.settingFixed = settingFixed;
	}

	public void setAttributes(String title, Location location, int maxPlayerCount, int waitingTime, int timeLimit,
			boolean actived, boolean settingFixed) {
		this.title = title;
		this.location = location;
		this.maxPlayerCount = maxPlayerCount;
		this.waitingTime = waitingTime;
		this.timeLimit = timeLimit;
		this.actived = actived;
		this.settingFixed = settingFixed;
	}

	public String getTitleWithClassName() {
		return this.title + "(ClassName: " + this.getClassName() + ")";
	}

	public String getClassName() {
		return this.getClass().getSimpleName();
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
