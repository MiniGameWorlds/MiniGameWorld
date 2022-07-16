package com.worldbiomusic.minigameworld.util;

import java.io.File;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import com.onarandombox.MultiverseCore.utils.WorldManager;
import com.wbm.plugin.util.CollectionTool;
import com.wbm.plugin.util.WorldTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {
	static MiniGameWorldMain main = MiniGameWorldMain.getInstance();
	static ConsoleCommandSender sender = main.getServer().getConsoleSender();

	public static String getMessagePrefix() {
		return "[" + Setting.MESSAGE_PREFIX + "] ";
	}

	public static void sendMsg(CommandSender sender, String msg) {
		sendMsg(sender, msg, true);
	}

	public static void sendMsg(CommandSender sender, String msg, boolean prefix) {
		if (prefix) {
			msg = getMessagePrefix() + msg;
		}
		sender.sendMessage(msg);
	}

	public static void sendMsgToEveryone(String msg) {
		sendMsgToEveryone(msg, true);
	}

	public static void sendMsgToEveryone(String msg, boolean prefix) {
		Bukkit.getOnlinePlayers().forEach(p -> sendMsg(p, msg, prefix));
	}

	public static void sendMsg(Player p, BaseComponent compo) {
		sendMsg(p, compo, true);
	}

	public static void sendMsg(Player p, BaseComponent compo, boolean prefix) {
		TextComponent msg = new TextComponent();
		if (prefix) {
			msg.addExtra(getMessagePrefix());
		}

		msg.addExtra(compo);
		p.spigot().sendMessage(msg);
	}

	public static void sendMsgToEveryone(BaseComponent compo) {
		sendMsgToEveryone(compo, true);
	}

	public static void sendMsgToEveryone(BaseComponent compo, boolean prefix) {
		Bukkit.getOnlinePlayers().forEach(p -> sendMsg(p, compo, prefix));
	}

	public static void info(String msg) {
		sender.sendMessage(getMessagePrefix() + msg);
	}

	public static void warning(String msg) {
		sender.sendMessage(ChatColor.YELLOW + getMessagePrefix() + msg);
	}

	public static void debug(String msg) {
		if (Setting.DEBUG_MODE) {
			info(ChatColor.RED + "[DEBUG] " + msg);
		}
	}

	public static boolean checkPerm(CommandSender sender, String permission) {
		return sender.hasPermission("minigameworld." + permission);
	}

	public static File getServerFile(String file) {
		return new File(main.getServer().getWorldContainer(), file);
	}

	public static File getDataFolder() {
		return main.getDataFolder();
	}

	public static File getMiniGamesDir() {
		return new File(getDataFolder(), Setting.MINIGAMES_DIR);
	}
	
	public static File getMessagesDir() {
		return new File(getDataFolder(), Setting.MESSAGES_DIR);
	}

	public static void syncMapKeys(Map<String, Object> configMap, Map<String, Object> pureMap) {
		// remove not necessary keys to avoid error (i.e. keys for some updates)
		if (Setting.REMOVE_NOT_NECESSARY_KEYS) {
			CollectionTool.removeNotNecessaryKeys(configMap, pureMap);
		}

		// Restore before apply to avoid error (i.e. keys for some updates)
		CollectionTool.restoreMissedKeys(configMap, pureMap);

		// sync map keys
		CollectionTool.syncKeyOrder(configMap, pureMap);
	}

	/**
	 * Call event and return event is cancelled or not <br>
	 * If event is not <b>Cancellable</b>, return always false
	 * 
	 * @param event Calling event
	 * @return True if event is cancelled
	 */
	public static boolean callEvent(Event event) {
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event instanceof Cancellable) {
			// check event is cancelled
			return ((Cancellable) event).isCancelled();
		}
		return false;
	}

	public static void registerEventListener(Listener listener) {
		main.getServer().getPluginManager().registerEvents(listener, main);
	}

	/**
	 * Default minigame location
	 * 
	 * @return default minigame location
	 */
	public static Location getDefaultLocation() {
		// check "world"
		Location loc = new Location(null, 0, 4, 0);

		// "world" world
		World w = Bukkit.getWorld("world");
		if (w != null) {
			loc.setWorld(w);
			return loc;
		}

		// check any overworld
		for (World world : Bukkit.getWorlds()) {
			if (world.getEnvironment() == Environment.NORMAL) {
				loc.setWorld(world);
				return loc;
			}
		}

		// just first world
		loc.setWorld(Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0));
		return loc;
	}

	public static void loadTemplateWorlds() {
		WorldManager worldManager = (WorldManager) MiniGameWorldMain.multiverseCore().getMVWorldManager();
		// load if world is not loaded yet
		worldManager.getPotentialWorlds().stream().filter(Setting.TEMPLATE_WORLDS::contains).forEach(w -> {
			Utils.warning("world: " + w);
			World loadedWorld = WorldTool.create(w);
			Utils.warning("loaded: " + (loadedWorld != null));

			Environment env = loadedWorld.getEnvironment();
			String seed = "" + loadedWorld.getSeed();
			WorldType type = loadedWorld.getWorldType();
			boolean structure = loadedWorld.canGenerateStructures();
			MiniGameWorldMain.multiverseCore().getMVWorldManager().addWorld(w, env, seed, type, structure, null);

			boolean mvLoaded = MiniGameWorldMain.multiverseCore().getMVWorldManager().loadWorld(w);
			Utils.warning("mvLoaded: " + mvLoaded);
		});
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
