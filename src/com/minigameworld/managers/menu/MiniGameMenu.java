package com.minigameworld.managers.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.minigameworld.events.menu.MenuClickEvent;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.LangUtils;
import com.minigameworld.util.Messenger;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.ItemStackTool;
import com.wbm.plugin.util.PlayerTool;

/**
 * [IMPORTANT]<br>
 * - (Only for MiniGameWorld) Menu icon must be executed with command, because
 * of command permission (Join-Game menu = make player run
 * {@code "/minigame join <title>"})
 */
public class MiniGameMenu {
	private Player player;
	private MiniGameManager minigameManager;
	private Inventory inv;
	private int currentPage;

	private final int MINIGAME_ICON_LIST_SIZE = 27;
	private final int MINIGAME_ICON_START_SLOT = 18;

	private Messenger messenger;

	public enum BaseIcon {
		LEAVE_GAME(ItemStackTool.item(Material.OAK_DOOR, "Leave Game"), 1),
		HORIZON_LINE(ItemStackTool.item(Material.BARRIER, " "), 9),
		PREVIOUS_PAGE(ItemStackTool.item(Material.REDSTONE_TORCH, "<"), 48),
		CURRENT_PAGE(ItemStackTool.item(Material.PAPER, "1"), 49),
		NEXT_PAGE(ItemStackTool.item(Material.TORCH, ">"), 50);

		private ItemStack item;
		private int slot;

		BaseIcon(ItemStack item, int slot) {
			this.item = item;
			this.slot = slot;
		}

		public ItemStack getItem() {
			return this.item;
		}

		public int getSlot() {
			return this.slot;
		}

		public static boolean checkSlot(int slot) {
			return !Arrays.asList(values()).stream().filter(icon -> icon.getSlot() == slot).toList().isEmpty();
		}

		public static boolean checkIcon(ItemStack targetIcon) {
			return !Arrays.asList(values()).stream().filter(icon -> icon.getItem().equals(targetIcon)).toList()
					.isEmpty();
		}
	}

	public MiniGameMenu(Player player, MiniGameManager minigameManager) {
		this.player = player;
		this.minigameManager = minigameManager;
		this.currentPage = 1;
		this.messenger = new Messenger(LangUtils.path(MiniGameMenu.class));

		this.makeBaseIcons();
	}

	private void makeBaseIcons() {
		this.inv = Bukkit.createInventory(null, 9 * 6, Setting.MENU_INV_TITLE);

		// player head
		ItemStack playerHead = this.getPlayerHead(this.player);
		this.inv.setItem(0, playerHead);

		// leave game button
		this.inv.setItem(BaseIcon.LEAVE_GAME.getSlot(), BaseIcon.LEAVE_GAME.getItem());

		// horizon line
		for (int i = 0; i < 9; i++) {
			this.inv.setItem(BaseIcon.HORIZON_LINE.getSlot() + i, BaseIcon.HORIZON_LINE.getItem());
		}

		// page buttons
		this.inv.setItem(BaseIcon.PREVIOUS_PAGE.getSlot(), BaseIcon.PREVIOUS_PAGE.getItem());
		this.inv.setItem(BaseIcon.CURRENT_PAGE.getSlot(), BaseIcon.CURRENT_PAGE.getItem());
		this.inv.setItem(BaseIcon.NEXT_PAGE.getSlot(), BaseIcon.NEXT_PAGE.getItem());
	}

	private ItemStack getPlayerHead(Player p) {
		ItemStack item = PlayerTool.getPlayerHead(this.player);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + this.messenger.getMsg(player, "info"));
		item.setItemMeta(meta);
		return item;
	}

	private void updatePlayerHead() {
		ItemStack playerHead = this.inv.getItem(0);

		MiniGame playingMinigame = this.minigameManager.getPlayingGame(this.player);
		String noneStr = messenger.getMsg(player, "none");
		String title = (playingMinigame == null) ? noneStr : playingMinigame.getTitle();

		ItemMeta meta = playerHead.getItemMeta();
		List<String> lore = new ArrayList<>();
		String minigameStr = messenger.getMsg(player, "minigame");
		lore.add(ChatColor.WHITE + "- " + minigameStr + ": " + title);

		// add party members
		lore.add("");
		lore.add("" + ChatColor.YELLOW + ChatColor.BOLD + messenger.getMsg(player, "party"));
		List<Player> partyMembers = this.minigameManager.getPartyManager().getMembers(this.player);
		for (Player member : partyMembers) {
			lore.add(ChatColor.WHITE + "- " + member.getName());
		}

		meta.setLore(lore);
		playerHead.setItemMeta(meta);
	}

	private void emptyMiniGameIconList() {
		for (int i = MINIGAME_ICON_START_SLOT; i < MINIGAME_ICON_START_SLOT + MINIGAME_ICON_LIST_SIZE; i++) {
			this.inv.setItem(i, null);
		}
	}

	public Inventory createMenu(int page) {
		// set player head content
		this.updatePlayerHead();

		// update page
		this.updateCurrentPageNumber();

		// first, empty list
		this.emptyMiniGameIconList();

		// slot: 18 ~ 44 (count: 27)
		int minigameIndex = 0 + ((page - 1) * MINIGAME_ICON_LIST_SIZE);
		List<MiniGame> minigameList = this.minigameManager.getTemplateGames();
//		for (int i = 18; i < 45; i++, minigameIndex++) {
		for (int i = MINIGAME_ICON_START_SLOT; i < MINIGAME_ICON_START_SLOT
				+ MINIGAME_ICON_LIST_SIZE; i++, minigameIndex++) {
			if (minigameIndex >= minigameList.size()) {
				break;
			}
			MiniGame minigame = minigameList.get(minigameIndex);
			this.inv.setItem(i, this.getMiniGameIcon(minigame));
		}

		return this.inv;
	}

	public void updateMenu() {
		this.createMenu(this.currentPage);
	}

	private ItemStack getMiniGameIcon(MiniGame minigame) {
		ItemStack item = new ItemStack(minigame.getSetting().getIcon());

		// display name
		String displayName = minigame.getTitle();
		if (minigame.isStarted()) {
			displayName = ChatColor.RED + displayName + ChatColor.RESET;
		} else {
			displayName = ChatColor.GREEN + displayName + ChatColor.RESET;
		}

		// lore
		List<String> lore = new ArrayList<>();

		List<MiniGame> instances = this.minigameManager.getInstanceGames().stream()
				.filter(g -> g.isSameTemplate(minigame)).toList();
		String instanceCount = "" + ChatColor.GREEN + instances.stream().filter(g -> !g.isStarted()).toList().size()
				+ ChatColor.WHITE + "/" + instances.size();
		lore.add(ChatColor.WHITE + "- " + messenger.getMsg(player, "instance") + ": " + instanceCount);
		lore.add(ChatColor.WHITE + "- " + messenger.getMsg(player, "play-time") + ": " + minigame.getPlayTime() + " "
				+ messenger.getMsg(player, "sec"));
		lore.add(ChatColor.WHITE + "- " + messenger.getMsg(player, "type") + ": " + minigame.getFrameType());

		// apply
		item = ItemStackTool.item(item.getType(), displayName, lore);

		return item;
	}

	public boolean isSameInventory(Inventory inv) {
		return this.inv.equals(inv);
	}

	// suppose inventory has only 1 viewer
	public void processClickEvent(InventoryClickEvent e) {
		// call MenuClickEvent
		String area = "";
		int clickedSlot = e.getSlot();
		boolean isBaseArea = BaseIcon.checkSlot(clickedSlot);

		if (isBaseArea) {
			area = "BASE";
		} else if (MINIGAME_ICON_START_SLOT <= clickedSlot
				&& clickedSlot <= MINIGAME_ICON_START_SLOT + MINIGAME_ICON_LIST_SIZE) {
			area = "MINIGAME";
		}

		// call player menu click event (check event is cancelled)
		if (Utils.callEvent(new MenuClickEvent(e, Setting.MENU_INV_TITLE + ":" + area))) {
			return;
		}

		ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null) {
			return;
		}

		Player p = (Player) e.getViewers().get(0);
		if (clickedItem.equals(BaseIcon.LEAVE_GAME.getItem())) {

			// leave or unview
			if (this.minigameManager.isPlayingGame(p)) {
				this.minigameManager.leaveGame(p);
			} else if (this.minigameManager.isViewingGame(p)) {
				this.minigameManager.unviewGame(p);
			}
		} else if (clickedItem.equals(BaseIcon.PREVIOUS_PAGE.getItem())) {
			if (this.currentPage > 1) {
				this.currentPage -= 1;
			}
		} else if (clickedItem.equals(BaseIcon.NEXT_PAGE.getItem())) {
			if (this.currentPage < this.getMaxPageNumber()) {
				this.currentPage += 1;
			}
		} else if (this.isMiniGameIconSlot(e.getSlot())) {
			ItemStack item = e.getCurrentItem();
			String minigameTitle = item.getItemMeta().getDisplayName();

			// join
			if (e.getClick() == ClickType.LEFT) {
				this.minigameManager.joinGame(p, minigameTitle);
			}
			// view
			else if (e.getClick() == ClickType.RIGHT) {
				this.minigameManager.viewGame(p, minigameTitle);
			}
		}
	}

	private boolean isMiniGameIconSlot(int slot) {
		return (MINIGAME_ICON_START_SLOT <= slot) && (slot <= MINIGAME_ICON_START_SLOT + this.MINIGAME_ICON_LIST_SIZE);
	}

	private int getMaxPageNumber() {
		List<MiniGame> minigameList = this.minigameManager.getTemplateGames();
		return (int) Math.ceil(minigameList.size() / (double) MINIGAME_ICON_LIST_SIZE);
	}

	private void updateCurrentPageNumber() {
		this.inv.setItem(BaseIcon.CURRENT_PAGE.getSlot(),
				ItemStackTool.item(BaseIcon.CURRENT_PAGE.getItem().getType(), "" + this.currentPage));
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
