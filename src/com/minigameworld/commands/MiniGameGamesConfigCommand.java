package com.minigameworld.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameDataManager;
import com.minigameworld.managers.DataManager;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.onarandombox.MultiverseCore.utils.WorldNameChecker;
import com.wbm.plugin.util.PlayerTool;

public class MiniGameGamesConfigCommand {
	private MiniGameManager minigameManager;
	private DataManager dataManager;

	public MiniGameGamesConfigCommand(MiniGameManager minigameManager, DataManager dataManager) {
		this.minigameManager = minigameManager;
		this.dataManager = dataManager;
	}

	private MiniGame getMiniGame(String className) {
		for (MiniGame game : this.minigameManager.getTemplateGames()) {
			if (game.className().equals(className)) {
				return game;
			}
		}
		return null;
	}

	public boolean games(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "config.minigames")) {
			return true;
		}

		// /mg games <ClassName> <key> <value>
		String className = args[1];
		MiniGame minigame = this.getMiniGame(className);
		if (minigame == null) {
			Utils.sendMsg(sender, "There is no minigame which has " + className + " class name");
			return true;
		}

		MiniGameDataManager minigameData = minigame.dataManager();
		Map<String, Object> data = minigameData.getData();

		// print just value of key
		if (args.length == 3) {
			this.printValue(sender, className, data, args);
		} else {
			String key = args[2];

			switch (key) {
			case Setting.GAMES_TITLE:
				title(sender, args, data);
				break;
			case Setting.GAMES_INSTANCES:
				instances(sender, args, data);
				break;
			case Setting.GAMES_INSTANCE_WORLD:
				instance_world(sender, args, data);
				break;
			case Setting.GAMES_LOCATIONS:
				locations(sender, args, data);
				break;
			case Setting.GAMES_MIN_PLAYERS:
				min_players(sender, args, data);
				break;
			case Setting.GAMES_MAX_PLAYERS:
				max_players(sender, args, data);
				break;
			case Setting.GAMES_WAITING_TIME:
				waiting_time(sender, args, data);
				break;
			case Setting.GAMES_PLAY_TIME:
				play_time(sender, args, data);
				break;
			case Setting.GAMES_FINISH_DELAY:
				finish_delay(sender, args, data);
				break;
			case Setting.GAMES_ACTIVE:
				active(sender, args, data);
				break;
			case Setting.GAMES_TUTORIAL:
				tutorial(sender, args, data);
				break;
			case Setting.GAMES_CUSTOM_DATA: // can not process
				custom_data(sender, args, data);
				break;
			case Setting.GAMES_ICON:
				icon(sender, args, data);
				break;
			case Setting.GAMES_VIEW:
				view(sender, args, data);
				break;
			case Setting.GAMES_SCOREBOARD:
				scoreboard(sender, args, data);
				break;
			}

			// save config
			this.minigameManager.getYamlManager().save(minigameData);

			// reload config
			this.dataManager.reload(minigameData);
		}

		return true;
	}

	private void printValue(CommandSender sender, String minigame, Map<String, Object> data, String[] args)
			throws Exception {
		// /mg games <classname> <key>
		String key = args[2];
		if (data.containsKey(key)) {
			Object value = data.get(key);
			Utils.sendMsg(sender, "[" + minigame + "] " + key + ": " + value);
		} else {
			Utils.sendMsg(sender, "settings.yml doesn't have " + key + " key");
		}
	}

	private void setKeyValue(CommandSender sender, String minigame, Map<String, Object> data, String key, Object value)
			throws Exception {
		if (data.containsKey(key)) {
			data.put(key, value);
			Utils.sendMsg(sender, "[" + minigame + "] " + key + " set to " + value);
		} else {
			Utils.sendMsg(sender, "settings.yml doesn't have " + key + " key");
		}
	}

	private boolean title(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg games <classname> title <value>
		String title = args[3];
		if (!WorldNameChecker.isValidWorldName(title)) {
			sender.sendMessage(title + " is not valid for game title. (Do not use space or special characters)");
			return true;
		}

		setKeyValue(sender, args[1], data, Setting.GAMES_TITLE, title);
		return true;
	}

	private boolean instances(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int amount = Integer.parseInt(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_INSTANCES, amount);
		return true;
	}

	private boolean instance_world(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_INSTANCE_WORLD, active);
		return true;
	}

	private boolean locations(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mw games <game> locations <[+] <<player> | <x> <y> <z>> | - <index>>

		final String ADD = "+", REMOVE = "-";
		String option = args[3];

		@SuppressWarnings("unchecked")
		List<Location> locations = (List<Location>) data.get(Setting.GAMES_LOCATIONS);

		if (option.equals(ADD)) {
			if (args.length == 5) {
				String p = args[4];
				addLocation(sender, p, locations);
				return true;
			} else if (args.length == 7) {
				addLocation(sender, args[4], args[5], args[6], locations);
				return true;
			}
		} else if (option.equals(REMOVE)) {
			int index = Integer.parseInt(args[4]);
			if (index > locations.size()) {
				Utils.sendMsg(sender, index + " index doesn't exist");
				return true;
			}

			locations.remove(index - 1);
			Utils.sendMsg(sender, index + " index location removed");
			return true;
		} else {
			// /mw games <game> locations <<player> | <x> <y> <z>>

			// reset
			locations.clear();
			Utils.sendMsg(sender, "Locations are reset");

			// add new location
			if (args.length == 4) {
				String p = args[3];
				addLocation(sender, p, locations);
				return true;
			} else if (args.length == 6) {
				addLocation(sender, args[3], args[4], args[5], locations);
				return true;
			}
		}
		return false;
	}

	private void addLocation(CommandSender sender, String p, List<Location> locations) {
		if (!PlayerTool.isOnlinePlayer(p)) {
			sender.sendMessage(p + " is not online or not exist");
			return;
		}

		Player targetPlayer = Bukkit.getPlayer(p);
		Location playerLoc = targetPlayer.getLocation();
		locations.add(playerLoc);

		// msg
		Utils.sendMsg(sender, "Added " + targetPlayer.getName() + "'s location");
	}

	private void addLocation(CommandSender sender, String xStr, String yStr, String zStr, List<Location> locations) {
		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return;
		}
		Player p = (Player) sender;

		double x = Integer.parseInt(xStr);
		double y = Integer.parseInt(yStr);
		double z = Integer.parseInt(zStr);
		World w = p.getLocation().getWorld();
		Location loc = new Location(w, x, y, z);
		locations.add(loc);

		// msg
		String locString = String.format("x: %.3f, y: %.3f, z: %.3f", x, y, z);
		Utils.sendMsg(p, "Added (" + locString + ")");
	}

	private boolean min_players(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg games <classname> min-players <count>
		int count = Integer.parseInt(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_MIN_PLAYERS, count);
		return true;
	}

	private boolean max_players(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int count = Integer.parseInt(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_MAX_PLAYERS, count);
		return true;
	}

	private boolean waiting_time(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_WAITING_TIME, time);
		return true;
	}

	private boolean play_time(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_PLAY_TIME, time);
		return true;
	}

	private boolean finish_delay(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int value = Integer.parseInt(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_FINISH_DELAY, value);
		return true;
	}

	private boolean active(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_ACTIVE, active);
		return true;
	}

	private boolean tutorial(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg games <classname> tutorial <line> <4> <5> <6> ...

		String tutorialString = "";
		for (int i = 4; i < args.length; i++) {
			tutorialString += args[i];
			if (i < args.length - 1) {
				tutorialString += " ";
			}
		}

		String className = args[1];
		MiniGame minigame = this.getMiniGame(className);
		List<String> tutorial = minigame.setting().getTutorial();
		int line = Integer.parseInt(args[3]); // 1 ~

		// remove tutorial line
		if (tutorialString.equals("-")) {
			if (line <= tutorial.size()) {
				tutorial.remove(line - 1);
			} else {
				sender.sendMessage(line + "th tutorial is not exist");
				return true;
			}
		} else {
			// check line is exist
			if (tutorial.size() < line) {
				// add more lines
				for (int i = tutorial.size(); i < line; i++) {
					tutorial.add("");
				}
			}
			tutorial.set(line - 1, tutorialString);
		}

		setKeyValue(sender, args[1], data, Setting.GAMES_TUTORIAL, tutorial);
		return true;
	}

	private boolean custom_data(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		Utils.sendMsg(sender, "custom-data only can be fixed with config (after edit, need /mw reload)");
		return true;
	}

	private boolean icon(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		String str = args[3].toUpperCase();
		String icon = Material.valueOf(str).name();
		setKeyValue(sender, args[1], data, Setting.GAMES_ICON, icon);
		return true;
	}

	private boolean view(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_VIEW, active);
		return true;
	}

	private boolean scoreboard(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		setKeyValue(sender, args[1], data, Setting.GAMES_SCOREBOARD, active);
		return true;
	}

}
