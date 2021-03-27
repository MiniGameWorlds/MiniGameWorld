package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.wbm.minigamemaker.games.FitTool;

public class MiniGameManager {
	private Map<MiniGameType, MiniGame> minigames;

	public MiniGameManager() {
		this.minigames = new HashMap<>();

		this.minigames.put(MiniGameType.FIT_TOOl, new FitTool());
	}

	public void joinGame(Player p, MiniGameType gameType) {
		/*
		 * 플레이어가 미니게임에 참여하는 메소드
		 */
		MiniGame game = this.getMiniGame(gameType);
		game.joinGame(p);
	}

	public void processEvent(Event e) {
		// 처리가능한 이벤트 목록들 (Player 확인가능한 이벤트)
		// PlayerEvent 서브 클래스들은 대부분 가능(Chat, OpenChest, CommandSend 등등)
		@SuppressWarnings("rawtypes")
		List<Class> possibleEventList = new ArrayList<>();
		possibleEventList.add(PlayerInteractEvent.class);
		possibleEventList.add(BlockBreakEvent.class);
		possibleEventList.add(BlockPlaceEvent.class);
		possibleEventList.add(EntityDamageEvent.class);

		// 허용되는 이벤트만 처리
		if (possibleEventList.contains(e.getClass())) {
			// 이벤트에서 플레이어 가져와서 플레이중인 미니게임에 이벤트 넘기기
			Player player = this.getPlayerFromEvent(e);
			MiniGame playingGame = this.getPlayingGame(player);
			if (playingGame != null) {
				playingGame.passEvent(e);
			}
		}
	}

	public void processException(Player p) {
		MiniGame playingGame = this.getPlayingGame(p);
		if (playingGame != null) {
			playingGame.handleException(p);
		}
	}

	private MiniGame getMiniGame(MiniGameType gameType) {
		return this.minigames.get(gameType);
	}

	private MiniGame getPlayingGame(Player p) {
		for (MiniGame game : this.minigames.values()) {
			if (game.containsPlayer(p)) {
				return game;
			}
		}
		return null;
	}

	private Player getPlayerFromEvent(Event e) {
		if (e instanceof PlayerInteractEvent) {
			return ((PlayerInteractEvent) e).getPlayer();
		} else if (e instanceof BlockBreakEvent) {
			return ((BlockBreakEvent) e).getPlayer();
		} else if (e instanceof BlockPlaceEvent) {
			return ((BlockPlaceEvent) e).getPlayer();
		} else if(e instanceof EntityDamageEvent) {
			return (Player)((EntityDamageEvent) e).getEntity();
		}
		return null;
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
//
//
//
//
//
//
