package com.wbm.minigamemaker.games.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.wbm.minigamemaker.util.Setting;
import com.wbm.plugin.util.SortTool;

public abstract class TeamBattleMiniGame extends MiniGame {

	/*
	 * 
	 * [Info]
	 * - team battle play
	 * - each team has same score
	 * - team util class, methods
	 * 
	 * [Rule]
	 * - when use initGameSetting(), must call super.initGameSetting()
	 * - when "autoTeamSetup" is false, register player with team using overrided registerAllPlayersToTeam() method
	 * - when use runTaskAfterStart(), must call super.runTaskAfterStart()
	 * - whem use processEvent(), must call super.processEvent()
	 * 
	 */

	private List<Team> allTeams;
	// 팀 개수
	private int teamCount;
	// 팀 인원 수
	private int teamSize;
	private boolean groupChat;
	// 팀 개수, 인원수에 맞게 자동 팀 분배
	private boolean autoTeamSetup;

	// 팀 등록 강제
	protected abstract void registerAllPlayersToTeam();

	public TeamBattleMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime, int teamCount,
			int teamSize) {
		super(title, maxPlayerCount, timeLimit, waitingTime);

		// set teamCount, teamSize
		this.fixTeamCount(teamCount);
		this.fixTeamSize(teamSize);
		// setup team
		this.setupAllTeams();
	}

	protected void setGroupChat(boolean groupChat) {
		this.groupChat = groupChat;
	}

	protected void setAutoTeamSetup(boolean autoTeamSetup) {
		//
		this.autoTeamSetup = autoTeamSetup;
	}

	private void setupAllTeams() {
		// allTeams 초기화
		if (this.allTeams == null) {
			this.allTeams = new ArrayList<Team>();
		} else {
			this.allTeams.clear();
		}

		// teamCount만큼 팀 추가
		for (int i = 0; i < this.teamCount; i++) {
			this.allTeams.add(new Team());
		}
	}

	protected void fixTeamCount(int teamCount) {
		// 팀 최대 갯수 설정
		this.teamCount = teamCount;
	}

	protected void fixTeamSize(int teamSize) {
		// 1팀당 최대 인원 설정
		this.teamSize = teamSize;
	}

	protected Team getTeam(int teamNumber) {
		return allTeams.get(teamNumber);
	}

	protected Team getPlayerTeam(Player p) {
		for (Team team : allTeams) {
			if (team.hasMember(p)) {
				return team;
			}
		}
		return null;
	}

	protected boolean isSameTeam(Player p1, Player p2) {
		/*
		 * 2 player가 같은 팀인지
		 */
		return this.getPlayerTeam(p1) == this.getPlayerTeam(p2);
	}

	protected boolean registerPlayerWithTeam(Player p) {
		/*
		 * 순서대로 팀에 플레이어 추가
		 */
		for (int teamNumber = 0; teamNumber < teamCount; teamNumber++) {
			if (this.registerPlayerWithTeam(p, teamNumber)) {
				return true;
			}
		}

		return false;
	}

	protected boolean registerPlayerWithTeam(Player p, int teamNumber) {
		/*
		 * teamNumber 팀에 플레이어 추가
		 */
		// teamCount 검사
		if (teamNumber >= teamCount) {
			return false;
		}
		// team 가져오기
		Team team = getTeam(teamNumber);
		return team.registerMember(p);
	}

	protected boolean unregisterPlayerFromTeam(Player p) {
		/*
		 * player를 어떤 팀에서든 unregister시킴
		 */
		for (int teamNumber = 0; teamNumber < teamCount; teamNumber++) {
			Team team = getTeam(teamNumber);
			if (team.unregisterMember(p)) {
				return true;
			}
		}
		return false;
	}

	protected int getTeamCount() {
		return this.teamCount;
	}

	protected int getTeamSize() {
		return this.teamSize;
	}

	protected boolean isValidTeam(Team team) {
		return !team.isEmpty();
	}

	protected int getValidTeamCount() {
		// player가 1명 이상 들어있는 팀의 개수
		int count = 0;
		for (Team team : this.allTeams) {
			if (this.isValidTeam(team)) {
				count++;
			}
		}
		return count;
	}

	protected void plusTeamScore(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		team.plusTeamScore(score);
	}

	// protected void plusScoreToTeam(Team team, int score) {
	// team.plusScoreToMembers(score);
	// }

	protected void minusTeamScore(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		team.minusTeamScore(score);
	}

	// protected void minusScoreToTeam(Team team, int score) {
	// team.minusScoreToMembers(score);
	// }

	protected int getTeamScore(int teamNumber) {
		Team team = this.getTeam(teamNumber);
		return team.getTeamScore();
	}

	@Override
	protected void initGameSetting() {
		this.setupAllTeams();
	}

	@Override
	protected void runTaskAfterStart() {
		// 자동 팀 분배 (한명씩 모든 팀에 번갈아가면서 넣기)
		if (this.autoTeamSetup) {
			int teamNumber = 0;
			for (Player p : this.getPlayers()) {
				this.registerPlayerWithTeam(p, teamNumber);
				teamNumber = (teamNumber + 1) % this.teamCount;
			}
		} else {
			this.registerAllPlayersToTeam();
		}

		this.checkOnlyOneTeamRemains();
	}

	protected boolean checkOnlyOneTeamRemains() {
		// 1개의 팀에만 플레이어가 존재하는지 검사후 맞으면 게임 종료
		if (this.getValidTeamCount() <= 1) {
			this.sendMessageToAllPlayers("Game End: only 1 team remains");
			this.endGame();
			return true;
		}
		return false;
	}

	@Override
	protected void handleGameExeption(Player p, Exception exception, Object arg) {
		super.handleGameExeption(p, exception, arg);
		this.checkOnlyOneTeamRemains();
	}

	@Override
	protected void processEvent(Event event) {
		/*
		 * 그룹채팅 기능
		 * 
		 * - 사용하려면 groupChat=true와 super.processEvent()를 사용하여야 함
		 */
		// groupChat이 true일 때
		if (this.groupChat) {
			if (event instanceof AsyncPlayerChatEvent) {
				AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
				// 플레이어 채팅 cancel후
				e.setCancelled(true);
				Player sender = e.getPlayer();

				// 팀 내 플레이어들에게만 채팅 전송
				Team team = this.getPlayerTeam(sender);
				// ex. [GameTitle] worldbiomusic: go go
				team.sendTeamMessage(sender, e.getMessage());
			}
		}
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
		// waitingTime
		if (this.getWaitingTime() <= 0) {
			Setting.warning(this.getTitleWithClassName() + ": waitingTime must be at least 1 sec");
		}
		// maxPlayerCount
		if (this.getMaxPlayerCount() <= 1) {
			Setting.warning(this.getTitleWithClassName()
					+ ": maxPlayer is recommended at least 2 players(or extends SoloMiniGame)");
		}
	}

	@Override
	protected void printScore() {
		// 스코어 결과 팀 score기준 내림차순으로 출력
		this.sendMessageToAllPlayers("[Score]");

		// team별 멤버 1명씩 삽입
		Map<Player, Integer> eachValidTeamPlayer = new HashMap<Player, Integer>();
		for (Team team : this.allTeams) {
			if (this.isValidTeam(team)) {
				Player member = team.getMembers().get(0);
				int teamScore = team.getTeamScore();
				eachValidTeamPlayer.put(member, teamScore);
			}
		}

		// 팀 점수대로 순위
		List<Entry<Player, Integer>> entries = SortTool.getDescendingSortedList(eachValidTeamPlayer);
		int rank = 1;
		for (Entry<Player, Integer> entry : entries) {
			Player p = entry.getKey();
			int score = entry.getValue();
			Team team = this.getPlayerTeam(p);
			String memberString = team.getAllMemberNameString();
			this.sendMessageToAllPlayers("[" + rank + "] " + "Team(" + memberString + ")" + ": " + score);
			rank += 1;
		}

	}

	public class Team {
		/*
		 * 변수 값에 대한 접근은 Team메소드는 public
		 * 
		 * 변수 값에 대한 설정은 TeamMiniGame의 메소드로의 접근을 강제하기 위해 Team메소드는 private
		 */
		private List<Player> members;

		Team() {
			this.members = new ArrayList<Player>();
		}

		public int size() {
			return this.members.size();
		}

		public boolean isEmpty() {
			return this.size() == 0;
		}

		public List<Player> getMembers() {
			return this.members;
		}

		public void sendTeamMessage(Player sender, String msg) {
			this.members.forEach(p -> sendMessage(p, sender.getName() + ": " + msg));
		}

		public int getTeamScore() {
			return getScore(this.members.get(0));
		}

		public void plusTeamScore(int score) {
			this.getMembers().forEach(p -> plusScore(p, score));
		}

		public void minusTeamScore(int score) {
			this.getMembers().forEach(p -> minusScore(p, score));
		}

		private boolean registerMember(Player p) {
			// teamSize 검사
			boolean isFull = this.size() >= teamSize;
			if (isFull) {
				return false;
			} else {
				this.members.add(p);
				return true;
			}
		}

		private boolean unregisterMember(Player p) {
			return this.members.remove(p);
		}

		private boolean hasMember(Player p) {
			return this.members.contains(p);
		}

		public String getAllMemberNameString() {
			String members = "";
			for (Player p : this.members) {
				members += p.getName() + ", ";
			}
			// 마지막 ", " 제거
			members = members.substring(0, members.length() - 2);
			return members;
		}
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
