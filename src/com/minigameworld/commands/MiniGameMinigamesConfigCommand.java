package com.minigameworld.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameData;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;

public class MiniGameMinigamesConfigCommand {
	private MiniGameManager minigameManager;

	public MiniGameMinigamesConfigCommand(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	private MiniGame getMiniGame(String className) {
		return this.minigameManager.getMiniGameWithClassName(className);
	}

	public boolean minigames(Player p, String[] args) throws Exception {
		if (!p.isOp()) {
			return true;
		}

		// /mg minigames <ClassName> <key> <value>
		String className = args[1];
		MiniGame minigame = this.getMiniGame(className);
		MiniGameData minigameData = minigame.getMiniGameData();
		Map<String, Object> data = minigameData.getData();

		String key = args[2];

		switch (key) {
		case Setting.MINIGAMES_TITLE:
			this.title(p, args, data);
			break;
		case Setting.MINIGAMES_LOCATION:
			this.location(p, args, data);
			break;
		case Setting.MINIGAMES_MIN_PLAYER_COUNT:
			this.min_player_count(p, args, data);
			break;
		case Setting.MINIGAMES_MAX_PLAYER_COUNT:
			this.max_player_count(p, args, data);
			break;
		case Setting.MINIGAMES_WAITING_TIME:
			this.waiting_time(p, args, data);
			break;
		case Setting.MINIGAMES_TIME_LIMIT:
			this.time_liimt(p, args, data);
			break;
		case Setting.MINIGAMES_ACTIVE:
			this.active(p, args, data);
			break;
		case Setting.MINIGAMES_TUTORIAL:
			this.tutorial(p, args, data);
			break;
		case Setting.MINIGAMES_CUSTOM_DATA: // can not process
			break;
		case Setting.MINIGAMES_ICON:
			this.icon(p, args, data);
			break;
		}

		// save config
		this.minigameManager.getYamlManager().save(minigameData);

		// reload config
		minigameData.reload();

		return true;
	}

	private void setKeyValue(Player p, Map<String, Object> data, String key, Object value) {
		if (data.containsKey(key)) {
			data.put(key, value);
			Utils.sendMsg(p, key + " set to " + value);
		} else {
			Utils.sendMsg(p, "settings.yml doesn't have " + key + " key");
		}
	}

	private boolean title(Player p, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> <key> title
		String title = args[3];
		this.setKeyValue(p, data, Setting.MINIGAMES_TITLE, title);
		return true;
	}

	private boolean location(Player p, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> location [<x> <y> <z>]
		if (args.length == 3) {
			Location playerLoc = p.getLocation();
			data.put(Setting.MINIGAMES_LOCATION, playerLoc);

			// msg
			Utils.sendMsg(p, Setting.MINIGAMES_LOCATION + " set to your location");
		} else if (args.length == 6) {
			double x = Integer.parseInt(args[3]);
			double y = Integer.parseInt(args[4]);
			double z = Integer.parseInt(args[5]);
			World w = p.getLocation().getWorld();
			Location loc = new Location(w, x, y, z);
			data.put(Setting.MINIGAMES_LOCATION, loc);

			// msg
			String locString = String.format("x: %f, y: %f, z: %f", x, y, z);
			Utils.sendMsg(p, Setting.MINIGAMES_LOCATION + " set to (" + locString + ")");
		} else {
			return false;
		}
		return true;
	}

	private boolean min_player_count(Player p, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> min-player-count <count>
		int count = Integer.parseInt(args[3]);
		this.setKeyValue(p, data, Setting.MINIGAMES_MIN_PLAYER_COUNT, count);
		return true;
	}

	private boolean max_player_count(Player p, String[] args, Map<String, Object> data) throws Exception {
		int count = Integer.parseInt(args[3]);
		this.setKeyValue(p, data, Setting.MINIGAMES_MAX_PLAYER_COUNT, count);
		return true;
	}

	private boolean waiting_time(Player p, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		this.setKeyValue(p, data, Setting.MINIGAMES_WAITING_TIME, time);
		return true;
	}

	private boolean time_liimt(Player p, String[] args, Map<String, Object> data) throws Exception {
		int time = Integer.parseInt(args[3]);
		this.setKeyValue(p, data, Setting.MINIGAMES_TIME_LIMIT, time);
		return true;
	}

	private boolean active(Player p, String[] args, Map<String, Object> data) throws Exception {
		boolean active = Boolean.parseBoolean(args[3]);
		this.setKeyValue(p, data, Setting.MINIGAMES_ACTIVE, active);
		return true;
	}

	private boolean tutorial(Player p, String[] args, Map<String, Object> data) throws Exception {
		// /mg minigames <classname> tutorial <index> <4> <5> <6> ...

		String tutorialString = "";
		for (int i = 4; i < args.length - 1; i++) {
			tutorialString += args[i];
		}

		String className = args[1];
		MiniGame minigame = this.getMiniGame(className);
		List<String> tutorial = minigame.getSetting().getTutorial();
		int index = Integer.parseInt(args[3]);

		tutorial.set(index, tutorialString);

		this.setKeyValue(p, data, Setting.MINIGAMES_TUTORIAL, tutorial);
		return true;
	}

	private boolean icon(Player p, String[] args, Map<String, Object> data) throws Exception {
		String str = args[3];
		Material icon = Material.valueOf(str);
		this.setKeyValue(p, data, Setting.MINIGAMES_ICON, icon);
		return true;
	}

}
