package com.wbm.minigamemaker.util;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.wbm.minigamemaker.Main;

public class PlayerTool {
	public static Collection<? extends Player> onlinePlayers() {
		return Bukkit.getOnlinePlayers();
	}

	public static int onlinePlayersCount() {
		return onlinePlayers().size();
	}

	public static void heal(Player p) {
		p.setHealth(20);
		p.setFoodLevel(20);
	}

	public static void removeAllState(Player p) {
		// unhide
		unhidePlayerFromEveryone(p);
		// set glow off
		p.setGlowing(false);
		// 모든 포션효과 제거
		for (PotionEffect potion : p.getActivePotionEffects()) {
			p.removePotionEffect(potion.getType());
		}
	}

	public static void setHungry(Player p, int amount) {
		p.setFoodLevel(amount);
	}

	public static void hidePlayerFromAnotherPlayer(Player hideTarget, Player anotherPlayer) {
		anotherPlayer.hidePlayer(Main.getInstance(), hideTarget);
	}

	public static void hidePlayerFromOtherPlayers(Player hideTarget, List<Player> others) {
		for (Player p : others) {
			p.hidePlayer(Main.getInstance(), hideTarget);
		}
	}

	public static void hidePlayerFromEveryone(Player hideTarget) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.hidePlayer(Main.getInstance(), hideTarget);
		}
	}

	public static void unhidePlayerFromAnotherPlayer(Player unhideTarget, Player anotherPlayer) {
		anotherPlayer.showPlayer(Main.getInstance(), unhideTarget);
	}

	public static void unhidePlayerFromOtherPlayers(Player unhideTarget, List<Player> others) {
		for (Player p : others) {
			p.showPlayer(Main.getInstance(), unhideTarget);
		}
	}

	public static void unhidePlayerFromEveryone(Player unhideTarget) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.showPlayer(Main.getInstance(), unhideTarget);
		}
	}

	public static void playSound(Player p, Sound sound) {
		p.playSound(p.getLocation(), sound, 10, 1);
	}

	public static void playSoundToEveryone(Sound sound) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			playSound(p, sound);
		}
	}

	public static void removeAllPotionEffects(Player p) {
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
	}

}
