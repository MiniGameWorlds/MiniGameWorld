package com.worldbiomusic.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.wbm.plugin.util.ChatColorTool;
import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGamePlayerExceptionEvent;
import com.worldbiomusic.minigameworld.managers.party.Party;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.TeamBattleMiniGame.Team;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRank;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting;
import com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard.MiniGameScoreboardSidebarUpdater;

/**
 * <b>[Info]</b><br>
 * - Minigame frame players battle with each teams<br>
 * - min team count: 2 or more <br>
 * - all teams have the same score <br>
 * - team util class, methods <br>
 * <br>
 * 
 * <b>[Custom Option]</b><br>
 * - group-chat: If true, send message only to team members (default: true)<br>
 * - team-pvp: If true, team members can not attack each other(pvp option have
 * to be set to true), (default: true)<br>
 * - team-size: Related with {@link MiniGameSetting#getMaxPlayers()}, if
 * "max-players" is 12 and "team-size" is 4 then, 3 teams are created
 * automatically<br>
 * <br>
 * 
 * <b>[Team Register Mode]</b><br>
 * - Participants divied into teams when game starts with mode<br>
 * - See {@link TeamBattleMiniGame.TeamRegisterMode} <br>
 * <br>
 * 
 * <b>[Rule]</b><br>
 * - Create Teams with createTeams()<br>
 * - When use initGameSettings(), must call super.initGameSettings()<br>
 * - If need different team types, override {@link #createTeams()} without
 * "super.createTeams()" and create custom teams (e.g. Boss player team vs
 * challengers team) (recommended creating teams with teamSize())<br>
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
	 * - NONE: no divide (use {@link #registerPlayersToTeam()})<br>
	 * - FAIR: all teams have the same player count fairly(= maximun team count)
	 * (e.g. 4, 3, 3, 3)<br>
	 * - FILL: fulfill teams as possible from first (= minimum team count) (e.g. 5,
	 * 5, 3, 0)<br>
	 * - FAIR_FILL: FILL fairly (e.g. 5, 4, 4, 0)<br>
	 * - RANDOM: random (e.g. ?, ?, ?, ?)<br>
	 * - PARTY: party members have the same team (only "max-players /
	 * team-size" party can join the game<br>
	 */
	public enum TeamRegisterMode {
		NONE, FAIR, FILL, FAIR_FILL, RANDOM, PARTY;
	}

	/**
	 * Team list
	 */
	private List<Team> allTeams;

	/**
	 * Registers players to team if TeamRegisterMode is NONE
	 */
	protected void registerPlayersToTeam() {
	}

	/**
	 * maxPlayers is sum of all teams member size
	 */
	public TeamBattleMiniGame(String title, int minPlayers, int maxPlayers, int playTime, int waitingTime) {
		super(title, minPlayers, maxPlayers, playTime, waitingTime);

		this.allTeams = new ArrayList<Team>();

		// set default value of custom options
		setGroupChat(true);
		setTeamPVP(false);
		setTeamSize(2);
		setTeamRegisterMode(TeamRegisterMode.FAIR_FILL);

		// set custom team battle scoreboard updater
		getScoreboardManager().setPlayScoreboardUpdater(new TeamBattleMiniGameScoreboardUpdater(this));
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
	 * Check player has team or not
	 * 
	 * @param p Player to check
	 * @return True if player has a team
	 */
	protected boolean hasTeam(Player p) {
		return getTeam(p) != null;
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
		return this.allTeams.stream().filter(t -> !t.isEmpty()).toList();
	}

	/**
	 * Gets not fulled random team
	 * 
	 * @return Not fulled random team
	 */
	private Team getNotFulledRandomTeams() {
		return this.getRandomTeam(this.allTeams.stream().filter(t -> !t.isFull()).toList());
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
		this.getCustomData().put("team-register-mode", teamRegisterMode.name());
	}

	/**
	 * Gets team register mode
	 * 
	 * @return TeamRegisterMode
	 */
	public TeamRegisterMode getTeamRegisterMode() {
		return TeamRegisterMode.valueOf((String) this.getCustomData().get("team-register-mode"));
	}

	/**
	 * Sets group chat
	 * 
	 * @param groupChat groupChat
	 */
	protected void setGroupChat(boolean groupChat) {
		this.getCustomData().put("group-chat", groupChat);
	}

	/**
	 * Check group chat is enabled
	 * 
	 * @return True if enabled
	 */
	protected boolean isGroupChat() {
		return (boolean) this.getCustomData().get("group-chat");
	}

	/**
	 * Sets team pvp (contains projectile damage)<br>
	 * If true, team members can damage each other<br>
	 * If false, team members can NOT damage each other<br>
	 * 
	 * @param teamPvp groupChat
	 */
	protected void setTeamPVP(boolean teamPvp) {
		this.getCustomData().put("team-pvp", teamPvp);
	}

	/**
	 * Check team pvp is enabled
	 * 
	 * @return True if enabled
	 */
	protected boolean isTeamPvP() {
		return (boolean) this.getCustomData().get("team-pvp");
	}

	/**
	 * Set team size<br>
	 * Example) max-players: 10, team-size: 2 -> There are 5 teams<br>
	 * 
	 * @param teamSize Team size
	 */
	protected void setTeamSize(int teamSize) {
		getCustomData().put("team-size", teamSize);
	}

	/**
	 * Get team size
	 * 
	 * @return Team size
	 */
	public int getTeamSize() {
		return (int) getCustomData().get("team-size");
	}

	public int getTeamCountLimit() {
		return getMaxPlayers() / getTeamSize();
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
		return this.allTeams.stream().filter(t -> t.isTeamLive()).toList();
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
		this.allTeams.clear();
		createTeams();
	}

	/**
	 * Creates team with overriding this method
	 */
	protected void createTeams() {
		List<ChatColor> colors = new ArrayList<ChatColor>();
		colors.addAll(Arrays.asList(new ChatColor[] { ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
				ChatColor.BLACK, ChatColor.WHITE, ChatColor.GRAY, ChatColor.AQUA, ChatColor.GOLD, ChatColor.DARK_AQUA,
				ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE,
				ChatColor.DARK_RED, }));

		int teamCount = getMaxPlayers() / getTeamSize();
		for (int i = 0; i < teamCount; i++) {
			Team team;
			if (i < colors.size()) {
				ChatColor color = colors.get(i);
				team = new Team(color.name(), getTeamSize(), color);
			} else {
				team = new Team("Team" + i, getTeamSize(), ChatColorTool.random());
			}

			createTeam(team);
		}
	}

	@Override
	protected void onStart() {
		// register players to teams
		switch (this.getTeamRegisterMode()) {
		case NONE:
			registerPlayersToTeam();
			break;
		case FAIR:
			registerPlayers_FAIR();
			break;
		case FILL:
			registerPlayers_FILL();
			break;
		case FAIR_FILL:
			registerPlayers_FAIR_FILL();
			break;
		case RANDOM:
			registerPlayers_RANDOM();
			break;
		case PARTY:
			registerPlayers_PARTY();
			break;
		}

		// remove empty teams
		this.allTeams.removeIf(team -> team.isEmpty());
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = FAIR
	 */
	private void registerPlayers_FAIR() {
		// shuffle player list
		List<Player> players = new ArrayList<>();
		getPlayers().forEach(players::add);
		Collections.shuffle(players);

		players.forEach(p -> this.registerPlayerToMinPlayerCountTeam(p));
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = FILL
	 */
	private void registerPlayers_FILL() {
		// shuffle player list
		List<Player> players = new ArrayList<>();
		getPlayers().forEach(players::add);
		Collections.shuffle(players);

		players.forEach(p -> this.registerPlayerToMaxPlayerCountTeam(p));
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = FAIR_FILL
	 */
	private void registerPlayers_FAIR_FILL() {
		// [IMPORTANT] All teams must have the same max player count
		int teamMemberCount = this.allTeams.get(0).maxCount();
		int maxTeamCount = (int) Math.ceil((double) this.getPlayerCount() / teamMemberCount);
		if (maxTeamCount == 1) {
			maxTeamCount += 1;
		}
		int memberCountPerTeam = this.getPlayerCount() / maxTeamCount;

		int createdTeamCount = 0;
		int playerIndex = 0;
		// shuffle player list
		List<Player> players = new ArrayList<>();
		getPlayers().forEach(players::add);
		Collections.shuffle(players);

		while (true) {
			if (createdTeamCount >= maxTeamCount) {
				break;
			}
			Player p = players.get(playerIndex);
			this.registerPlayerToTeam(p, createdTeamCount);

			playerIndex += 1;

			if (playerIndex % memberCountPerTeam == 0) {
				createdTeamCount += 1;
			}
		}

		// register remain players to `min player count teams`
		while (playerIndex < this.getPlayerCount()) {
			Player p = players.get(playerIndex);
			this.registerPlayerToMinPlayerCountTeam(p);
			playerIndex += 1;
		}
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = RANDOM
	 */
	private void registerPlayers_RANDOM() {
		this.getPlayers().forEach(p -> this.registerPlayerToRandomTeam(p));
	}

	/**
	 * TeamBattleMiniGame.TeamRegisterMode = PARTY
	 */
	private void registerPlayers_PARTY() {
		MiniGameWorld mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);
		PartyManager partyManager = mw.getPartyManager();
		for (Team team : this.allTeams) {
			for (Player p : getPlayers()) {
				if (hasTeam(p)) {
					continue;
				}

				Party party = partyManager.getPlayerParty(p);
				party.getMembers().forEach(m -> registerPlayerToTeam(m, team));
				break;
			}
		}
	}

	@Override
	protected void onEvent(Event event) {
		if (event instanceof EntityDamageByEntityEvent) {
			processTeamPVP((EntityDamageByEntityEvent) event);
		} else if (event instanceof AsyncPlayerChatEvent) {
			processChat((AsyncPlayerChatEvent) event);
		}
	}

	private void processTeamPVP(EntityDamageByEntityEvent e) {
		// if team pvp is true, team members can damage each other
		if (this.isTeamPvP()) {
			return;
		}

		// cancel damage by entity
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (!(victim instanceof Player)) {
			return;
		}

		// direct damage
		if (damager instanceof Player) {
			if (this.isSameTeam((Player) victim, (Player) damager)) {
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
				e.setCancelled(true);
			}
		}
	}

	private void processChat(AsyncPlayerChatEvent e) {
		// group chat
		if (this.isGroupChat()) {
			// cancel event
			e.setCancelled(true);

			Player sender = e.getPlayer();

			// send message to only team members
			Team team = this.getTeam(sender);
			// ex. [Title] worldbiomusic: go go
			team.sendTeamMessage(sender, "(" + ChatColor.GOLD + this.messenger.getMsg(sender, "group-chat")
					+ ChatColor.RESET + "): " + e.getMessage());
		} else {
			Player p = e.getPlayer();
			String msg = e.getMessage();
			Team team = getTeam(p);
			String teamString = "(" + team.getColoredTeamName() + ")";
			e.setMessage(teamString + ": " + msg);
		}
	}

	@Override
	protected void printScores() {
		// print team score in descending order
		getPlayers().forEach(p -> {
			p.sendMessage(ChatColor.BOLD + "[" + this.messenger.getMsg(p, "score") + "]");
		});

		// rank team by score
		@SuppressWarnings("unchecked")
		List<Team> teams = (List<Team>) this.getRank();

		int rank = 1;
		ChatColor[] rankColors = { ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE };

		for (Team team : teams) {
			int score = team.getScore();
			String memberString = team.getAllMemberNameString();

			// rank string with color
			String rankString = "[";
			if (rank <= 3) {
				rankString += rankColors[rank - 1];
			}
			rankString += rank + "" + ChatColor.RESET + "] ";

			for (Player all : getPlayers()) {
				sendMessage(all, rankString + this.messenger.getMsg(all, "team") + "(" + memberString + ")" + ": "
						+ ChatColor.GOLD + score, false);
			}
			
			rank += 1;
		}

	}

	/**
	 * Gets team rank
	 * 
	 * @return Ordered team list by score
	 */
	@Override
	public List<? extends MiniGameRank> getRank() {
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
	protected void handleGameException(MiniGameExceptionEvent exception) {
		super.handleGameException(exception);

		// handleGameException() is called when exception is only about player exception
		MiniGamePlayerExceptionEvent e = (MiniGamePlayerExceptionEvent) exception;
		Player p = e.getPlayer();

		if (isStarted()) {
			// remove player from team
			Team team = getTeam(p);
			team.unregisterMember(p);
		}
	}

	@Override
	public String getFrameType() {
		return "TeamBattle";
	}

	/**
	 * Team which used in TeamBattleMiniGame frame<br>
	 * Manage: teamName, maxMemberCount, members, color
	 *
	 */
	public class Team implements MiniGameRank {
		private String teamName;
		private int maxMemberCount;
		private List<Player> members;
		private ChatColor color;

		/**
		 * Member size is needed for calculating maxPlayers of minigame
		 * 
		 * @param teamName   Team name
		 * @param memberSize Team max member size
		 */
		public Team(String teamName, int memberSize) {
			this(teamName, memberSize, ChatColor.WHITE);
		}

		/**
		 * Member size is needed for calculating maxPlayers of minigame
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
			if (isEmpty()) {
				return false;
			}
			
			return this.members.stream().filter(m -> isLive(m)).toList().size() > 0;
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
		public String toString() {
			String str = "[" + getColoredTeamName() + "]\n";
			for (Player m : this.members) {
				str += "- " + m.getName() + "\n";
			}
			return str;
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

class TeamBattleMiniGameScoreboardUpdater extends MiniGameScoreboardSidebarUpdater {

	public TeamBattleMiniGameScoreboardUpdater(MiniGame minigame) {
		super(minigame);
	}

	@Override
	public void updateScoreboard() {
		super.updateScoreboard();

		Objective sidebarObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		TeamBattleMiniGame minigame = (TeamBattleMiniGame) this.minigame;

		// Team list
		for (Team team : minigame.getTeamList()) {
			// team name
			String coloredTeamName = team.isTeamLive() ? team.getColoredTeamName()
					: ChatColor.STRIKETHROUGH + team.getColoredTeamName() + ChatColor.RESET;
			String teamNameStr = "[" + coloredTeamName + "]" + ": " + ChatColor.GOLD + ChatColor.BOLD + team.getScore();
			Score teamName = sidebarObjective.getScore(teamNameStr);
			teamName.setScore(sidebarScoreLine--);

			// team member list
			for (Player p : team.getMembers()) {
				String playerStr = "- ";

				MiniGamePlayerData pData = minigame.getPlayerData(p);
				if (pData.isLive()) {
					playerStr = playerStr + ChatColor.WHITE + p.getName() + ChatColor.RESET;
				} else {
					playerStr = playerStr + ChatColor.GRAY + ChatColor.STRIKETHROUGH + p.getName() + ChatColor.RESET;
				}

				Score playerList = sidebarObjective.getScore(playerStr);
				playerList.setScore(this.sidebarScoreLine--);

			}

			// empty line
			addEmptyLineToSiderbar();
		}

		// left time
		// TODO: change "Time left" to "<time-left>" and replace placeholder in scoreboard manager 
		String leftTimeStr = "Time left: " + ChatColor.RED + ChatColor.BOLD + minigame.getLeftFinishTime();
		Score leftTime = sidebarObjective.getScore(leftTimeStr);
		leftTime.setScore(this.sidebarScoreLine--);
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
