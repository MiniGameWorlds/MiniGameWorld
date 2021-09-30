package com.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.minigameworld.minigameframes.utils.MiniGameSetting.RankOrder;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.PlayerTool;
import com.wbm.plugin.util.SortTool;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;

public abstract class TeamBattleMiniGame extends MiniGame {

	/*
	 * 
	 * [Info]
	 * - team battle play
	 * - team has same score
	 * - team util class, methods
	 * - groupChat
	 * - Team Register Mode: NONE, FAIR, FILL, FAIR_FILL, RANDOM or can divide overriding registerPlayersToTeam()
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
	 * - If use TeamRegisterMode.NONE, register players to team using registerPlayersToTeam()
	 * 
	 */

	public enum TeamRegisterMode {
		NONE, FAIR, FILL, FAIR_FILL, RANDOM;
	}

	private List<Team> allTeams;

	protected abstract void createTeams();

	protected void registerPlayersToTeam() {
	}

	public TeamBattleMiniGame(String title, int minPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, -1, timeLimit, waitingTime);

		// custom options
		this.setGroupChat(true);
		this.setTeamPVP(false);

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

		// set team register mode
		this.setTeamRegisterMode(TeamRegisterMode.FAIR_FILL);
	}

	/*
	 * team
	 */
	private void initAllTeams() {
		this.allTeams.forEach(t -> t.init());
	}

	protected void createTeam(Team team) {
		this.allTeams.add(team);
	}

	protected Team getTeam(int teamNumber) {
		return allTeams.get(teamNumber);
	}

	protected Team getTeam(String teamName) {
		for (Team team : this.allTeams) {
			if (team.getName().equals(teamName)) {
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
			if (team.getMemberCount() < minTeam.getMemberCount()) {
				minTeam = team;
			}
		}
		return minTeam;
	}

	protected Team getMaxPlayerCountTeam() {
		Team maxTeam = this.allTeams.get(0);
		for (Team team : this.allTeams) {
			if (team.getMemberCount() > maxTeam.getMemberCount()) {
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
	protected void setTeamRegisterMode(TeamRegisterMode teamRegisterMode) {
		this.getCustomData().put("teamRegisterMode", teamRegisterMode.name());
	}

	protected TeamRegisterMode getTeamRegisterMode() {
		return TeamRegisterMode.valueOf((String) this.getCustomData().get("teamRegisterMode"));
	}

	protected void setGroupChat(boolean groupChat) {
		this.getCustomData().put("groupChat", groupChat);
	}

	protected boolean isGroupChat() {
		return (boolean) this.getCustomData().get("groupChat");
	}

	protected void setTeamPVP(boolean active) {
		this.getCustomData().put("teamPvp", active);
	}

	protected boolean isTeamPvP() {
		return (boolean) this.getCustomData().get("teamPvp");
	}

	protected List<Team> getTeamList() {
		return this.allTeams;
	}

	protected int getTeamCount() {
		return this.allTeams.size();
	}

	protected List<Team> getLiveTeamsList() {
		List<Team> liveTeams = new ArrayList<>();
		liveTeams.forEach(t -> {
			if (t.isTeamLive()) {
				liveTeams.add(t);
			}
		});

		return liveTeams;
	}

	protected int getLiveTeamCount() {
		return this.getLiveTeamsList().size();
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

//	@Override
//	protected void plusScore(Player p, int score) {
//		this.plusTeamScore(p, score);
//	}

	protected void minusTeamScore(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		team.minusTeamScore(score);
	}

	protected void minusTeamScore(Player p, int score) {
		Team team = this.getTeam(p);
		team.minusTeamScore(score);
	}

//	@Override
//	protected void minusScore(Player p, int score) {
//		this.minusTeamScore(p, score);
//	}

	protected int getTeamScore(int teamNumber) {
		Team team = this.getTeam(teamNumber);
		return team.getTeamScore();
	}

	protected int getTeamScore(Player p) {
		Team team = this.getTeam(p);
		return team.getTeamScore();
	}

	// change counting unit: player > team
	@Override
	protected boolean isMinPlayersLive() {
		return this.getLiveTeamCount() > 1;
	}

	@Override
	protected void initGameSettings() {
		this.initAllTeams();
	}

	@Override
	protected void runTaskAfterStart() {
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
	protected void processChatting(AsyncChatEvent e) {
		if (!this.isStarted()) {
			super.processChatting(e);
			return;
		}

		// cancel event
		e.setCancelled(true);

		// group chat
		if (this.isGroupChat()) {
			Player sender = e.getPlayer();

			// send message to only team members
			Team team = this.getTeam(sender);
			// ex. [Title] worldbiomusic: go go
			team.sendTeamMessage(sender, ((TextComponent)e.message()).content());
		} else {
			Player p = e.getPlayer();
			String msg = ((TextComponent)e.message()).content();
			Team team = this.getTeam(p);
			String teamName = team.getName();
			ChatColor color = team.getColor();
			String teamString = "(" + color + teamName + ChatColor.WHITE + ")";
			this.getPlayers().forEach(all -> this.sendMessage(all, p.getName() + teamString + ": " + msg));
		}
	}

	@Override
	protected void printScore() {
		// print team score in descending order
		this.sendMessageToAllPlayers(ChatColor.BOLD + "[Score]");

		// left teams
		Map<Team, Integer> leftTeams = new HashMap<Team, Integer>();
		for (Team team : this.allTeams) {
			if (!team.isEmpty()) {
				int teamScore = team.getTeamScore();
				leftTeams.put(team, teamScore);
			}
		}

		// rank team by score
		List<Entry<Team, Integer>> entries = this.getRank(leftTeams);
		int rank = 1;
		for (Entry<Team, Integer> entry : entries) {
			int score = entry.getValue();
			Team team = entry.getKey();
			String memberString = team.getAllMemberNameString();
			this.sendMessageToAllPlayers("[" + rank + "] " + "Team(" + memberString + ")" + ": " + score);
			rank += 1;
		}

	}

	private List<Entry<Team, Integer>> getRank(Map<Team, Integer> leftTeams) {
		// return rank with MiniGame RankOrder setting
		RankOrder order = this.getSetting().getRankOrder();
		if (order == RankOrder.ASCENDING) {
			return SortTool.getAscendingSortedList(leftTeams);
		} else { // if (order == RankOrder.DESCENDING) {
			return SortTool.getDescendingSortedList(leftTeams);
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
			this.members = new ArrayList<>(memberSize);
			this.color = ChatColor.WHITE;
		}

		public void init() {
			this.members.clear();
		}

		public boolean isEmpty() {
			return this.getMemberCount() == 0;
		}

		public boolean isFull() {
			return this.getMemberCount() == this.maxMemberCount;
		}

		public List<Player> getMembers() {
			return this.members;
		}

		public void sendTeamMessage(Player sender, String msg) {
			if (this.hasMember(sender)) {
				this.getMembers().forEach(p -> sendMessage(p,
						sender.getName() + "(" + this.color + this.teamName + ChatColor.WHITE + ")" + ": " + msg));
			}
		}

		public int getTeamScore() {
			return getScore(this.getRandomMember());
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
			return this.getMembers().contains(p);
		}

		public Player getRandomMember() {
			int randomIndex = (int) (Math.random() * this.getMemberCount());
			return this.getMembers().get(randomIndex);
		}

		public String getAllMemberNameString() {
			return PlayerTool.getPlayersNameString(this.members, ",");
		}

		public ChatColor getColor() {
			return color;
		}

		public void setColor(ChatColor color) {
			this.color = color;
		}

		public boolean isTeamLive() {
			for (Player member : this.members) {
				if (!getLivePlayers().contains(member)) {
					return false;
				}
			}
			return true;
		}

		public int getLiveMemberCount() {
			return this.getLiveMemberList().size();
		}

		public List<Player> getLiveMemberList() {
			List<Player> liveMembers = new ArrayList<>();
			for (Player member : this.members) {
				if (getLivePlayers().contains(member)) {
					liveMembers.add(member);
				}
			}
			return liveMembers;
		}

		public String getName() {
			return teamName;
		}

		public int getMemberCount() {
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
