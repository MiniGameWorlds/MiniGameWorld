package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.codehaus.plexus.util.FileUtils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import com.wbm.plugin.util.CollectionTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Utils;

public class LocationManager {
	// Using locations by minigames which are not instance world
	// when instance-world option is false
	private static List<Location> usingLocations = new ArrayList<>();

	// Used instances location(world) which will be removed
	// when instance-world option is true
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
		Utils.debug("reset()");
		// check inited
		if (!this.inited) {
			return;
		}

		Utils.debug("reset() passed");
		if (gameSetting.isInstanceWorld()) {
			Utils.debug("usedLocations added");
			// add used location
			usedLocations.add(gameSetting.getLocation().getWorld().getName());

			// let viewers get out of world
			MiniGameViewManager viewM = minigame.getViewManager();
			viewM.getViewers().forEach(viewM::unviewGame);

			// remove instance world
			String world = gameSetting.getLocation().getWorld().getName();
			try {
				// remove dir
				Bukkit.unloadWorld(world, false);
				FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), world));
				Utils.debug(ChatColor.RED + world + " deleted\n");

				// remove key from multiverse-core config
				WorldManager worldManager = (WorldManager) this.multiverseCore.getMVWorldManager();
				worldManager.removeWorldFromConfig(world);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
