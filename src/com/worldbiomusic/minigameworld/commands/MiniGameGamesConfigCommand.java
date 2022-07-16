package com.worldbiomusic.minigameworld.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameDataManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameGamesConfigCommand {
	private MiniGameManager minigameManager;
	private DataManager dataManager;

	public MiniGameGamesConfigCommand(MiniGameManager minigameManager, DataManager dataManager) {
		this.minigameManager = minigameManager;
		this.dataManager = dataManager;
	}

	private MiniGame getMiniGame(String className) {
		return this.minigameManager.getTemplateGame(className);
	}

	public boolean games(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "config.minigames")) {
			return true;
		}

		// /mg games <ClassName> <key> <value>
		String className = args[1];
		MiniGame minigame = this.getMiniGame(className);
		MiniGameDataManager minigameData = minigame.getDataManager();
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
				time_liimt(sender, args, data);
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
		String title = "";
		for (int i = 3; i < args.length; i++) {
			title += args[i];
			if (i < args.length - 1) {
				title += " ";
			}
		}

		this.setKeyValue(sender, args[1], data, Setting.GAMES_TITLE, title);
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
		// /mg games <classname> locations <<player>|<x> <y> <z>>

		@SuppressWarnings("unchecked")
		List<Location> locations = (List<Location>) data.get(Setting.GAMES_LOCATIONS);

		if (args.length == 4) {
			if (!PlayerTool.isOnlinePlayer(args[3])) {
				sender.sendMessage(args[3] + " is not online or not exist");
				return true;
			}
			Player targetPlayer = Bukkit.getPlayer(args[3]);
			Location playerLoc = targetPlayer.getLocation();
			locations.add(playerLoc);

			// msg
			Utils.sendMsg(sender, "[" + args[1] + "] " + Setting.GAMES_LOCATIONS + " added " + targetPlayer.getName()
					+ "'s location");
		} else if (args.length == 6) {
			// only player
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only Player");
				return true;
			}
			Player p = (Player) sender;

			double x = Integer.parseInt(args[3]);
			double y = Integer.parseInt(args[4]);
			double z = Integer.parseInt(args[5]);
			World w = p.getLocation().getWorld();
			Location loc = new Location(w, x, y, z);
			locations.add(loc);

			// msg
			String locString = String.format("x: %.3f, y: %.3f, z: %.3f", x, y, z);
			Utils.sendMsg(p, Setting.GAMES_LOCATIONS + " added (" + locString + ")");
		} else {
			return false;
		}
		return true;
	}

	private boolean min_players(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg games <classname> min-players <count>
		int count = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.GAMES_MIN_PLAYERS, count);
		return true;
	}

	private boolean max_players(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int count = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.GAMES_MAX_PLAYERS, count);
		return true;
	}

	private boolean waiting_time(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.GAMES_WAITING_TIME, time);
		return true;
	}

	private boolean time_liimt(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.GAMES_PLAY_TIME, time);
		return true;
	}

	private boolean active(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.GAMES_ACTIVE, active);
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
		List<String> tutorial = minigame.getSetting().getTutorial();
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

		this.setKeyValue(sender, args[1], data, Setting.GAMES_TUTORIAL, tutorial);
		return true;
	}

	private boolean custom_data(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		Utils.sendMsg(sender, "custom-data only can be fixed with config (after edit, need /mw reload)");
		return true;
	}

	private boolean icon(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		String str = args[3].toUpperCase();
		String icon = Material.valueOf(str).name();
		this.setKeyValue(sender, args[1], data, Setting.GAMES_ICON, icon);
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
