package com.minigameworld.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.gui.MiniGameGUIManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Utils;

public class MiniGameCommand implements CommandExecutor {

	private MiniGameManager minigameManager;
	private MiniGameCommandTabCompleter tabCompleter;

	private MiniGamePartyCommand miniGamePartyCommand;

	public MiniGameCommand(MiniGameManager minigameM) {
		this.minigameManager = minigameM;

		this.miniGamePartyCommand = new MiniGamePartyCommand(this.minigameManager.getPartyManager());

		// set tab completer
		this.tabCompleter = new MiniGameCommandTabCompleter(minigameM);
		MiniGameWorldMain.getInstance().getCommand("minigame").setTabCompleter(this.tabCompleter);
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
			case "gui":
				return this.gui(p, args);
			case "party":
				return this.miniGamePartyCommand.party(p, args);
			case "reload":
				return this.reloadConfig(p, args);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean canCommandUse() {
		return (boolean) this.minigameManager.getGameSetting().get("minigameCommand");
	}

	private boolean join(Player p, String[] args) throws Exception {
		/*
		 * minigame join <title>
		 */

		// check minigameCommand is true(setting.yml)
		if (!this.canCommandUse()) {
			Utils.sendMsg(p, "minigameCommand option is false in \"setting.yml\" file");
		}

		String title = args[1];
		this.minigameManager.joinGame(p, title);
		return true;
	}

	private boolean leave(Player p, String[] args) throws Exception {
		/*
		 * minigame leave
		 */

		// check minigameCommand is true(setting.yml)
		if (!this.canCommandUse()) {
			Utils.sendMsg(p, "minigameCommand option is false in \"setting.yml\" file");
		}

		this.minigameManager.leaveGame(p);
		return true;
	}

	private boolean list(Player p, String[] args) throws Exception {

		// check minigameCommand is true(setting.yml)
		if (!this.canCommandUse()) {
			Utils.sendMsg(p, "minigameCommand option is false in \"setting.yml\" file");
		}

		List<MiniGame> games = this.minigameManager.getMiniGameList();

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

	private boolean gui(Player p, String[] args) {
		// check minigameCommand is true(setting.yml)
		if (!this.canCommandUse()) {
			Utils.sendMsg(p, "minigameCommand option is false in \"setting.yml\" file");
		}

		MiniGameGUIManager guiManager = this.minigameManager.getMiniGameGUIManager();
		guiManager.openGUI(p);
		return true;
	}

	private boolean reloadConfig(Player p, String[] args) throws Exception {
		// OP
		if (!p.isOp()) {
			return true;
		}

		// reload "setting.yml", "minigames.yml"
		this.minigameManager.reload();
		Utils.sendMsg(p, "" + ChatColor.GREEN + ChatColor.BOLD + "[ Reload Complete] ");
		p.sendMessage("- " + this.minigameManager.getFileName());
		this.minigameManager.getMiniGameList().forEach(m -> {
			p.sendMessage("- " + m.getTitleWithClassName());
		});
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
