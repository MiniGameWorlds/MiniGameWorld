package com.minigameworld.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minigameworld.manager.MiniGameDataManager;
import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Utils;

public class MiniGameCommand implements CommandExecutor {

	private MiniGameManager minigameM;
	private MiniGameDataManager MiniGameDataM;

	public MiniGameCommand(MiniGameManager minigameM, MiniGameDataManager MiniGameDataM) {
		this.minigameM = minigameM;
		this.MiniGameDataM = MiniGameDataM;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// only player
		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;

		try {

			// menu
			String menu = args[0];
			switch (menu) {
			case "join":
				return this.join(p, args);
			case "leave":
				return this.leave(p, args);
			case "list":
				return this.list(p, args);
			case "reload":
				return this.reloadConfig(p, args);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private boolean join(Player p, String[] args) throws Exception {
		/*
		 * minigame join <title>
		 */
		// check minigameCommand is true(setting.yml)
		boolean minigameCommand = (boolean) this.minigameM.getGameSetting().get("minigameCommand");
		if (!minigameCommand) {
			Utils.sendMsg(p, "minigameCommand option is false in \"setting.yml\" file");
			return true;
		}

		String title = args[1];
		this.minigameM.joinGame(p, title);
		return true;
	}

	private boolean leave(Player p, String[] args) throws Exception {
		/*
		 * minigame leave
		 */
		// check minigameCommand is true(setting.yml)
		boolean minigameCommand = (boolean) this.minigameM.getGameSetting().get("minigameCommand");
		if (!minigameCommand) {
			Utils.sendMsg(p, "minigameCommand option is false in \"setting.yml\" file");
			return true;
		}

		this.minigameM.leaveGame(p);
		return true;
	}

	private boolean list(Player p, String[] args) throws Exception {
		List<MiniGame> games = this.minigameM.getMiniGameList();

		// info
		String info = "\n" + ChatColor.BOLD + "[MiniGame List]";
		info += "\n" + "※ " + ChatColor.RED + "RED" + ChatColor.WHITE + ": already started";
		info += "\n" + "※ " + ChatColor.GREEN + "GREEN" + ChatColor.WHITE + ": can join";
		info += "\n" + "※ " + ChatColor.STRIKETHROUGH + "STRIKETHROUGH" + ChatColor.WHITE + ": inactive";
		Utils.sendMsg(p, info);

		// print mingames
		for (MiniGame game : games) {
			String gameTitle = game.getTitle();
			if (!game.isActive()) {
				Utils.sendMsg(p, "- " + ChatColor.STRIKETHROUGH + gameTitle);
			} else if (game.isStarted()) {
				Utils.sendMsg(p, "- " + ChatColor.RED + gameTitle);
			} else if (!game.isStarted()) {
				Utils.sendMsg(p, "- " + ChatColor.GREEN + gameTitle);
			}
		}

		return true;
	}

	private boolean reloadConfig(Player p, String[] args) throws Exception {
		// reload "setting.yml", "minigames.yml"
		this.minigameM.reload();
		this.MiniGameDataM.reload();
		Utils.sendMsg(p, "" + ChatColor.GREEN + ChatColor.BOLD + "Reload Complete" + ChatColor.WHITE + ": "
				+ this.minigameM.getFileName() + ", " + this.MiniGameDataM.getFileName());
		return true;
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
