package com.worldbiomusic.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRankResult;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting;
import com.worldbiomusic.minigameworld.util.Utils;

/**
 * <b>[Info]</b><br>
 * - Minigame frame players battle with each teams<br>
 * - min team count: 2 or more <br>
 * - all teams have the same score <br>
 * - team util class, methods <br>
 * <br>
 * 
 * <b>[Custom Option]</b><br>
 * - groupChat: If true, send message only to team members (default: true)<br>
 * - teamPVP: If true, team members can not attack each other(pvp option have to
 * be set to true), (default: true)<br>
 * <br>
 * 
 * <b>[Team Register Mode]</b><br>
 * - Participants divied into teams when game starts with mode<br>
 * - See {@link TeamBattleMiniGame.TeamRegisterMode} <br>
 * <br>
 * 
 * <b>[Rule]</b><br>
 * - Create Teams with createTeams()<br>
 * - When use initGameSetting(), must call super.initGameSetting()<br>
 * - If use TeamRegisterMode.NONE, should register players to team with
 * registerPlayersToTeam()<br>
 * 
 */
public abstract class TeamBattleMiniGame extends MiniGame {

	/**
	 * Team register mode<br>
	 * - Participants divied into teams when game starts with mode<br>
	 * - If use TeamRegisterMode.NONE, should register players to team with
	 * registerPlayersToTeam()<br>
	 * <br>
	 * 
	 * <b>[Team Register Mode]</b><br>
	 * - NONE, FAIR, FILL, FAIR_FILL, RANDOM or can divide overriding
	 * "registerPlayersToTeam()"<br>
	 * <br>
	 * - e.g. playerCount: 13, teamMaxPlayerCount: 5, teamCount: 4<br>
	 * - NONE: no divide (use registerPlayersToTeam())<br>
	 * - FAIR: all teams have the same player count fairly(= maximun team count)
	 * (e.g. 4, 3, 3, 3)<br>
	 * - FILL: fulfill teams as possible from first (= minimum team count) (e.g. 5,
	 * 5, 3, 0)<br>
	 * - FAIR_FILL: FILL fairly (e.g. 5, 4, 4, 0)<br>
	 * - RANDOM: random (e.g. ?, ?, ?, ?)<br>
	 */
	public enum TeamRegisterMode {
		NONE, FAIR, FILL, FAIR_FILL, RANDOM;
	}

	/**
	 * Team list
	 */
	private List<Team> allTeams;

	/**
	 * Creates team with overriding this method
	 */
	protected abstract void createTeams();

	/**
	 * Registers players to team if TeamRegisterMode is NONE
	 */
	protected void registerPlayersToTeam() {
	}

	/**
	 * maxPlayerCount is sum of all teams member size
	 */
	public TeamBattleMiniGame(String title, int minPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, -1, timeLimit, waitingTime);

		// custom options
		this.setGroupChat(true);
		this.setTeamPVP(true);

		// setup teams
		this.initTeams();
	}

	/**
	 * Inits team settings just once
	 */
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

		// set team register FAIR_FILL as a default mode
		this.setTeamRegisterMode(TeamRegisterMode.FAIR_FILL);
	}

	/**
	 * Registers team to list
	 * 
	 * @param team Team to register
	 */
	protected void createTeam(Team team) {
		if (!this.allTeams.contains(team)) {
			this.allTeams.add(team);
		}
	}

	/**
	 * Registers teams to list
	 * 
	 * @param teams Teams to register
	 */
	protected void createTeam(Team... teams) {
		for (Team team : teams) {
			createTeam(team);
		}
	}

	/**
	 * Remove team from playing minigame
	 * 
	 * @param team Team to remove
	 */
	protected void removeTeam(Team team) {
		this.allTeams.remove(team);
	}

	/**
	 * Gets team
	 * 
	 * @param teamNumber Index of list
	 * @return Team
	 */
	protected Team getTeam(int teamNumber) {
		return allTeams.get(teamNumber);
	}

	/**
	 * Gets team
	 * 
	 * @param teamName Team name
	 * @return Team
	 */
	protected Team getTeam(String teamName) {
		for (Team team : this.allTeams) {
			if (team.getName().equals(teamName)) {
				return team;
			}
		}
		return null;
	}

	/**
	 * Gets player's team
	 * 
	 * @param p Target player
	 * @return Player team
	 */
	protected Team getTeam(Player p) {
		for (Team team : allTeams) {
			if (team.hasMember(p)) {
				return team;
			}
		}
		return null;
	}

	/**
	 * Check two team is the same team
	 * 
	 * @param p1 Player of first team
	 * @param p2 Player of second team
	 * @return True if two team is the same
	 */
	protected boolean isSameTeam(Player p1, Player p2) {
		// check p1 team has p2
		return this.getTeam(p1).hasMember(p2);
	}

	/**
	 * Gets random team
	 * 
	 * @return Random team
	 */
	protected Team getRandomTeam() {
		int randomIndex = (int) (Math.random() * this.getTeamCount());
		return this.allTeams.get(randomIndex);
	}

	/**
	 * Gets random team in given list
	 * 
	 * @param teams Teams to randomize
	 * @return Random Team
	 */
	protected Team getRandomTeam(List<Team> teams) {
		int randomIndex = (int) (Math.random() * this.getTeamCount());
		return teams.get(randomIndex);
	}

	/**
	 * Returns not empty(exist one or more players) team list
	 * 
	 * @return Not empty team list
	 */
	protected List<Team> notEmptyTeamList() {
//		List<Team> notEmptyTeams = new ArrayList<Team>();
//		this.allTeams.stream().filter(t -> !t.isEmpty()).forEach(notEmptyTeams::add);
//		return notEmptyTeams;

		return this.allTeams.stream().filter(t -> !t.isEmpty()).toList();
	}

	/**
	 * Gets not fulled random team
	 * 
	 * @return Not fulled random team
	 */
	private Team getNotFulledRandomTeams() {
		List<Team> notFulledTeams = new ArrayList<>();
		for (Team team : this.allTeams) {
			if (!team.isFull()) {
				notFulledTeams.add(team);
			}
		}
		return this.getRandomTeam(notFulledTeams);
	}

	/**
	 * Gets team which has minimum player count
	 * 
	 * @return Min player count team
	 */
	protected Team getMinPlayerCountTeam() {
		Team minTeam = this.allTeams.get(0);
		for (Team team : this.allTeams) {
			if (team.getMemberCount() < minTeam.getMemberCount()) {
				minTeam = team;
			}
		}
		return minTeam;
	}

	/**
	 * Gets team which has maximum team player count
	 * 
	 * @return Max player count team
	 */
	protected Team getMaxPlayerCountTeam() {
		Team maxTeam = this.allTeams.get(0);
		for (Team team : this.allTeams) {
			if (team.getMemberCount() > maxTeam.getMemberCount()) {
				maxTeam = team;
			}
		}
		return maxTeam;
	}

	/**
	 * Registers player to team which has minimum player count
	 * 
	 * @param p Player to join team
	 */
	protected void registerPlayerToMinPlayerCountTeam(Player p) {
		Team team = this.getMinPlayerCountTeam();
		this.registerPlayerToTeam(p, team);
	}

	/**
	 * Registers player to team which has maximum player count
	 * 
	 * @param p Player to join team
	 */
	protected void registerPlayerToMaxPlayerCountTeam(Player p) {
		Team team = this.getMaxPlayerCountTeam();
		this.registerPlayerToTeam(p, team);
	}

	/**
	 * Registers player to random team
	 * 
	 * @param p Player to join team
	 */
	protected void registerPlayerToRandomTeam(Player p) {
		Team notFulledRandomTeam = this.getNotFulledRandomTeams();
		this.registerPlayerToTeam(p, notFulledRandomTeam);
	}

	/**
	 * Registers player to team with index
	 * 
	 * @param p          Player to join team
	 * @param teamNumber Team index of list
	 * @return True if player join team, or false
	 */
	protected boolean registerPlayerToTeam(Player p, int teamNumber) {
		// check teamCount
		if (teamNumber >= this.getTeamCount()) {
			return false;
		}

		// register
		Team team = getTeam(teamNumber);
		return team.registerMember(p);
	}

	/**
	 * Registers player to team with name
	 * 
	 * @param p        Player to join team
	 * @param teamName Team index of list
	 * @return True if player join team, or false
	 */
	protected boolean registerPlayerToTeam(Player p, String teamName) {
		// register
		Team team = getTeam(teamName);
		if (team == null) {
			return false;
		}
		return team.registerMember(p);
	}

	/**
	 * Registers player to team with team instance
	 * 
	 * @param p    Player to join team
	 * @param team Team index of list
	 * @return True if player join team, or false
	 */
	protected boolean registerPlayerToTeam(Player p, Team team) {
		// register
		if (team == null) {
			return false;
		}
		return team.registerMember(p);
	}

	/**
	 * Unregisters player from team
	 * 
	 * @param p Player to unregister from team
	 * @return True if player is in any team, or false
	 */
	protected boolean unregisterPlayerFromTeam(Player p) {
		// unregister from team
		for (Team team : this.allTeams) {
			if (team.hasMember(p)) {
				return team.unregisterMember(p);
			}
		}
		return false;
	}

	/**
	 * Sets team register mode
	 * 
	 * @param teamRegisterMode TeamRegisterMode
	 */
	protected void setTeamRegisterMode(TeamRegisterMode teamRegisterMode) {
		this.getCustomData().put("teamRegisterMode", teamRegisterMode.name());
	}

	/**
	 * Gets team register mode
	 * 
	 * @return TeamRegisterMode
	 */
	protected TeamRegisterMode getTeamRegisterMode() {
		return TeamRegisterMode.valueOf((String) this.getCustomData().get("teamRegisterMode"));
	}

	/**
	 * Sets group chat
	 * 
	 * @param groupChat groupChat
	 */
	protected void setGroupChat(boolean groupChat) {
		this.getCustomData().put("groupChat", groupChat);
	}

	/**
	 * Check group chat is enabled
	 * 
	 * @return True if enabled
	 */
	protected boolean isGroupChat() {
		return (boolean) this.getCustomData().get("groupChat");
	}

	/**
	 * Sets team pvp
	 * 
	 * @param teamPvp groupChat
	 */
	protected void setTeamPVP(boolean teamPvp) {
		this.getCustomData().put("teamPvp", teamPvp);
	}

	/**
	 * Check team pvp is enabled
	 * 
	 * @return True if enabled
	 */
	protected boolean isTeamPvP() {
		return (boolean) this.getCustomData().get("teamPvp");
	}

	/**
	 * Gets all team list
	 * 
	 * @return Team list
	 */
	public List<Team> getTeamList() {
		return this.allTeams;
	}

	/**
	 * Gets all team count
	 * 
	 * @return Team count
	 */
	protected int getTeamCount() {
		return this.allTeams.size();
	}

	/**
	 * Gets live teams
	 * 
	 * @return Live teams
	 */
	protected List<Team> getLiveTeamsList() {
		List<Team> liveTeams = new ArrayList<>();
		this.allTeams.forEach(t -> {
			if (t.isTeamLive()) {
				liveTeams.add(t);
			}
		});

		return liveTeams;
	}

	/**
	 * Gets live teams count
	 * 
	 * @return Live teams count
	 */
	protected int getLiveTeamCount() {
		return this.getLiveTeamsList().size();
	}

	/**
	 * Plus team score with team index
	 * 
	 * @param teamNumber Team index
	 * @param amount     Amount to plus
	 */
	protected void plusTeamScore(int teamNumber, int amount) {
		Team team = this.getTeam(teamNumber);
		team.plusTeamScore(amount);
	}

	/**
	 * Plus score of the team which player belongs
	 * 
	 * @param p      Team of Player
	 * @param amount Amount to plus
	 */
	protected void plusTeamScore(Player p, int amount) {
		Team team = this.getTeam(p);
		team.plusTeamScore(amount);
	}

	/**
	 * Minus team score with team index
	 * 
	 * @param teamNumber Team index
	 * @param amount     Amount to minus
	 */
	protected void minusTeamScore(int teamNumber, int amount) {
		Team team = this.getTeam(teamNumber);
		team.minusTeamScore(amount);
	}

	/**
	 * Minus score of the team which player belongs
	 * 
	 * @param p      Team of Player
	 * @param amount Amount to minus
	 */
	protected void minusTeamScore(Player p, int amount) {
		Team team = this.getTeam(p);
		team.minusTeamScore(amount);
	}

	/**
	 * Gets team score with index
	 * 
	 * @param teamNumber Team index
	 * @return Team score
	 */
	protected int getTeamScore(int teamNumber) {
		Team team = this.getTeam(teamNumber);
		return team.getScore();
	}

	/**
	 * Gets score of team which player belongs
	 * 
	 * @param p Team of player
	 * @return Team score
	 */
	protected int getTeamScore(Player p) {
		Team team = this.getTeam(p);
		return team.getScore();
	}

	/**
	 * Counting unit changed: "player" to "team"<br>
	 * <b>[IMPORTANT]</b> Must override {@link MiniGame#isLessThanPlayersLive},
	 * because will be checked in {@link MiniGame#checkGameFinishCondition} and
	 * {@link MiniGamePlayerData#setLive(boolean)}<br>
	 */
	@Override
	protected boolean isLessThanPlayersLive() {
		return getLiveTeamCount() < getSetting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Same with the {@link TeamBattleMiniGame#isLessThanPlayersLive()}
	 * 
	 * @return True if live teams are less than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isLessThanTeamsLive() {
		return isLessThanPlayersLive();
	}

	/**
	 * Counting unit changed: "player" to "team"<br>
	 * <b>[IMPORTANT]</b> Must override {@link MiniGame#isMoreThanPlayersLive},
	 * because will be checked in {@link MiniGame#checkGameFinishCondition} and
	 * {@link MiniGamePlayerData#setLive(boolean)}<br>
	 */
	@Override
	protected boolean isMoreThanPlayersLive() {
		return getLiveTeamCount() > getSetting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Same with the {@link TeamBattleMiniGame#isMoreThanPlayersLive()}
	 * 
	 * @return True if live teams are more than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isMoreThanTeamsLive() {
		return isMoreThanPlayersLive();
	}

	/**
	 * Counting unit changed: "player" to "team"<br>
	 * <b>[IMPORTANT]</b> Must override {@link MiniGame#isLessThanPlayersLeft},
	 * because will be checked in {@link MiniGame#checkGameFinishCondition} and
	 * {@link MiniGamePlayerData#setLive(boolean)}<br>
	 */
	@Override
	protected boolean isLessThanPlayersLeft() {
		return notEmptyTeamList().size() < getSetting().getGameFinishConditionPlayerCount();
	}

	/**
	 * Same with the {@link TeamBattleMiniGame#isLessThanPlayersLeft()}
	 * 
	 * @return True if left teams are less than
	 *         {@link MiniGameSetting#getGameFinishConditionPlayerCount()}
	 */
	protected boolean isLessThanTeamsLeft() {
		return isLessThanPlayersLeft();
	}

	@Override
	protected void initGameSettings() {
		createTeams();
	}

	@Override
	protected void runTaskAfterStart() {
		// register players to teams
		switch (this.getTeamRegisterMode()) {
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

		// remove empty teams
		this.allTeams.removeIf(team -> team.isEmpty());
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = FAIR
	 */
	private void registerPlayers_FAIR() {
		this.getPlayers().forEach(p -> this.registerPlayerToMinPlayerCountTeam(p));
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = FILL
	 */
	private void registerPlayers_FILL() {
		this.getPlayers().forEach(p -> this.registerPlayerToMaxPlayerCountTeam(p));
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = FAIR_FILL
	 */
	private void registerPlayers_FAIR_FILL() {
		// [IMPORTANT] All teams must have the same max player count
		int teamMemberCount = this.allTeams.get(0).maxCount();
		int usingTeamCount = (int) Math.ceil((double) this.getPlayerCount() / teamMemberCount);
		if (usingTeamCount == 1) {
			usingTeamCount += 1;
		}
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

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = RANDOM
	 */
	private void registerPlayers_RANDOM() {
		this.getPlayers().forEach(p -> this.registerPlayerToRandomTeam(p));
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof EntityDamageByEntityEvent) {
			if (this.isTeamPvP()) {
				return;
			}

			// cancel damage by entity
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			Entity victim = e.getEntity();
			Entity damager = e.getDamager();
			if (!(victim instanceof Player)) {
				return;
			}

			// direct damage
			if (damager instanceof Player) {
				if (this.isSameTeam((Player) victim, (Player) damager)) {
					Utils.debug("cancel same team damage");
					e.setCancelled(true);
				}
			}
			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				if (!(proj.getShooter() instanceof Player)) {
					return;
				}
				if (this.isSameTeam((Player) victim, (Player) proj.getShooter())) {
					Utils.debug("cancel projectile event");
					e.setCancelled(true);
				}
			}
		}
	}

	@Override
	protected void processChatting(AsyncPlayerChatEvent e) {
		if (!this.isStarted()) {
			return;
		}

		// group chat
		if (this.isGroupChat()) {
			// cancel event
			e.setCancelled(true);

			Player sender = e.getPlayer();

			// send message to only team members
			Team team = this.getTeam(sender);
			// ex. [Title] worldbiomusic: go go
			team.sendTeamMessage(sender,
					"(" + ChatColor.GOLD + "Group-Chat" + ChatColor.RESET + "): " + e.getMessage());
		} else {
			Player p = e.getPlayer();
			String msg = e.getMessage();
			Team team = getTeam(p);
			String teamString = "(" + team.getColoredTeamName() + ")";
			e.setMessage(teamString + ": " + msg);
		}
	}

	@Override
	protected void printScore() {
		// print team score in descending order
		BroadcastTool.sendMessage(this.getPlayers(), ChatColor.BOLD + "[Score]");

		// rank team by score
		@SuppressWarnings("unchecked")
		List<Team> entries = (List<Team>) this.getRank();

		int rank = 1;
		ChatColor[] rankColors = { ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE };

		for (Team team : entries) {
			int score = team.getScore();
			String memberString = team.getAllMemberNameString();

			// rank string with color
			String rankString = "[";
			if (rank <= 3) {
				rankString += rankColors[rank - 1];
			}
			rankString += rank + "" + ChatColor.RESET + "] ";

			BroadcastTool.sendMessage(this.getPlayers(),
					rankString + "Team(" + memberString + ")" + ": " + ChatColor.GOLD + score);
			rank += 1;
		}

	}

	/**
	 * Gets team rank
	 * 
	 * @return Ordered team list by score
	 */
	@Override
	public List<? extends MiniGameRankResult> getRank() {
		List<Team> notEmptyTeams = new ArrayList<>();
		for (Team t : this.allTeams) {
			if (!t.isEmpty()) {
				notEmptyTeams.add(t);
			}
		}
		Collections.sort(notEmptyTeams);
		return notEmptyTeams;
	}

	@Override
	protected void handleGameException(Player p, Exception exception) {
		super.handleGameException(p, exception);

		if (isStarted()) {
			// remove player from team
			Team team = getTeam(p);
			team.unregisterMember(p);
		}
	}

	@Override
	public String getType() {
		return "TeamBattle";
	}

	/**
	 * Team which used in TeamBattleMiniGame frame<br>
	 * Manage: teamName, maxMemberCount, members, color
	 *
	 */
	public class Team implements MiniGameRankResult {
		private String teamName;
		private int maxMemberCount;
		private List<Player> members;
		private ChatColor color;

		/**
		 * Member size is needed for calculating maxPlayerCount of minigame
		 * 
		 * @param teamName   Team name
		 * @param memberSize Team max member size
		 */
		public Team(String teamName, int memberSize) {
			this.teamName = teamName;
			this.maxMemberCount = memberSize;
			this.members = new ArrayList<>(memberSize);
			this.color = ChatColor.WHITE;
		}

		/**
		 * Member size is needed for calculating maxPlayerCount of minigame
		 * 
		 * @param teamName   Team name
		 * @param memberSize Team max member size
		 */
		public Team(String teamName, int memberSize, ChatColor color) {
			this.teamName = teamName;
			this.maxMemberCount = memberSize;
			this.members = new ArrayList<>(memberSize);
			this.color = color;
		}

		/**
		 * Check team has players
		 * 
		 * @return True if team has no players
		 */
		public boolean isEmpty() {
			return this.getMemberCount() == 0;
		}

		/**
		 * Check team has players
		 * 
		 * @return True if team players are full
		 */
		public boolean isFull() {
			return this.getMemberCount() == this.maxMemberCount;
		}

		/**
		 * Gets team members
		 * 
		 * @return Player list
		 */
		public List<Player> getMembers() {
			return this.members;
		}

		/**
		 * Sends message to team members
		 * 
		 * @param sender Player from send
		 * @param msg    Message to send
		 */
		public void sendTeamMessage(Player sender, String msg) {
			if (this.hasMember(sender)) {
				this.getMembers().forEach(p -> sendMessage(p,
						sender.getName() + "(" + this.color + this.teamName + ChatColor.WHITE + ")" + ": " + msg));
			}
		}

		/**
		 * Gets team score
		 * 
		 * @return
		 */
		@Override
		public int getScore() {
			return TeamBattleMiniGame.this.getScore(this.getRandomMember());
		}

		/**
		 * Plus team score
		 * 
		 * @param amount Amount to plus
		 */
		public void plusTeamScore(int amount) {
			this.getMembers().forEach(p -> TeamBattleMiniGame.super.plusScore(p, amount));
		}

		/**
		 * Minus team score
		 * 
		 * @param amount Amount to minus
		 */
		public void minusTeamScore(int amount) {
			this.getMembers().forEach(p -> TeamBattleMiniGame.super.minusScore(p, amount));
		}

		/**
		 * Registers member to this team
		 * 
		 * @param p Player to register
		 * @return False if team players are full, or true
		 */
		private boolean registerMember(Player p) {
			// check teamSize
			if (this.isFull()) {
				return false;
			}

			if (this.members.contains(p)) {
				return false;
			}

			this.members.add(p);
			return true;

		}

		/**
		 * Unregisters player from this team
		 * 
		 * @param p Player to unregister
		 * @return False if team doesn't has player
		 */
		private boolean unregisterMember(Player p) {
			return this.members.remove(p);
		}

		/**
		 * Check team has player
		 * 
		 * @param p Player to check
		 * @return True if team has player
		 */
		public boolean hasMember(Player p) {
			return this.getMembers().contains(p);
		}

		/**
		 * Gets random player of team members
		 * 
		 * @return Random team member
		 */
		public Player getRandomMember() {
			int randomIndex = (int) (Math.random() * this.getMemberCount());
			return this.getMembers().get(randomIndex);
		}

		/**
		 * Gets all members name string
		 * 
		 * @return All members name string
		 */
		public String getAllMemberNameString() {
			return PlayerTool.getPlayersNameString(this.members, ",");
		}

		/**
		 * Gets team color
		 * 
		 * @return Team color
		 */
		public ChatColor getColor() {
			return color;
		}

		/**
		 * Sets team color
		 * 
		 * @param color Team color
		 */
		public void setColor(ChatColor color) {
			this.color = color;
		}

		/**
		 * Check team is live
		 * 
		 * @return True if any member is live, or false if all of members are death
		 */
		public boolean isTeamLive() {
//			for (Player member : this.members) {
//				if (!getLivePlayers().contains(member)) {
//					return false;
//				}
//			}
//			return true;

			for (Player member : this.members) {
				if (!isLive(member)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Gets live member list
		 * 
		 * @return Live member list
		 */
		public List<Player> getLiveMemberList() {
			List<Player> liveMembers = new ArrayList<>();
			for (Player member : this.members) {
				if (getLivePlayers().contains(member)) {
					liveMembers.add(member);
				}
			}
			return liveMembers;
		}

		/**
		 * Gets live members count
		 * 
		 * @return Count of live members
		 */
		public int getLiveMemberCount() {
			return this.getLiveMemberList().size();
		}

		/**
		 * Gets team name
		 * 
		 * @return
		 */
		public String getName() {
			return teamName;
		}

		public String getColoredTeamName() {
			return this.color + this.teamName + ChatColor.RESET;
		}

		/**
		 * Gets members count
		 * 
		 * @return
		 */
		public int getMemberCount() {
			return this.members.size();
		}

		/**
		 * Gets team member max size
		 * 
		 * @return Max size of team
		 */
		public int maxCount() {
			return this.maxMemberCount;
		}

		@Override
		public List<Player> getPlayers() {
			return getMembers();
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			} else if (this == other) {
				return true;
			} else if (other instanceof Team) {
				Team otherTeam = (Team) other;
				return getName().equals(otherTeam.getName());
			}

			return false;
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
