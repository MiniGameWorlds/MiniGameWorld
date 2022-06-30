package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.wbm.plugin.util.CollectionTool;
import com.wbm.plugin.util.Utils;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class LocationManager {
	// Using locations by minigames which are not instance world
	private static List<Location> usingLocations = new ArrayList<>();

	// Used instances location(world) which will be removed
	private static List<String> usedLocations = new ArrayList<>();

	private MiniGame minigame;
	private MiniGameSetting gameSetting;

	private boolean inited;
	private MultiverseCore multiverseCore;

	public LocationManager(MiniGame minigame) {
		this.minigame = minigame;
		this.gameSetting = minigame.getSetting();
		this.inited = false;

		this.multiverseCore = MiniGameWorldMain.multiverseCore();
	}

	public void addUsingLocation(Location loc) {
		usingLocations.add(loc);
	}

	public void removeUsingLocation(Location loc) {
		usingLocations.remove(loc);
	}

	public boolean containsUsingLocation(Location loc) {
		return usingLocations.contains(loc);
	}

	public static List<Location> getUsingLocations() {
		return usingLocations;
	}

	public static List<String> getUsedLocations() {
		return usedLocations;
	}

	public void init() {
		Utils.debug("LocationManager.init() 1");
		if (this.inited) {
			return;
		}
		Utils.debug("LocationManager.init() 2");

		if (!remainsExtra()) {
			Utils.debug("LocationManager.init(): no extra location remains");
			return;
		}

		Location newLocation = null;

		// create new instance world
		if (gameSetting.isInstanceWorld()) {
			// select random location of list
			Location randomLocation = CollectionTool.random(gameSetting.getLocations()).get();
			World world = randomLocation.getWorld();

			double x = randomLocation.getX();
			double y = randomLocation.getY();
			double z = randomLocation.getZ();
			float yaw = randomLocation.getYaw();
			float pitch = randomLocation.getPitch();

			// create a new copied world: `<world>_<minigame ID>` and load world
			String newWorldName = world.getName() + "_" + gameSetting.getId();

			this.multiverseCore.cloneWorld(world.getName(), newWorldName, "");

			newLocation = new Location(Bukkit.getWorld(newWorldName), x, y, z, yaw, pitch);
		} else {
			// select not using random location
			newLocation = CollectionTool.random(gameSetting.getNotUsingLocations()).get();

			// add new location to using location list
			addUsingLocation(newLocation);
		}

		gameSetting.setLocation(newLocation);

		// set inited flag to true
		this.inited = true;
	}

	/**
	 * [IMPORTANT] Only work if {@code LocationManager#init() } is invoked
	 */
	public void reset() {
		// check inited
		if (!this.inited) {
			return;
		}

		if (gameSetting.isInstanceWorld()) {
			// remove instance world
			multiverseCore.getMVWorldManager().deleteWorld(gameSetting.getLocation().getWorld().getName(), false,
					false);

			// add used location
			usedLocations.add(gameSetting.getLocation().getWorld().getName());

			// let viewers get out of world
			MiniGameViewManager viewM = minigame.getViewManager();
			viewM.getViewers().forEach(viewM::unviewGame);
		} else {
			// set other minigame can use this location
			removeUsingLocation(gameSetting.getLocation());
		}

		// set inited to false
		this.inited = false;
	}

	public boolean remainsExtra() {
		return gameSetting.isInstanceWorld() ? true : !gameSetting.getNotUsingLocations().isEmpty();
	}
}
