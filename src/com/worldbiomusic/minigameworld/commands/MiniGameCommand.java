package com.worldbiomusic.minigameworld.commands;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.FileTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenuManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.FunctionItem;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameCommand implements CommandExecutor {

	private MiniGameManager minigameManager;
	private MiniGameCommandTabCompleter tabCompleter;

	private MiniGamePartyCommand miniGamePartyCommand;
	private MiniGameSettingsConfigCommand miniGameSettingsConfigCommand;
	private MiniGameGamesConfigCommand miniGameGamesConfigCommand;
	private MiniGameHelpCommand minigameHelpCommand;
	private DataManager dataManager;

	public MiniGameCommand(MiniGameManager minigameM, DataManager dataManager) {
		this.minigameManager = minigameM;
		this.dataManager = dataManager;

		this.miniGamePartyCommand = new MiniGamePartyCommand(this.minigameManager.getPartyManager());
		this.miniGameSettingsConfigCommand = new MiniGameSettingsConfigCommand(this.minigameManager, this.dataManager);
		this.miniGameGamesConfigCommand = new MiniGameGamesConfigCommand(this.minigameManager,
				this.dataManager);
		this.minigameHelpCommand = new MiniGameHelpCommand();

		// set tab completer
		this.tabCompleter = new MiniGameCommandTabCompleter(minigameM, dataManager);
		MiniGameWorldMain.getInstance().getCommand("minigame").setTabCompleter(this.tabCompleter);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		try {
			if (!Utils.checkPerm(sender, "command")) {
				return true;
			}

			// give function items if there are no args
			if (args.length == 0) {
				return giveFunctionItems(sender);
			}

			// menu
			String menu = args[0];

			switch (menu) {
			case "help":
				return help(sender, args);
			case "join":
				return join(sender, args);
			case "view":
				return view(sender, args);
			case "leave":
				return leave(sender, args);
			case "list":
				return list(sender, args);
			case "menu":
				return menu(sender, args);
			case "backup":
				return backup(sender, args);
			case "reload":
				return reload(sender, args);
			case "party":
				return this.miniGamePartyCommand.party(sender, args);
			case "settings":
				return this.miniGameSettingsConfigCommand.settings(sender, args);
			case "games":
				return this.miniGameGamesConfigCommand.games(sender, args);
			}
		} catch (Exception e) {
			if (Setting.DEBUG_MODE) {
				e.printStackTrace();
			}
		}

		// print usage
		this.minigameHelpCommand.printHelp(sender, args);

		return true;
	}

	private boolean giveFunctionItems(CommandSender sender) throws Exception {
		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return true;
		}

		// give menu opener
		Player p = (Player) sender;
		FunctionItem.giveAll(p);

		return true;
	}

	private boolean help(CommandSender sender, String[] args) throws Exception {
		this.minigameHelpCommand.printUsage(sender);
		this.minigameHelpCommand.printWikiUrl(sender);
		return true;
	}

	private boolean join(CommandSender sender, String[] args) throws Exception {
		/*
		 * minigame join <title>
		 */

		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return true;
		}
		Player p = (Player) sender;

		String title = args[1];
		this.minigameManager.joinGame(p, title);
		return true;
	}

	private boolean view(CommandSender sender, String[] args) throws Exception {
		/*
		 * minigame view <title>
		 */

		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return true;
		}
		Player p = (Player) sender;

		String title = args[1];
		this.minigameManager.viewGame(p, title);

		return true;
	}

	private boolean leave(CommandSender sender, String[] args) throws Exception {
		/*
		 * minigame leave
		 */

		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return true;
		}
		Player p = (Player) sender;

		// leave or unview
		if (this.minigameManager.isPlayingMiniGame(p)) {
			this.minigameManager.leaveGame(p);
		} else if (this.minigameManager.isViewingMiniGame(p)) {
			this.minigameManager.unviewGame(p);
		}
		return true;
	}

	private boolean list(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "play.list")) {
			return true;
		}

		List<MiniGame> games = this.minigameManager.getMiniGameList();

		// info
		String info = "\n" + ChatColor.BOLD + "[MiniGame List]";
		info += "\n" + "※ " + ChatColor.RED + "RED" + ChatColor.WHITE + ": already started";
		info += "\n" + "※ " + ChatColor.GREEN + "GREEN" + ChatColor.WHITE + ": can join";
		info += "\n" + "※ " + ChatColor.STRIKETHROUGH + "STRIKETHROUGH" + ChatColor.WHITE + ": inactive";
		sender.sendMessage(info);

		// print mingames
		for (MiniGame game : games) {
			String gameTitle = game.getTitle();
			if (!game.isActive()) {
				sender.sendMessage("- " + ChatColor.STRIKETHROUGH + gameTitle);
			} else if (game.isStarted()) {
				sender.sendMessage("- " + ChatColor.RED + gameTitle);
			} else if (!game.isStarted()) {
				sender.sendMessage("- " + ChatColor.GREEN + gameTitle);
			}
		}

		return true;
	}

	private boolean menu(CommandSender sender, String[] args) throws Exception {
		// only player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Player");
			return true;
		}
		Player p = (Player) sender;

		MiniGameMenuManager menuManager = this.minigameManager.getMiniGameMenuManager();
		menuManager.openMenu(p);
		return true;
	}

	// /mw backup [<backup-folder>]
	private boolean backup(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "config.backup")) {
			return true;
		}

		String backupDataDirName = null;
		boolean isOverwrite = false;

		// when backup folder is given
		if (args.length == 2) {
			backupDataDirName = args[1];

			// [IMPORTATN] To overwriting, add wildcard in front of the dir name
			// e.g. /mw backup *Today
			String wildcard = "*";
			isOverwrite = backupDataDirName.contains(wildcard);
			backupDataDirName = backupDataDirName.replace(wildcard, "");

			// check file name vaildation
			if (!FileTool.isValidFileName(backupDataDirName)) {
				sender.sendMessage(backupDataDirName + " is not vaild for folder name");
				return true;
			}

			// check duplication
			if (!isOverwrite && this.dataManager.existBackupData(backupDataDirName)) {
				sender.sendMessage(backupDataDirName + " backup folder is already exist");
				return true;
			}
		}

		// create backup data directory
		File backupDataFile = this.dataManager.saveBackupData(backupDataDirName);
		backupDataDirName = backupDataFile.getName();

		// send msg
		sender.sendMessage("Backup folder " + ChatColor.GREEN + backupDataDirName + ChatColor.RESET
				+ ((isOverwrite) ? " is overwritten" : " created"));

		return true;
	}

	// mw reload [<folder>]
	private boolean reload(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "config.reload")) {
			return true;
		}

		// when backup folder is given
		if (args.length == 2) {
			String dataDirName = args[1];

			// check backup dir is exist
			if (!this.dataManager.existBackupData(dataDirName)) {
				sender.sendMessage(dataDirName + " backup folder is not exist");
				return true;
			}

			// load(copy) backup data
			this.dataManager.loadBackupData(dataDirName, Utils.getDataFolder());
			sender.sendMessage("Backup folder " + ChatColor.GREEN + dataDirName + ChatColor.RESET + " loaded");
		}

		// reload "setting.yml", all minigames
		this.dataManager.reloadAllData();
		
		// load template-worlds
		Utils.loadTemplateWorlds();

		// complete msg
		sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "[ Reload Complete] ");
		sender.sendMessage("- " + this.minigameManager.getFileName());
		if (Setting.EDIT_MESSAGES) {
			sender.sendMessage("- Language messages");
		}
		this.minigameManager.getMiniGameList().forEach(m -> {
			sender.sendMessage("- " + m.getTitleWithClassName());
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
