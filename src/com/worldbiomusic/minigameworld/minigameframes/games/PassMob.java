package com.worldbiomusic.minigameworld.minigameframes.games;

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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.InventoryTool;
import com.worldbiomusic.minigameworld.minigameframes.TeamBattleMiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting.RankOrder;
import com.worldbiomusic.minigameworld.util.Utils;

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
	private int mobSpawnDelay;

	public PassMob() {
		super("PassMob", 2, 60 * 3, 10);

		// settings
		this.getSetting().setPassUndetectableEvent(true);
		this.getSetting().setRankOrder(RankOrder.ASCENDING);
		this.setGroupChat(true);
		this.getSetting().setIcon(Material.OAK_FENCE);

		// options
		this.getCustomOption().set(Option.MINIGAME_RESPAWN, false);

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
	public void loadCustomData() {
		super.loadCustomData();

		// create areas
		this.redArea.loc = (Location) this.getCustomData().get("redLocation");
		this.blueArea.loc = (Location) this.getCustomData().get("blueLocation");

		// mob spawn delay
		this.mobSpawnDelay = (int) this.getCustomData().get("mobSpawnDelay");
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

	@SuppressWarnings("deprecation")
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
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();

				// if death
				if (p.getHealth() <= e.getDamage()) {
					// cancel damage
					e.setDamage(0);

					// heal player
					p.setHealth(p.getMaxHealth());
					p.setFoodLevel(20);

					Team team = this.getTeam(p);
					Area area = this.getTeamArea(team);
					p.teleport(area.loc);
				}
			}
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
		this.getTaskManager().runTaskTimer("spawnMob", this.mobSpawnDelay * 20, this.mobSpawnDelay * 20);
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
		List<String> tutorial = new ArrayList<>();
		tutorial.add("At the game end, winner is the team that has less mobs");
		return tutorial;
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
