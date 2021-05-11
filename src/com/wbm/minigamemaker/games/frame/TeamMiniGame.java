package com.wbm.minigamemaker.games.frame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class TeamMiniGame extends MiniGame {
	class Team {
		private List<Player> players;

	}
	/*
	 * 팀 미니게임
	 * 
	 * - 팀 개수, 팀 사이즈 설정 기능
	 * 
	 * - 구현클래스에서 initGameSetting메소드 super.override() 필수
	 * 
	 * - 팀 그룹 채팅 기능: 구현 클래스의 processEvent()에서 super.proprocessEvent()는 선택사항임
	 * 
	 * - TODO: Team class 만들어서 관리하기
	 */

//	color: redTeam, blueTeam, greenTeam, yellowTeam, blackTeam, whiteTeam, goldTeam, grayTeam;
//	private List<Player> team1, team2, team3, team4, team5, team6, team7, team8;

	private List<List<Player>> allTeams;
	private int teamCount;
	private int teamSize;
	private boolean groupChat;

	public TeamMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime, int teamCount, int teamSize,
			boolean groupChat) {
		super(title, maxPlayerCount, timeLimit, waitingTime);

		// set teamCount, teamSize
		this.fixTeamCount(teamCount);
		this.fixTeamSize(teamSize);
		// setup team
		this.setupAllTeams();
		// groupChat
		this.groupChat = groupChat;
	}

	private void setupAllTeams() {
		// allTeams 초기화
		if (this.allTeams == null) {
			this.allTeams = new ArrayList<List<Player>>();
		} else {
			this.allTeams.clear();
		}

		// teamCount만큼 팀 추가
		for (int i = 0; i < teamCount; i++) {
			this.allTeams.add(new ArrayList<Player>());
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

	protected boolean registerPlayerToTeam(Player p) {
		/*
		 * 순서대로 팀에 플레이어 추가
		 */
		for (int teamNumber = 0; teamNumber < this.teamCount; teamNumber++) {
			if (this.registerPlayerToTeam(p, teamNumber)) {
				return true;
			}
		}

		return false;
	}

	protected boolean registerPlayerToTeam(Player p, int teamNumber) {
		/*
		 * teamNumber 팀에 플레이어 추가
		 */
		// teamCount 검사
		if (teamNumber >= this.teamCount) {
			return false;
		}
		// team 가져오기
		List<Player> team = this.getTeam(teamNumber);

		// teamSize 검사
		boolean isFull = team.size() >= this.teamSize;
		if (isFull) {
			return false;
		} else {
			this.allTeams.get(teamNumber).add(p);
			return true;
		}
	}

	protected boolean unregisterPlayerFromTeam(Player p) {
		for (int teamNumber = 0; teamNumber < this.teamCount; teamNumber++) {
			List<Player> team = this.getTeam(teamNumber);
			if (team.contains(p)) {
				team.remove(p);
				return true;
			}
		}
		return false;
	}

	protected List<Player> getTeam(int teamNumber) {
		return this.allTeams.get(teamNumber);
	}

	protected List<Player> getPlayerTeam(Player p) {
		for (List<Player> team : this.allTeams) {
			if (team.contains(p)) {
				return team;
			}
		}
		return null;
	}

	protected int getTeamCount() {
		return this.teamCount;
	}

	protected int getTeamSize() {
		return this.teamSize;
	}

	@Override
	protected void initGameSetting() {
		this.setupAllTeams();
	}

	@Override
	protected void processEvent(Event event) {
		/*
		 * 그룹채팅 기능
		 * 
		 * - 사용하려면 groupChat=true와 super.processEvent()를 사용하여야 함
		 */
		if (this.groupChat) {
			if (event instanceof AsyncPlayerChatEvent) {
				AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
				// 플레이어 채팅 cancel후
				e.setCancelled(true);
				Player sender = e.getPlayer();
				// 팀 내 플레이어들에게만 채팅 전송
				List<Player> team = this.getPlayerTeam(sender);
				for (Player member : team) {
					// [Team: worldbiomusic] go go
					member.sendMessage("[Team: " + sender.getName() + "] " + e.getMessage());
				}
			}
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
