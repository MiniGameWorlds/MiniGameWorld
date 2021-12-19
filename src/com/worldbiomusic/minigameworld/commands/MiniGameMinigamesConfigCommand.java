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

public class MiniGameMinigamesConfigCommand {
	private MiniGameManager minigameManager;
	private DataManager dataManager;

	public MiniGameMinigamesConfigCommand(MiniGameManager minigameManager, DataManager dataManager) {
		this.minigameManager = minigameManager;
		this.dataManager = dataManager;
	}

	private MiniGame getMiniGame(String className) {
		return this.minigameManager.getMiniGameWithClassName(className);
	}

	public boolean minigames(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "config.minigames")) {
			return true;
		}

		// /mg minigames <ClassName> <key> <value>
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
			case Setting.MINIGAMES_TITLE:
				this.title(sender, args, data);
				break;
			case Setting.MINIGAMES_LOCATION:
				this.location(sender, args, data);
				break;
			case Setting.MINIGAMES_MIN_PLAYER_COUNT:
				this.min_player_count(sender, args, data);
				break;
			case Setting.MINIGAMES_MAX_PLAYER_COUNT:
				this.max_player_count(sender, args, data);
				break;
			case Setting.MINIGAMES_WAITING_TIME:
				this.waiting_time(sender, args, data);
				break;
			case Setting.MINIGAMES_TIME_LIMIT:
				this.time_liimt(sender, args, data);
				break;
			case Setting.MINIGAMES_ACTIVE:
				this.active(sender, args, data);
				break;
			case Setting.MINIGAMES_TUTORIAL:
				this.tutorial(sender, args, data);
				break;
			case Setting.MINIGAMES_CUSTOM_DATA: // can not process
				this.custom_data(sender, args, data);
				break;
			case Setting.MINIGAMES_ICON:
				this.icon(sender, args, data);
				break;
			}

			// save config
			this.minigameManager.getYamlManager().save(minigameData);

			// reload config
			this.dataManager.reload(minigameData);
		}

		return true;
	}

	private void printValue(CommandSender sender, String minigame, Map<String, Object> data, String[] args) throws Exception {
		// /mg minigames <classname> <key>
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
		// /mg minigames <classname> title <value>
		String title = args[3];

		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_TITLE, title);
		return true;
	}

	private boolean location(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> location <<player>|<x> <y> <z>>
		if (args.length == 4) {
			if (!PlayerTool.isOnlinePlayer(args[3])) {
				sender.sendMessage(args[3] + " is not online or not exist");
				return true;
			}
			Player targetPlayer = Bukkit.getPlayer(args[3]);
			Location playerLoc = targetPlayer.getLocation();
			data.put(Setting.MINIGAMES_LOCATION, playerLoc);

			// msg
			Utils.sendMsg(sender, Setting.MINIGAMES_LOCATION + " set to your location");
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
			data.put(Setting.MINIGAMES_LOCATION, loc);

			// msg
			String locString = String.format("x: %.3f, y: %.3f, z: %.3f", x, y, z);
			Utils.sendMsg(p, Setting.MINIGAMES_LOCATION + " set to (" + locString + ")");
		} else {
			return false;
		}
		return true;
	}

	private boolean min_player_count(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> min-player-count <count>
		int count = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_MIN_PLAYER_COUNT, count);
		return true;
	}

	private boolean max_player_count(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int count = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_MAX_PLAYER_COUNT, count);
		return true;
	}

	private boolean waiting_time(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_WAITING_TIME, time);
		return true;
	}

	private boolean time_liimt(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_TIME_LIMIT, time);
		return true;
	}

	private boolean active(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_ACTIVE, active);
		return true;
	}

	private boolean tutorial(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> tutorial <line> <4> <5> <6> ...

		String tutorialString = "";
		for (int i = 4; i < args.length - 1; i++) {
			tutorialString += args[i];
			if (i < args.length - 1) {
				tutorialString += " ";
			}
		}

		String className = args[1];
		MiniGame minigame = this.getMiniGame(className);
		List<String> tutorial = minigame.getSetting().getTutorial();
		int line = Integer.parseInt(args[3]);

		tutorial.set(line, tutorialString);

		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_TUTORIAL, tutorial);
		return true;
	}

	private void custom_data(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		Utils.sendMsg(sender, "custom-data is only can be fixed with file");
	}

	private boolean icon(CommandSender sender, String[] args, Map<String, Object> data) throws Exception {
		String str = args[3];
		Material icon = Material.valueOf(str);
		this.setKeyValue(sender, args[1], data, Setting.MINIGAMES_ICON, icon);
		return true;
	}

}
