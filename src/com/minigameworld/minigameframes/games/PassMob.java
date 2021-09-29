package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.minigameworld.minigameframes.TeamBattleMiniGame;
import com.minigameworld.minigameframes.utils.MiniGameCustomOption.Option;
import com.minigameworld.minigameframes.utils.MiniGameSetting.RankOrder;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.InventoryTool;

public class PassMob extends TeamBattleMiniGame {
	public class Area {
		String name;
		Team team;
		Location loc;
		List<Entity> mobs;

		public Area(String name) {
			this.name = name;
			this.mobs = new ArrayList<>();
		}

		public void init() {
			// kill all mobs
			Utils.debug(this.name + " area killed: " + this.mobs.size());
			this.mobs.forEach(m -> m.remove());
			this.mobs.clear();
		}

		public void setTeam(Team team) {
			this.team = team;
		}

		public void spawnRandomMob() {
			int r = (int) (Math.random() * getMobList().size());
			this.spawnMob(getMobList().get(r));
		}

		public void spawnMob(EntityType entityType) {
			Entity entity = this.loc.getWorld().spawnEntity(this.loc, entityType);
			this.mobs.add(entity);

			// set team random targeting
			((Mob) entity).setTarget(this.team.getRandomMember());
		}

		public void passMobToOtherArea(Entity mob, Area area) {
			this.mobs.remove(mob);
			area.spawnMob(mob.getType());
		}

		public List<Entity> getMobs() {
			return this.mobs;
		}

		public void tpTeam() {
			this.team.getMembers().forEach(m -> m.teleport(this.loc));
		}

	}

	private Area redArea, blueArea;

	public PassMob() {
		super("PassMob", 2, 60 * 3, 10);

		// settings
		this.getSetting().setPassUndetectableEvents(true);
		this.getSetting().setRankOrder(RankOrder.ASCENDING);
		this.setGroupChat(true);

		// options
		this.getCustomOption().setOption(Option.MINIGAME_RESPAWN, false);

		// areas
		this.redArea = new Area("red");
		this.blueArea = new Area("blue");

		// set area with team
		this.redArea.setTeam(this.getTeam("red"));
		this.blueArea.setTeam(this.getTeam("blue"));

		// task
		this.registerTask();
	}

	private void registerTask() {
		this.getTaskManager().registerTask("spawnMob", new Runnable() {

			@Override
			public void run() {
				// spawn random mob
				redArea.spawnRandomMob();
				blueArea.spawnRandomMob();
			}
		});
	}

	@Override
	protected void registerCustomData() {
		super.registerCustomData();
		this.getCustomData().put("redLocation", new Location(this.getLocation().getWorld(), 0, 0, 0));
		this.getCustomData().put("blueLocation", new Location(this.getLocation().getWorld(), 0, 0, 0));

		this.getCustomData().put("mobSpawnDelay", 10);
	}

	@Override
	protected void createTeams() {
		Team red = new Team("red", 4);
		red.setColor(ChatColor.RED);
		this.createTeam(red);

		Team blue = new Team("blue", 4);
		blue.setColor(ChatColor.BLUE);
		this.createTeam(blue);
	}

	@Override
	protected void processEvent(Event event) {
		super.processEvent(event);

		if (event instanceof EntityDeathEvent) {
			EntityDeathEvent e = (EntityDeathEvent) event;
			Entity entity = e.getEntity();
			if (this.isPassMobEntity(entity)) {
				Area area = this.getMobArea(entity);
				area.passMobToOtherArea(entity, this.otherArea(area));
			}
		} else if (event instanceof PlayerRespawnEvent) {
			PlayerRespawnEvent e = (PlayerRespawnEvent) event;
			Player p = e.getPlayer();
			Team team = this.getTeam(p);
			Area area = this.getTeamArea(team);
			e.setRespawnLocation(area.loc);
		}
	}

	private Area otherArea(Area area) {
		if (area.equals(this.redArea)) {
			return this.blueArea;
		} else {
			return this.redArea;
		}
	}

	@Override
	protected void initGameSettings() {
		super.initGameSettings();

		// create areas
		Location redLoc = (Location) this.getCustomData().get("redLocation");
		Location blueLoc = (Location) this.getCustomData().get("blueLocation");

		this.redArea.loc = redLoc;
		this.blueArea.loc = blueLoc;
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// give kits
		for (Player p : this.getPlayers()) {
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.IRON_SWORD));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.COOKED_PORKCHOP, 64));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.BOW));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.ARROW, 64));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.GOLDEN_APPLE, 3));

			p.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
			p.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			p.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			p.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
			p.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
		}

		// spawn random mob
		this.redArea.spawnRandomMob();
		this.blueArea.spawnRandomMob();

		// tp team members
		this.redArea.tpTeam();
		this.blueArea.tpTeam();

		// start random mob spawn task
		int mobSpawnDelay = (int) this.getCustomData().get("mobSpawnDelay");
		this.getTaskManager().runTaskTimer("spawnMob", mobSpawnDelay * 20, mobSpawnDelay * 20);
	}

	@Override
	protected void runTaskBeforeFinish() {
		super.runTaskBeforeFinish();
		this.processTeamScore();
	}

	public void processTeamScore() {
		int redMobCount = this.redArea.getMobs().size();
		int blueMobCount = this.blueArea.getMobs().size();

		this.redArea.team.plusTeamScore(redMobCount);
		this.blueArea.team.plusTeamScore(blueMobCount);
	}

	@Override
	protected void runTaskAfterFinish() {
		super.runTaskAfterFinish();

		// init areas
		this.redArea.init();
		this.blueArea.init();
	}

	@Override
	protected List<String> registerTutorial() {
		return null;
	}

	public boolean isPassMobEntity(Entity entity) {
		return this.redArea.getMobs().contains(entity) || this.blueArea.getMobs().contains(entity);
	}

	public Area getMobArea(Entity entity) {
		if (this.redArea.getMobs().contains(entity)) {
			return this.redArea;
		} else if (this.blueArea.getMobs().contains(entity)) {
			return this.blueArea;
		}
		return null;
	}

	public Area getTeamArea(Team team) {
		if (this.redArea.team.equals(team)) {
			return this.redArea;
		} else {
			return this.blueArea;
		}
	}

	public List<EntityType> getMobList() {
		List<EntityType> mobs = new ArrayList<>();
		mobs.add(EntityType.ZOMBIE);
		mobs.add(EntityType.SKELETON);
		mobs.add(EntityType.SPIDER);

		return mobs;
	}
}
