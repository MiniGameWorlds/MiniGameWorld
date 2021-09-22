package com.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.minigameworld.util.Utils;
import com.wbm.plugin.util.SortTool;

@SuppressWarnings("deprecation")
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
	 * - whem use processEvent(), must call super.processEvent()
	 * 
	 */

	private List<Team> allTeams;
	// team count
	private int teamCount;
	// players per team
	private int teamSize;

	private boolean groupChat;

	// distribute players according to teamCount and teamSize
	private boolean autoTeamSetup;

	// registe team
	protected abstract void registerAllPlayersToTeam();

	public TeamBattleMiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime,
			int teamCount, int teamSize) {
		super(title, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);

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
		this.autoTeamSetup = autoTeamSetup;
	}

	private void setupAllTeams() {
		// init allTeams
		if (this.allTeams == null) {
			this.allTeams = new ArrayList<Team>();
		} else {
			this.allTeams.clear();
		}

		// add "teamCount" teams
		for (int i = 0; i < this.teamCount; i++) {
			this.allTeams.add(new Team());
		}
	}

	protected void fixTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}

	protected void fixTeamSize(int teamSize) {
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
		// compare with "=="(memory ref)
		return this.getPlayerTeam(p1) == this.getPlayerTeam(p2);
	}

	protected boolean registerPlayerWithTeam(Player p) {
		// register player to team in order
		for (int teamNumber = 0; teamNumber < teamCount; teamNumber++) {
			if (this.registerPlayerWithTeam(p, teamNumber)) {
				return true;
			}
		}

		return false;
	}

	protected boolean registerPlayerWithTeam(Player p, int teamNumber) {
		// check teamCount
		if (teamNumber >= teamCount) {
			return false;
		}

		// register
		Team team = getTeam(teamNumber);
		return team.registerMember(p);
	}

	protected boolean unregisterPlayerFromTeam(Player p) {
		// unregister from team
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
		// valid team = team which has 1 player or more count
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

	protected void minusTeamScore(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		team.minusTeamScore(score);
	}

	protected int getTeamScore(int teamNumber) {
		Team team = this.getTeam(teamNumber);
		return team.getTeamScore();
	}

	@Override
	protected void initGameSettings() {
		this.setupAllTeams();
	}

	@Override
	protected void runTaskAfterStart() {
		// auto team setup: register all players to each teams
		if (this.autoTeamSetup) {
			int teamNumber = 0;
			for (Player p : this.getPlayers()) {
				this.registerPlayerWithTeam(p, teamNumber);
				teamNumber = (teamNumber + 1) % this.teamCount;
			}
		} else {
			this.registerAllPlayersToTeam();
		}
	}

	@Override
	protected void processEvent(Event event) {
		// group chat
		if (this.groupChat) {
			if (event instanceof AsyncPlayerChatEvent) {
				AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
				// cancel event
				e.setCancelled(true);
				Player sender = e.getPlayer();

				// send message to only team members
				Team team = this.getPlayerTeam(sender);
				// ex. [Title] worldbiomusic: go go
				team.sendTeamMessage(sender, e.getMessage());
			}
		}
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
		// waitingTime
		if (this.getWaitingTime() <= 0) {
			Utils.warning(this.getTitleWithClassName() + ": waitingTime must be at least 1 sec");
		}
		// maxPlayerCount
		if (this.getMaxPlayerCount() <= 1) {
			Utils.warning(this.getTitleWithClassName() + ": maxPlayer is recommended at least 2 players");
		}
	}

	@Override
	protected void printScore() {
		// print team score in descending order
		this.sendMessageToAllPlayers("[Score]");

		// add each player of all teams (for sorting score by team)
		Map<Player, Integer> eachValidTeamPlayer = new HashMap<Player, Integer>();
		for (Team team : this.allTeams) {
			if (this.isValidTeam(team)) {
				Player member = team.getMembers().get(0);
				int teamScore = team.getTeamScore();
				eachValidTeamPlayer.put(member, teamScore);
			}
		}

		// rank team by score
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
			// check teamSize
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
			// remove last ", "
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
