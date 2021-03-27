package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.minigamemaker.Main;
import com.wbm.minigamemaker.util.Counter;
import com.wbm.minigamemaker.util.PlayerTool;

import net.md_5.bungee.api.ChatColor;

public abstract class MiniGame {
	/*
	 * 플레이어 서버 나갈 때 예외처리
	 */

	// 게임 끝나고 돌아갈 서버 스폰
	private static Location serverSpawn;

	// 게임이 카운트 다운이 끝나고 실제로 시작한 여부
	private boolean started;

	// 미니게임 타입
	private MiniGameType gameType;

	// 각종 타이머 태스크
	private BukkitTask waitingTimerTask, finishTimerTask;

	// <참여중인 플레이어들, 플레이어 점수>
	private Map<Player, Integer> players;

	// abstract
	protected abstract void processEvent(Event event);

	protected abstract void runTaskAfterStart();

	protected abstract void runTaskAfterFinish();

	protected abstract List<String> getGameTutorialStrings();

	protected abstract void initGameSetting();

	protected abstract void handleGameExeption(Player p);

	// 생성자
	public MiniGame(MiniGameType gameType) {
		this.gameType = gameType;
		this.initSetting();
	}

	private void initSetting() {
		serverSpawn = new Location(Bukkit.getWorld("world"), 0, 4, 0);
		this.started = false;
		this.stopAllTask();
		this.players = new HashMap<>();

		// 하위 미니게임 세팅 값 설정
		this.initGameSetting();
	}

	public void passEvent(Event event) {
		/*
		 * 게임이 시작됬을 때만 이벤트 처리
		 */
		if (this.started) {
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

	public void joinGame(Player p) {
		// 처리 순서
		// 1.이미 게임이 시작전인가
		// 2.게임 인원이 풀이 아닌가
		// -> 게임 참여 로직 처리
		if (this.started) {
			p.sendMessage("Game already started");
			return;
		}

		if (this.isFullPlayer()) {
			p.sendMessage("Player is full");
			return;
		}

		// 처음 들어온 사람일 경우 세팅초기화후에 타이머 시작
		if (this.isEmpty()) {
			this.initSetting();
			this.startWaitingTimerTask();
		}

		// player 세팅
		setupPlayerSettings(p);
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
		p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + this.gameType.name() + ChatColor.WHITE);
		p.sendMessage("=================================");

		// print rule
		p.sendMessage("");
		p.sendMessage(ChatColor.BOLD + "[Rule]");
		p.sendMessage("Time Limit: " + this.gameType.getTimeLimit() + " sec");
		for (String msg : this.getGameTutorialStrings()) {
			p.sendMessage(msg);
		}
	}

	private void makePlayerPureState(Player p) {
		// 플레이어 상태 초기화
		// 상태 초기화
		PlayerTool.removeAllState(p);
		// 힐, 배고픔 충전
		PlayerTool.heal(p);
	}

	private void startWaitingTimerTask() {
		/*
		 * waitingTime동안 기다린 후에 게임 시작
		 */
		Counter timer = new Counter(this.gameType.getWaitingTime());

		this.waitingTimerTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				// 카운트다운 끝
				if (timer.getCount() <= 0) {
					// 활성화
					started = true;

					// 시작 타이틀
					sendTitleToEveryone("START", "", 4, 20 * 2, 4);

					// runTaskAfterStart
					runTaskAfterStart();

					// finishTask 시작
					startFinishTimerTask();

					// 태스크 종료
					waitingTimerTask.cancel();

					return;
				}

				// 플레이어들에게 카운트 다운 타이틀
				sendTitleToEveryone("" + timer.getCount(), "", 4, 12, 4);
				timer.removeCount(1);
			}
		}, 0, 20);
	}

	private void startFinishTimerTask() {
		Counter timer = new Counter(this.gameType.getTimeLimit());
		this.finishTimerTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				int leftTime = timer.getCount();
				if (leftTime <= 0) {
					// 종료 알리기
					sendTitleToEveryone("FINISH", "", 4, 20 * 2, 4);

					// runTaskAfterFinish 시작
					runTaskAfterFinish();

					// print score
					printScore();

					// initSeting
					initSetting();

					// setup player
					for (Player p : getPlayers()) {
						setupPlayerWhenExit(p);
					}

					// 태스크 종료
					finishTimerTask.cancel();

					return;
				} else if (leftTime <= 10) {
					// 10초 이하 남았을 때 알리기
					sendTitleToEveryone("" + leftTime, "", 4, 12, 4);
				}

				timer.removeCount(1);
			}
		}, 0, 20);
	}

	private void printScore() {
		this.sendMessageToEveryone("[Score]");
		for (Player p : this.getPlayers()) {
			this.sendMessageToEveryone(p.getName() + ": " + this.getScore(p));
		}
	}

	private boolean isEmpty() {
		return (this.getPlayers().size() == 0);
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

	private int getMaxPlayerCount() {
		return this.gameType.getMaxPlayerCount();
	}

	private void addPlayer(Player p) {
		// 0점 으로 플레이어 등록
		this.players.put(p, 0);
	}

	private void removePlayer(Player p) {
		this.players.remove(p);
	}

	private void sendMessageToEveryone(String msg) {
		for (Player p : getPlayers()) {
			p.sendMessage(msg);
		}
	}

	private void sendTitleToEveryone(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		for (Player p : getPlayers()) {
			p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
		}
	}

	private int getScore(Player p) {
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

	public void handleException(Player p) {
		/*
		 * [예외 관리]
		 * 
		 * 나중에 MiniGame.Exception enum으로 세분화해서 관리 예정
		 * 
		 * 현재: 게임도중 서버 나가는 예외 처리
		 * 
		 * 마지막에 각 게임에게 예외 발생 매소드로 알림
		 */

		System.out.println("handle exception");

		// player 삭제
		this.removePlayer(p);

		// setup player
		this.setupPlayerWhenExit(p);

		if (this.isEmpty()) {
			// 미니게임에 남은 사람이 없으면 미니게임 세팅 초기화
			this.initSetting();
		} else {
			// 미니게임에 남은 사람이 있으면 남은 인원에게 알리기
			this.sendMessageToEveryone(p.getName() + " quit " + this.gameType.name());
		}

		// 각 게임에게 예외 발생 매소드로 알림
		this.handleGameExeption(p);
	}

	private void setupPlayerWhenJoin(Player p) {
		// 게임룸 위치로 tp
		p.teleport(this.gameType.getSpawnLocation());

		// player inventory clear
		p.getInventory().clear();

		// 플레이어 상태 초기화
		this.makePlayerPureState(p);
	}

	private void setupPlayerWhenExit(Player p) {
		// player tp
		p.teleport(serverSpawn);

		// player inventory clear
		p.getInventory().clear();

		// 플레이어 상태 초기화
		this.makePlayerPureState(p);
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
