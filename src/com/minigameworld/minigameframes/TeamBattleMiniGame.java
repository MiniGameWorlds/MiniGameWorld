package com.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.wbm.plugin.util.SortTool;

public abstract class TeamBattleMiniGame extends MiniGame {

	/*
	 * 
	 * [Info]
	 * - team battle play
	 * - team has same score
	 * - team util class, methods
	 * - groupChat
	 * - Team Register Method: NONE, FAIR, FILL, FAIR_FILL, RANDOM or can divide overriding registerPlayersToTeam()
	 * > e.g. playerCount: 13, teamMaxPlayerCount: 5, teamCount: 4
	 * > NONE: no divide (use registerPlayersToTeam())
	 * > FAIR: all teams have the same player count (= maximun team count) (e.g. 4, 3, 3, 3)
	 * > FILL: fulfill teams as possible (= minimum team count) (e.g. 5, 5, 3, 0)
	 * > FAIR_FILL: FILL fairly (e.g. 5, 4, 4, 0)
	 * > RANDOM: random (e.g. ?, ?, ?, ?)
	 * 
	 * [Rule]
	 * - create Teams with createTeams()
	 * - When use initGameSetting(), must call super.initGameSetting()
	 * - If use TeamRegisterMethod.NONE, register players to team using registerPlayersToTeam()
	 * - When use handleGameException(), must call super.handleGameException()
	 * 
	 */

	public enum TeamRegisterMethod {
		NONE, FAIR, FILL, FAIR_FILL, RANDOM;
	}

	private List<Team> allTeams;
//	private TeamRegisterMethod teamRegisterMethod;

	protected abstract void createTeams();

	protected void registerPlayersToTeam() {
	}

	public TeamBattleMiniGame(String title, int minPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, -1, timeLimit, waitingTime);

		// group chat
		this.getCustomData().put("groupChat", true);

		// setup teams
		this.initTeams();
	}

	private void initTeams() {
		// create teams
		this.allTeams = new ArrayList<Team>();
		this.createTeams();

		// set maxPlayerCount with sum of all team members
		int allMemberCount = 0;
		for (Team team : this.allTeams) {
			allMemberCount += team.maxCount();
		}
		this.getSetting().setMaxPlayerCount(allMemberCount);

		// set team register method
//		this.teamRegisterMethod = TeamRegisterMethod.FAIR_FILL;
		this.getCustomData().put("TeamRegisterMethod", TeamRegisterMethod.FAIR_FILL.name());
	}

	/*
	 * team
	 */
	private void emptyAllTeams() {
		this.allTeams.forEach(t -> t.emptyMembers());
	}

	protected void createTeam(Team team) {
		this.allTeams.add(team);
	}

	protected Team getTeam(int teamNumber) {
		return allTeams.get(teamNumber);
	}

	protected Team getTeam(String teamName) {
		for (Team team : this.allTeams) {
			if (team.getTeamName().equals(teamName)) {
				return team;
			}
		}
		return null;
	}

	protected Team getTeam(Player p) {
		for (Team team : allTeams) {
			if (team.hasMember(p)) {
				return team;
			}
		}
		return null;
	}

	protected boolean isSameTeam(Player p1, Player p2) {
		// check p1 team has p2
		return this.getTeam(p1).hasMember(p2);
	}

	protected Team getRandomTeam() {
		int randomIndex = (int) (Math.random() * this.getTeamCount());
		return this.allTeams.get(randomIndex);
	}

	protected Team getRandomTeam(List<Team> teams) {
		int randomIndex = (int) (Math.random() * this.getTeamCount());
		return teams.get(randomIndex);
	}

	private Team getNotFulledRandomTeams() {
		List<Team> notFulledTeams = new ArrayList<>();
		for (Team team : this.allTeams) {
			if (!team.isFull()) {
				notFulledTeams.add(team);
			}
		}
		return this.getRandomTeam(notFulledTeams);
	}

	protected Team getMinPlayerCountTeam() {
		Team minTeam = this.allTeams.get(0);
		for (Team team : this.allTeams) {
			if (team.getPlayerCount() < minTeam.getPlayerCount()) {
				minTeam = team;
			}
		}
		return minTeam;
	}

	protected Team getMaxPlayerCountTeam() {
		Team maxTeam = this.allTeams.get(0);
		for (Team team : this.allTeams) {
			if (team.getPlayerCount() > maxTeam.getPlayerCount()) {
				maxTeam = team;
			}
		}
		return maxTeam;
	}

	/*
	 * register team
	 */
	protected void registerPlayerToMinPlayerCountTeam(Player p) {
		Team team = this.getMinPlayerCountTeam();
		this.registerPlayerToTeam(p, team);
	}

	protected void registerPlayerToMaxPlayerCountTeam(Player p) {
		Team team = this.getMaxPlayerCountTeam();
		this.registerPlayerToTeam(p, team);
	}

	protected void registerPlayerToRandomTeam(Player p) {
		Team notFulledRandomTeam = this.getNotFulledRandomTeams();
		this.registerPlayerToTeam(p, notFulledRandomTeam);
	}

	protected boolean registerPlayerToTeam(Player p, int teamNumber) {
		// check teamCount
		if (teamNumber >= this.getTeamCount()) {
			return false;
		}

		// register
		Team team = getTeam(teamNumber);
		return team.registerMember(p);
	}

	protected boolean registerPlayerToTeam(Player p, String teamName) {
		// register
		Team team = getTeam(teamName);
		if (team == null) {
			return false;
		}
		return team.registerMember(p);
	}

	protected boolean registerPlayerToTeam(Player p, Team team) {
		// register
		if (team == null) {
			return false;
		}
		return team.registerMember(p);
	}

	protected boolean unregisterPlayerFromTeam(Player p) {
		// unregister from team
		for (Team team : this.allTeams) {
			if (team.hasMember(p)) {
				return team.unregisterMember(p);
			}
		}
		return false;
	}

	/*
	 * setter, getter
	 */
	protected void setTeamRegisterMethod(TeamRegisterMethod teamRegisterMethod) {
//		this.teamRegisterMethod = teamRegisterMethod;
		this.getCustomData().put("TeamRegisterMethod", teamRegisterMethod.name());
	}

	protected void setGroupChat(boolean groupChat) {
		this.getCustomData().put("groupChat", groupChat);
	}

	protected boolean isGroupChat() {
		return (boolean) this.getCustomData().get("groupChat");
	}

	protected List<Team> getTeamList() {
		return this.allTeams;
	}

	protected int getTeamCount() {
		return this.allTeams.size();
	}

	/*
	 * score
	 */
	protected void plusTeamScore(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		team.plusTeamScore(score);
	}

	protected void plusTeamScore(Player p, int score) {
		Team team = this.getTeam(p);
		team.plusTeamScore(score);
	}

	@Override
	protected void plusScore(Player p, int score) {
		this.plusTeamScore(p, score);
	}

	protected void minusTeamScore(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		team.minusTeamScore(score);
	}

	protected void minusTeamScore(Player p, int score) {
		Team team = this.getTeam(p);
		team.minusTeamScore(score);
	}

	@Override
	protected void minusScore(Player p, int score) {
		this.minusTeamScore(p, score);
	}

	protected int getTeamScore(int teamNumber) {
		Team team = this.getTeam(teamNumber);
		return team.getTeamScore();
	}

	protected int getTeamScore(Player p) {
		Team team = this.getTeam(p);
		return team.getTeamScore();
	}

	/*
	 * check
	 */
	protected boolean isValidTeam(Team team) {
		// valid team = team which has 1 player or more
		return !team.isEmpty();
	}

	protected int getValidTeamCount() {
		int count = 0;
		for (Team team : this.allTeams) {
			if (this.isValidTeam(team)) {
				count++;
			}
		}
		return count;
	}

	private boolean checkAtLeastTeamRemains(int count) {
		if (this.getValidTeamCount() <= count) {
			this.sendMessageToAllPlayers("Game End: only " + this.getValidTeamCount() + " team remains");
			this.endGame();
			return true;
		}
		return false;
	}

	@Override
	protected void handleGameException(Player p, Exception exception, Object arg) {
		super.handleGameException(p, exception, arg);
		this.checkAtLeastTeamRemains(1);
	}

	@Override
	protected void initGameSettings() {
		this.emptyAllTeams();
	}

	@Override
	protected void runTaskAfterStart() {
		String methodString = (String) this.getCustomData().get("TeamRegisterMethod");
		TeamRegisterMethod method = TeamRegisterMethod.valueOf(methodString);
		switch (method) {
		case NONE:
			this.registerPlayersToTeam();
			break;
		case FAIR:
			this.registerPlayers_FAIR();
			break;
		case FILL:
			this.registerPlayers_FILL();
			break;
		case FAIR_FILL:
			this.registerPlayers_FAIR_FILL();
			break;
		case RANDOM:
			this.registerPlayers_RANDOM();
			break;
		}
	}

	private void registerPlayers_FAIR() {
		this.getPlayers().forEach(p -> this.registerPlayerToMinPlayerCountTeam(p));
	}

	private void registerPlayers_FILL() {
		this.getPlayers().forEach(p -> this.registerPlayerToMaxPlayerCountTeam(p));
	}

	private void registerPlayers_FAIR_FILL() {
		// [IMPORTANT] suppose that all teams has the same max player count
		int teamMemberCount = this.allTeams.get(0).maxCount();
		int usingTeamCount = (int) Math.ceil((double) this.getPlayerCount() / teamMemberCount);
		int memberCountPerTeam = this.getPlayerCount() / usingTeamCount;

		int teamNumber = 0;
		int playerNumber = 0;
		while (true) {
			if (teamNumber >= usingTeamCount) {
				break;
			}
			Player p = this.getPlayers().get(playerNumber);
			this.registerPlayerToTeam(p, teamNumber);

			playerNumber += 1;
			if (playerNumber % memberCountPerTeam == 0) {
				teamNumber += 1;
			}
		}

		// register remain players to `min player count teams`
		while (playerNumber < this.getPlayerCount()) {
			Player p = this.getPlayers().get(playerNumber);
			this.registerPlayerToMinPlayerCountTeam(p);
			playerNumber += 1;
		}
	}

	private void registerPlayers_RANDOM() {
		this.getPlayers().forEach(p -> this.registerPlayerToRandomTeam(p));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void processChatting(PlayerChatEvent e) {
		// group chat
		if (this.isStarted() && this.isGroupChat()) {
			// cancel event
			e.setCancelled(true);
			Player sender = e.getPlayer();

			// send message to only team members
			Team team = this.getTeam(sender);
			// ex. [Title] worldbiomusic: go go
			team.sendTeamMessage(sender, e.getMessage());
		}
	}

	@Override
	protected void printScore() {
		// print team score in descending order
		this.sendMessageToAllPlayers(ChatColor.BOLD + "[Score]");

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
			Team team = this.getTeam(p);
			String memberString = team.getAllMemberNameString();
			this.sendMessageToAllPlayers("[" + rank + "] " + "Team(" + memberString + ")" + ": " + score);
			rank += 1;
		}

	}

	public class Team {
		private String teamName;
		private int maxMemberCount;
		private List<Player> members;
		private ChatColor color;

		public Team(String teamName, int memberSize) {
			this.teamName = teamName;
			this.maxMemberCount = memberSize;
			this.members = new ArrayList<Player>(memberSize);
		}

		public void emptyMembers() {
			this.members.clear();
		}

		public boolean isEmpty() {
			return this.getPlayerCount() == 0;
		}

		public boolean isFull() {
			return this.getPlayerCount() == this.maxMemberCount;
		}

		public List<Player> getMembers() {
			return this.members;
		}

		public void sendTeamMessage(Player sender, String msg) {
			this.members.forEach(p -> sendMessage(p,
					sender.getName() + "(" + this.color + this.teamName + ChatColor.WHITE + ")" + ": " + msg));
		}

		public int getTeamScore() {
			return getScore(this.members.get(0));
		}

		public void plusTeamScore(int score) {
			this.getMembers().forEach(p -> TeamBattleMiniGame.super.plusScore(p, score));
		}

		public void minusTeamScore(int score) {
			this.getMembers().forEach(p -> TeamBattleMiniGame.super.minusScore(p, score));
		}

		private boolean registerMember(Player p) {
			// check teamSize
			if (this.isFull()) {
				return false;
			} else {
				this.members.add(p);
				return true;
			}
		}

		private boolean unregisterMember(Player p) {
			return this.members.remove(p);
		}

		public boolean hasMember(Player p) {
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

		public ChatColor getColor() {
			return color;
		}

		public void setColor(ChatColor color) {
			this.color = color;
		}

		public String getTeamName() {
			return teamName;
		}

		public int getPlayerCount() {
			return this.members.size();
		}

		public int maxCount() {
			return this.maxMemberCount;
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
