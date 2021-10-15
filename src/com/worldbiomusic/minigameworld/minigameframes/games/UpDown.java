package com.worldbiomusic.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.worldbiomusic.minigameworld.minigameframes.SoloMiniGame;

public class UpDown extends SoloMiniGame {
	enum Action {
		UP, DOWN;

		public static Action random() {
			if (Math.random() < 0.5) {
				return UP;
			} else {
				return DOWN;
			}
		}

		public boolean isSame(Event event) {
			if (event instanceof PlayerJumpEvent) {
				return this == UP;
			} else if (event instanceof PlayerToggleSneakEvent) {
				return this == DOWN;
			} else {
				return false;
			}
		}

		public String getEventName() {
			if (this == UP) {
				return "Jump";
			} else if (this == DOWN) {
				return "Sneak";
			} else {
				return "ERROR";
			}
		}
	}

	private Action action;

	public UpDown() {
		super("UpDown", 30, 10);
	}

	@Override
	protected void initGameSettings() {
		this.action = Action.random();
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerJumpEvent) {
			this.checkPlayerAction(event);
		} else if (event instanceof PlayerToggleSneakEvent) {
			PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;

			// check when player is not down state
			if (!e.getPlayer().isSneaking()) {
				this.checkPlayerAction(event);
			}
		}
	}

	private void checkPlayerAction(Event event) {
		if (this.action.isSame(event)) {
			// plus score
			this.plusScore(1);

			// change to random action
			this.action = Action.random();

			// send next action title
			this.sendActionMessage();
		} else {
			this.finishGame();
		}
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// send title
		this.sendActionMessage();
	}

	private void sendActionMessage() {
		// message
		this.sendMessage(getSoloPlayer(), "Next Action: " + this.action.name());
		
		// title
		this.sendTitle(getSoloPlayer(), this.action.name(), this.action.getEventName());
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Up = Jump");
		tutorial.add("Down = Sneak");

		return tutorial;
	}

}
