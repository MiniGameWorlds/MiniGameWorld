package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.minigameworld.minigameframes.TeamMiniGame;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.InventoryTool;

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
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			Player p = e.getEntity();

			// set live: false, gamemode: spectator
			p.setGameMode(GameMode.SPECTATOR);
			this.setLive(p, false);

			if (!this.isMinPlayersLive()) {
				this.endGame();
			}

		} else if (event instanceof EntityDeathEvent) {
			EntityDeathEvent e = (EntityDeathEvent) event;

			LivingEntity entity = e.getEntity();
//			Player killer = entity.getKiller();

			// check mob
			if (BreedingMob.mobList().contains(entity.getType())) {
				Location deathLoc = entity.getLocation();

				// spawn random 2 mobs
				this.mobs.add(BreedingMob.spawnRandomMob(deathLoc));
				this.mobs.add(BreedingMob.spawnRandomMob(deathLoc));

				// plus score
				this.plusTeamScore(1);

				// firework
				Firework firework = (Firework) deathLoc.getWorld().spawnEntity(deathLoc, EntityType.FIREWORK);
				FireworkMeta fireworkMeta = firework.getFireworkMeta();

				FireworkEffect.Type type = FireworkEffect.Type.BALL;

				FireworkEffect effect = FireworkEffect.builder().with(type).withColor(Color.YELLOW).build();
				fireworkMeta.addEffect(effect);

				fireworkMeta.setPower(0);
				firework.setFireworkMeta(fireworkMeta);
			}
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

		Utils.debug("left mobs: " + this.mobs.size());
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
