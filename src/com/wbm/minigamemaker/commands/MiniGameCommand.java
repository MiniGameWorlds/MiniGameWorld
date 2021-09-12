package com.wbm.minigamemaker.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.minigamemaker.util.Utils;

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
		Utils.sendMsg(p, "\n" + ChatColor.BOLD + "[MiniGame List]");
		Utils.sendMsg(p, "\n" + "※ " + ChatColor.RED + "RED" + ChatColor.WHITE + ": already started");
		Utils.sendMsg(p, "\n" + "※ " + ChatColor.GREEN + "GREEN" + ChatColor.WHITE + ": can join");

		// print mingames
		for (MiniGame game : games) {
			String gameTitle = game.getTitle();
			if (game.isStarted()) {
				Utils.sendMsg(p, "- " + ChatColor.RED + gameTitle);
			} else {
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
