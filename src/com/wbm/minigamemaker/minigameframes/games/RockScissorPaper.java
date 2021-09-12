package com.wbm.minigamemaker.minigameframes.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.wbm.minigamemaker.minigameframes.SoloBattleMiniGame;

@SuppressWarnings("deprecation")
public class RockScissorPaper extends SoloBattleMiniGame {
	/*
	 * 설명: 가위 바위 보 게임, 일정 시간내에 선택지를 입력하고, 일정시간 후에 결과가 나옴(안내면 패배) 타입:
	 * SoloBattleMiniGame
	 */

	Map<Player, Selection> selections;

	enum Selection {
		R, S, P;

		static Selection random() {
			int r = (int) (Math.random() * 3);
			if (r == 0) {
				return R;
			} else if (r == 1) {
				return S;
			} else {
				return P;
			}
		}

		static Selection getSelectionWithString(String str) {
			switch (str) {
			case "R":
			case "r":
				return R;
			case "S":
			case "s":
				return S;
			case "P":
			case "p":
				return P;
			default:
				return null;
			}
		}

	}

	public RockScissorPaper() {
		super("RSP", 2, 15, 15);
		this.getSetting().setForcePlayerCount(true);
		this.selections = new HashMap<Player, RockScissorPaper.Selection>();
	}

	@Override
	protected void initGameSetting() {
		this.selections.clear();
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();
		// input random selection
		for (Player p : this.getPlayers()) {
			this.selections.put(p, Selection.random());
		}
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof AsyncPlayerChatEvent) {
			AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
			Player p = e.getPlayer();
			String msg = e.getMessage();
			Selection selection = Selection.getSelectionWithString(msg);

			// put selection and cancel chat event
			if (selection != null) {
				this.selections.put(p, selection);
				e.setCancelled(true);
				this.sendMessage(p, "your choice: " + ChatColor.GREEN + selection.name());
			}
		}
	}

	Player getWinner(Player p1, Player p2) {
		Selection p1Selection = this.selections.get(p1);
		Selection p2Selection = this.selections.get(p2);

		// DRAW
		if (p1Selection == p2Selection) {
			return null;
		}

		if (p1Selection == Selection.R) {
			if (p2Selection == Selection.S) {
				return p1;
			}
		} else if (p1Selection == Selection.S) {
			if (p2Selection == Selection.P) {
				return p1;
			}
		} else if (p1Selection == Selection.P) {
			if (p2Selection == Selection.R) {
				return p1;
			}
		}
		return p2;
	}

	@Override
	protected void runTaskBeforeFinish() {
		if (!(this.getPlayerCount() == 2)) {
			return;
		}

		Player p1 = this.getPlayers().get(0);
		Player p2 = this.getPlayers().get(1);
		Player winner = this.getWinner(p1, p2);

		// result
		if (winner != null) {
			this.plusScore(winner, 1);
		}

		// print result
		String p1Selection = "" + ChatColor.GREEN + ChatColor.BOLD + this.selections.get(p1).name() + ChatColor.WHITE;
		String p2Selection = "" + ChatColor.GREEN + ChatColor.BOLD + this.selections.get(p2).name() + ChatColor.WHITE;
		this.sendMessageToAllPlayers(
				String.format("%s[%s] : [%s]%s", p1.getName(), p1Selection, p2Selection, p2.getName()));
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("enter chat: R or S or P");
		tutorial.add("Result will be appeared at the end");
		tutorial.add("selection can be changed");
		return tutorial;
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
