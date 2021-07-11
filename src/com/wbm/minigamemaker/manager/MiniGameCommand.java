package com.wbm.minigamemaker.manager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MiniGameCommand implements CommandExecutor {

	/*
	 * minigame join <title>
	 * 
	 * minigame leave
	 */

	private MiniGameManager minigameManager;

	public MiniGameCommand(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// only player
		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;

		// check minigameCommand is true(setting.yml)
		boolean minigameCommand = (boolean) this.minigameManager.getGameSetting().get("minigameCommand");
		if (!minigameCommand) {
			p.sendMessage("minigameCommand option is false");
			return true;
		}

		// menu
		String menu = args[0];
		switch (menu) {
		case "join":
			this.join(p, args);
			break;
		case "leave":
			this.leave(p, args);
			break;
		}

		return true;
	}

	private void join(Player p, String[] args) {
		/*
		 * minigame join <title>
		 */
		String title = args[1];
		this.minigameManager.joinGame(p, title);
	}

	private void leave(Player p, String[] args) {
		/*
		 * minigame leave
		 */
		this.minigameManager.leaveGame(p);
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
