package com.minigameworld.dev;

import java.util.List;

import org.bukkit.event.entity.EntityDamageEvent;

import com.minigameworld.frames.SoloBattleMiniGame;
import com.minigameworld.managers.event.GameEvent;
import com.wbm.plugin.util.Utils;

public class GameA extends SoloBattleMiniGame {

	public GameA() {
		super("GameA", 2, 5, 30, 5);
	}

	@GameEvent
	public void onPlayerDamaged_GameA(EntityDamageEvent e) {
		Utils.debug("DAMGED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	@Override
	protected List<String> tutorial() {
		return null;
	}
}
