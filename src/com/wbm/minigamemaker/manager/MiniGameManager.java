package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

public class MiniGameManager implements YamlMember {
	// API 용 클래스
	// Singleton 사용
	// TODO: 명령어로 gameSetting값 조절할 수 있게 기능 추가

	// Singleton 객체
	private static MiniGameManager instance = new MiniGameManager();
	private static boolean instanceCreated = false;

	// 미니게임 관리 리스트
	private List<MiniGame> minigames;

	private Set<Class<? extends Event>> possibleEventList;

	private Map<String, Object> setting;

	// 게임 끝나고 돌아갈 서버 스폰
	private Location serverSpawn;

	// 미니게임 파일 데이터
	private MiniGameDataManager minigameDataM;

	// 이벤트의 관련있는 플레이어 변수 (메모리를 위해 멤버변수로 설정)
	private List<Player> eventPlayers;

//	// yaml config
//	private FileConfiguration config;

	// getInstance() 로 접근해서 사용
	private MiniGameManager() {
		// 미니게임 리스트 초기화
		this.minigames = new ArrayList<>();

		this.eventPlayers = new ArrayList<Player>();

		this.setting = new HashMap<String, Object>();
		this.initSettingData();

		// 처리 가능한 이벤트 목록 초기화
		this.possibleEventList = new HashSet<>();
		// 처리가능한 이벤트 목록들 (Player 확인가능한 이벤트)
		// PlayerEvent 서브 클래스들은 대부분 가능(Chat, OpenChest, CommandSend 등등)
		this.possibleEventList.add(BlockBreakEvent.class);
		this.possibleEventList.add(BlockPlaceEvent.class);
		this.possibleEventList.add(PlayerEvent.class);
		this.possibleEventList.add(EntityEvent.class);
		this.possibleEventList.add(HangingEvent.class);
		this.possibleEventList.add(InventoryEvent.class);
		this.possibleEventList.add(InventoryMoveItemEvent.class);
		this.possibleEventList.add(InventoryPickupItemEvent.class);
		this.possibleEventList.add(PlayerLeashEntityEvent.class);

	}

	public static MiniGameManager getInstance() {
		if (instanceCreated == false) {
			instanceCreated = true;
			return instance;
		}
		return null;
	}

	private void initSettingData() {
		/*
		 * set basic setting.yml
		 */
		// serverSpawn 설정
		if (!this.setting.containsKey("spawnLocation")) {
			this.setting.put("spawnLocation", new Location(Bukkit.getWorld("world"), 0, 4, 0, 90, 0));
		}

		this.serverSpawn = (Location) this.setting.get("spawnLocation");

		// minigameSign
		if (!this.setting.containsKey("minigameSign")) {
			this.setting.put("minigameSign", true);
		}

		// minigameCommand
		if (!this.setting.containsKey("minigameCommand")) {
			this.setting.put("minigameCommand", true);
		}
	}

	public boolean joinGame(Player p, String title) {
		/*
		 * 플레이어가 미니게임에 참여하는 메소드
		 * 
		 * check player is not playing minigame
		 */
		if (!this.checkPlayerIsPlayingMiniGame(p)) {
			MiniGame game = this.getMiniGame(title);
			if (game == null) {
				p.sendMessage(title + " minigame does not exist");
				return false;
			} else {
				return game.joinGame(p);
			}
		} else {
			p.sendMessage("You already joined other minigame");
			return false;
		}
	}

	public boolean leaveGame(Player p) {
		/*
		 * check player is playing minigame
		 */

		if (this.checkPlayerIsPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);
			return playingGame.leaveGame(p);
		} else {
			p.sendMessage("You're not playing any minigame");
			return false;
		}

	}

	public void handleException(Player p, MiniGame.Exception exception, Object arg) {
		/*
		 * check player is playing minigame (API)
		 */
		if (this.checkPlayerIsPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);
			playingGame.handleException(p, exception, arg);
		}
	}

	public boolean isPossibleEvent(Event event) {
		// 미니게임에서 처리가능한 이벤트인지 체크 (possibleEvent 클래스 구현 클래스인지 확인)
		for (Class<? extends Event> c : this.possibleEventList) {
			if (c.isAssignableFrom(event.getClass())) {
				return true;
			}
		}
		return false;
	}

	public boolean isPossibleEvent(Class<? extends Event> event) {
		// 미니게임에서 처리가능한 이벤트인지 체크 (possibleEvent 클래스 구현 클래스인지 확인)
		for (Class<? extends Event> c : this.possibleEventList) {
			if (c.isAssignableFrom(event)) {
				return true;
			}
		}
		return false;
	}

	boolean processEvent(Event e) {
		/*
		 * 이벤트에서 플레이어 가져와서 플레이어가 참여중인 미니게임으로 이벤트 넘기기
		 */
		// 허용되는 이벤트만 아닐 시 false 반환
		if (!this.isPossibleEvent(e)) {
			return false;
		}

		// check player is trying to join or leave game with Sign
		this.checkPlayerTryingMiniGameSign(e);

		// 이벤트에서 플레이어 추출
		List<Player> players = this.getPlayersFromEvent(e);

		// 미니게임과 관련된 이벤트가 아닐 경우 처리 안함
		if (players.isEmpty()) {
			return false;
		}

		// 이벤트에서 얻은 플레이어들에 대한 미니게임 이벤트 처리
		for (Player p : players) {
			// 플레이어가 플레이중인 미니게임이 없으면 반환
			if (this.checkPlayerIsPlayingMiniGame(p)) {
				MiniGame playingGame = this.getPlayingMiniGame(p);
				playingGame.passEvent(e);
			}
		}

		return true;
	}

	private boolean checkPlayerTryingMiniGameSign(Event event) {
		// check player is trying to join or leaving with sign
		if (event instanceof PlayerInteractEvent) {
			PlayerInteractEvent e = (PlayerInteractEvent) event;
			Player p = e.getPlayer();

			// process
			Block block = e.getClickedBlock();
			if (block != null) {
				if (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN) {
					if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
						Sign sign = (Sign) block.getState();
						String minigame = sign.getLines()[0];
						String title = sign.getLines()[1];

						// check minigameSign option
						boolean minigameSign = (boolean) this.getGameSetting().get("minigameSign");
						if (minigame.equals("[MiniGame]") || minigame.equals("[Leave MiniGame]")) {
							if (!minigameSign) {
								p.sendMessage("minigameSign option is false");
								return true;
							}

							// check sign
							if (minigame.equals("[MiniGame]")) {
								// join
								this.joinGame(p, title);
							} else if (minigame.equals("[Leave MiniGame]")) {
								// leave
								this.leaveGame(p);
							}

							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean checkPlayerIsPlayingMiniGame(Player p) {
		return this.getPlayingMiniGame(p) != null;
	}

	public MiniGame getMiniGame(String title) {
		for (MiniGame game : this.minigames) {
			if (game.getTitle().equalsIgnoreCase(title)) {
				return game;
			}
		}
		return null;
	}

	public MiniGame getPlayingMiniGame(Player p) {
		for (MiniGame game : this.minigames) {
			if (game.containsPlayer(p)) {
				return game;
			}
		}
		return null;
	}

	private List<Player> getPlayersFromEvent(Event e) {
		// 플레이어 변수 clear
		this.eventPlayers.clear();

		// Event마다 Player 얻는 작업
		if (e instanceof BlockBreakEvent) {
			eventPlayers.add(((BlockBreakEvent) e).getPlayer());
		} else if (e instanceof BlockPlaceEvent) {
			eventPlayers.add(((BlockPlaceEvent) e).getPlayer());
		} else if (e instanceof PlayerEvent) {
			eventPlayers.add(((PlayerEvent) e).getPlayer());
		} else if (e instanceof EntityEvent) {
			Entity entity = ((EntityEvent) e).getEntity();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof HangingEvent) {
			Entity entity = ((HangingEvent) e).getEntity();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof InventoryEvent) {
			HumanEntity entity = (((InventoryEvent) e).getView()).getPlayer();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof InventoryMoveItemEvent) {
			Inventory inv = ((InventoryMoveItemEvent) e).getInitiator();
			List<HumanEntity> viewers = inv.getViewers();
			for (HumanEntity humanEntity : viewers) {
				if (humanEntity instanceof Player) {
					eventPlayers.add((Player) humanEntity);
				}
			}
		} else if (e instanceof PlayerLeashEntityEvent) {
			eventPlayers.add(((PlayerLeashEntityEvent) e).getPlayer());
		}

		return eventPlayers;
	}

	public List<MiniGame> getMiniGameList() {
		return this.minigames;
	}

	public boolean registerMiniGame(MiniGame newGame) {
		// 이미 같은 미니게임이 minigames에 있으면 등록 실패
		// 주의: 미니게임.title로 비교하면 안됨 -> 클래스 이름으로 비교해야 함(대소문자 구별x),
		// 왜냐면 minigames.json파일에서 클래스 이름으로 구분해서 저장하였기 때문
		String newGameClassName = newGame.getClassName();
		for (MiniGame game : this.minigames) {
			String existGameClassName = game.getClassName();
			if (existGameClassName.equalsIgnoreCase(newGameClassName)) {
				BroadcastTool.warn(newGame.getTitleWithClassName() + " minigame is already registered");
				BroadcastTool.warn(
						"Cause: the same minigame " + game.getTitleWithClassName() + " minigame is already registered");
				return false;
			}
		}

		// 등록하는 미니게임 인스턴스에 이전에 저장된 minigames.json 파일 데이터 적용
		if (this.minigameDataM.isMinigameDataExists(newGame)) {
			this.minigameDataM.applyMiniGameDataToInstance(newGame);
		} else {
			this.minigameDataM.addMiniGameData(newGame);
		}

		// 게임 등록
		this.minigames.add(newGame);

		BroadcastTool.info("" + ChatColor.GREEN + ChatColor.BOLD + newGame.getTitleWithClassName() + ChatColor.WHITE
				+ " minigame is registered");
		return true;
	}

	public boolean unregisterMiniGame(MiniGame minigame) {
		// 등록된 미니게임 삭제
		if (this.minigames.remove(minigame)) {
			BroadcastTool.info(minigame.getTitleWithClassName() + " minigame is removed");
			return true;
		} else {
			return false;
		}
	}

	public Map<String, Object> getGameSetting() {
		return this.setting;
	}

	public Location getServerSpawn() {
		return this.serverSpawn;
	}

	public void setMiniGameDataManager(MiniGameDataManager minigameDataM) {
		this.minigameDataM = minigameDataM;
	}

//	@Override
//	public void distributeData(Gson gson,String jsonString) {
//		if (jsonString == null) {
//			return;
//		}
//		
//		this.setting = gson.fromJson(jsonString, new TypeToken<Map<String, Object>>() {
//		}.getType());
//		// update gameSetting
//		this.initSettingData();
//
//	}
//
//	@Override
//	public Object getData() {
//		return this.setting;
//	}

	@Override
	public void setData(YamlManager yamlM, FileConfiguration config) {
//		this.config = config;

		// sync config setting with variable setting
		if (config.isSet("setting")) {
			this.setting = YamlHelper.ObjectToMap(config.getConfigurationSection("setting"));
		}
		config.set("setting", this.setting);

		// check setting has basic values
		this.initSettingData();
	}

	@Override
	public String getFileName() {
		return "setting.yml";
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
