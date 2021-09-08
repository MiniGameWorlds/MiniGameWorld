package com.wbm.minigamemaker.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.minigamemaker.util.Setting;

public class MiniGameCommand implements CommandExecutor {

	/*
	 * minigame join <title>
	 * 
	 * minigame leave
	 */

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

		// menu
		String menu = args[0];
		switch (menu) {
		case "join":
			this.join(p, args);
			break;
		case "leave":
			this.leave(p, args);
			break;
		case "reload":
			this.reloadConfig(p, args);
			break;
		}

		return true;
	}

	private void join(Player p, String[] args) {
		/*
		 * minigame join <title>
		 */
		// check minigameCommand is true(setting.yml)
		boolean minigameCommand = (boolean) this.minigameM.getGameSetting().get("minigameCommand");
		if (!minigameCommand) {
			p.sendMessage("minigameCommand option is false in \"setting.yml\" file");
			return;
		}

		String title = args[1];
		this.minigameM.joinGame(p, title);
	}

	private void leave(Player p, String[] args) {
		/*
		 * minigame leave
		 */
		// check minigameCommand is true(setting.yml)
		boolean minigameCommand = (boolean) this.minigameM.getGameSetting().get("minigameCommand");
		if (!minigameCommand) {
			p.sendMessage("minigameCommand option is false in \"setting.yml\" file");
			return;
		}

		this.minigameM.leaveGame(p);
	}

	private void reloadConfig(Player p, String[] args) {
		// reload "setting.yml", "minigames.yml"
		this.minigameM.reload();
		this.MiniGameDataM.reload();
		Setting.sendMsg(p, "" + ChatColor.GREEN + ChatColor.BOLD + "Reload Complete" + ChatColor.WHITE + ": "
				+ this.minigameM.getFileName() + ", " + this.MiniGameDataM.getFileName());
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
