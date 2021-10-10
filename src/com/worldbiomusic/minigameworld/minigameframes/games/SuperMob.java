package com.worldbiomusic.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.wbm.plugin.util.InventoryTool;
import com.worldbiomusic.minigameworld.minigameframes.SoloBattleMiniGame;

public class SuperMob extends SoloBattleMiniGame {

	private Zombie superMob;
	private List<Entity> entities;
	double skillChance;

	public SuperMob() {
		super("SuperMob", 1, 5, 60 * 3, 10);
		this.entities = new ArrayList<>();
		this.getSetting().setIcon(Material.ZOMBIE_HEAD);
		this.getSetting().setPassUndetectableEvent(true);

		// random targeting task
		this.getTaskManager().registerTask("changeTarget", new Runnable() {

			@Override
			public void run() {
				int r = (int) (Math.random() * getPlayerCount());
				superMob.setTarget(getPlayers().get(r));
			}
		});
	}

	@Override
	protected void initGameSettings() {
		this.killAllEntities();
		this.skillChance = 0.1;
	}

	private void killAllEntities() {
		this.entities.forEach(e -> e.remove());
		this.entities.clear();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// give kits
		for (Player p : this.getPlayers()) {
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.IRON_SWORD));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.COOKED_PORKCHOP, 20));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.BOW));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.ARROW, 64));
			InventoryTool.addItemToPlayer(p, new ItemStack(Material.GOLDEN_APPLE));

			p.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			p.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			p.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			p.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			p.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
		}

		// spawn super mob
		this.superMob = (Zombie) this.getLocation().getWorld().spawnEntity(this.getLocation(), EntityType.ZOMBIE);
		this.superMob.setBaby();
		this.superMob.setMaxHealth(100_000_000);
		this.superMob.setHealth(this.superMob.getMaxHealth());
		this.superMob.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
		this.superMob.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
		this.superMob.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		this.superMob.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		this.superMob.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));

		// glow
		this.superMob.setGlowing(true);

		// add to list
		this.entities.add(this.superMob);

		// run random targeting task
		this.getTaskManager().runTaskTimer("changeTarget", 0, 20 * 60);
	}

	@Override
	protected void runTaskAfterFinish() {
		super.runTaskAfterFinish();
		this.killAllEntities();
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (!(e.getEntity() instanceof Zombie)) {
				return;
			}

			Zombie zombie = (Zombie) e.getEntity();
			if (!this.superMob.equals(zombie)) {
				return;
			}
			// direct damage
			if (e.getDamager() instanceof Player) {
				this.whenSuperMobDamagedByPlayer(e, (Player) e.getDamager());
			}
			// projectile damage
			else if (e.getDamager() instanceof Arrow) {
				Arrow proj = (Arrow) e.getDamager();
				if (!(proj.getShooter() instanceof Player)) {
					return;
				}

				Player shooter = (Player) proj.getShooter();
				if (!this.containsPlayer(shooter)) {
					return;
				}

				this.whenSuperMobDamagedByPlayer(e, shooter);
			}

		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			Player p = e.getEntity();

			e.getDrops().clear();
			p.setGameMode(GameMode.SPECTATOR);
			this.setLive(p, false);
			
			if(!this.isMinPlayersLive()) {
				this.finishGame();
			}
		} else if (event instanceof PlayerRespawnEvent) {
			PlayerRespawnEvent e = (PlayerRespawnEvent) event;
			e.setRespawnLocation(this.getLocation());
		} else if (event instanceof EntityDeathEvent) {
			EntityDeathEvent e = (EntityDeathEvent) event;
			if (this.entities.contains(e.getEntity())) {
				if (e.getEntity().getKiller() != null) {
					this.plusScore(e.getEntity().getKiller(), 1);
				}
			}
		}
	}

	private void whenSuperMobDamagedByPlayer(EntityDamageByEntityEvent e, Player p) {
		int damage = (int) e.getDamage();
		if (damage > 0) {
			// plus score
			this.plusScore(p, damage);
		}

		// use skill
		if (Math.random() < this.skillChance) {
			this.useSkill();
			this.skillChance += 0.01;
		}

		// set target player
		this.superMob.setTarget(p);
	}

	public enum Mode {
		ATTACK, DEFENSE, FAST;

		public static Mode random() {
			switch ((int) (Math.random() * 3)) {
			case 0:
				return ATTACK;
			case 1:
				return DEFENSE;
			default:
				return FAST;
			}
		}
	}

	private void useSkill() {
		int r = (int) (Math.random() * 2);
		switch (r) {
		case 0:
			this.spawnFriends();
			break;
		case 1:
			this.useMode(Mode.random());
			break;
		}
	}

	private void spawnFriends() {
		int amount = this.getPlayerCount();
		Location loc = this.superMob.getLocation();
		for (int i = 0; i < amount; i++) {
			this.spawnMob(loc, EntityType.ZOMBIE);
			this.spawnMob(loc, EntityType.SKELETON);
			this.spawnMob(loc, EntityType.SPIDER);
		}
		this.sendMessageToAllPlayers("SuperMob invites friends");
	}

	private void spawnMob(Location loc, EntityType type) {
		// add to entities List
		this.entities.add(loc.getWorld().spawnEntity(loc, type));
	}

	private void useMode(Mode mode) {
		switch (mode) {
		case ATTACK:
			this.useAttackMode();
			break;
		case DEFENSE:
			this.useDefenseMode();
			break;
		case FAST:
			this.useFastMode();
			break;
		}
	}

	private void useAttackMode() {
		this.superMob.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 10, 0));
		this.sendMessageToAllPlayers("SuperMob uses attack mode");
	}

	private void useDefenseMode() {
		this.superMob.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 0));
		this.sendMessageToAllPlayers("SuperMob uses defense mode");
	}

	private void useFastMode() {
		this.superMob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 0));
		this.sendMessageToAllPlayers("SuperMob uses fast mode");
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Hit Super Mob: +(damage)");
		return tutorial;
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
//
