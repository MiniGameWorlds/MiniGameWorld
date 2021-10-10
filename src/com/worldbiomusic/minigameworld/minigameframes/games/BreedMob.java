package com.worldbiomusic.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.InventoryTool;
import com.worldbiomusic.minigameworld.minigameframes.TeamMiniGame;
import com.worldbiomusic.minigameworld.util.Utils;

public class BreedMob extends TeamMiniGame {

	enum BreedingMob {
		ZOMBIE, SPIDER, SKELETON;

		public static Entity spawnRandomMob(Location loc) {
			List<EntityType> mobList = mobList();
			Collections.shuffle(mobList);
			return loc.getWorld().spawnEntity(loc, mobList.get(0));
		}

		public static List<EntityType> mobList() {
			List<EntityType> list = new ArrayList<>();
			for (BreedingMob mob : BreedingMob.values()) {
				list.add(EntityType.valueOf(mob.name()));
			}
			return list;
		}
	}

	private List<Entity> mobs;

	public BreedMob() {
		super("BreedMob", 1, 4, 60 * 3, 10);
		this.getSetting().setIcon(Material.ZOMBIE_SPAWN_EGG);
	}

	@Override
	protected void initGameSettings() {
		this.mobs = new ArrayList<>();
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof EntityDeathEvent) {
			EntityDeathEvent e = (EntityDeathEvent) event;
			
			LivingEntity entity = e.getEntity();

			// check mob
			if (this.mobs.contains(entity)) {
				Location deathLoc = entity.getLocation();

				// spawn random 2 mobs
				this.mobs.add(BreedingMob.spawnRandomMob(deathLoc));
				this.mobs.add(BreedingMob.spawnRandomMob(deathLoc));

				// plus score
				this.plusTeamScore(1);
			}
		} 
		else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();

				// if death
				if (p.getHealth() <= e.getDamage()) {
					e.setCancelled(true);
					// set live: false, gamemode: spectator
					p.setGameMode(GameMode.SPECTATOR);
					this.setLive(p, false);

					if (!this.isMinPlayersLive()) {
						this.finishGame();
					}

				}
			}
//			else if (this.mobs.contains(e.getEntity())) {
//				LivingEntity entity = (LivingEntity) e.getEntity();
//
//				// if death
//				if (entity.getHealth() <= e.getDamage()) {
//					Location deathLoc = entity.getLocation();
//
//					// spawn random 2 mobs
//					this.mobs.add(BreedingMob.spawnRandomMob(deathLoc));
//					this.mobs.add(BreedingMob.spawnRandomMob(deathLoc));
//
//					// plus score
//					this.plusTeamScore(1);
//				}
//			}
		}
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// spawn random mob
		Entity entity = BreedingMob.spawnRandomMob(getLocation());
		this.mobs.add(entity);

		// give kits to players
		List<ItemStack> items = new ArrayList<>();
		items.add(new ItemStack(Material.IRON_SWORD));
		items.add(new ItemStack(Material.BOW));
		items.add(new ItemStack(Material.ARROW, 64));
		items.add(new ItemStack(Material.COOKED_BEEF, 10));
		items.add(new ItemStack(Material.GOLDEN_APPLE, 3));

		InventoryTool.addItemsToPlayers(getPlayers(), items);

		// set armors
		for (Player p : this.getPlayers()) {
			p.setHealthScale(40);
			p.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			p.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			p.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			p.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			p.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
		}
	}

	@Override
	protected void runTaskAfterFinish() {
		super.runTaskAfterFinish();

		this.mobs.forEach(e -> e.remove());
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Kill mob: +1");
		tutorial.add("Death: spectator");
		tutorial.add("Mobs will lay two mobs when die");
		return tutorial;
	}

}
