package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataMember;

import net.md_5.bungee.api.ChatColor;

public class MiniGameManager implements JsonDataMember {
	// API 용 클래스
	// Singleton 사용
	// TODO: 명령어로 gameSetting값 조절할 수 있게 기능 추가
	// TODO: MiniGameManager 시작하고, registerMiniGame메소드에서 등록될 때마다, minigames.json파일에
	// 미니게임 요소5개 map으로 값 넣기, or 이미 있으면 해당 미니게임 요소5개 수정 메소드로 파일의 값으로 적용(서버마다 미니게임
	// 플레이 시간, 위치 등이 다를 수 있기 때문에
	// MiniGame에서 5개 요소를 파일(minigames.json)에서 수정할 수 있게 하기

	// Singleton 객체
	private static MiniGameManager instance = new MiniGameManager();

	// 미니게임 관리 리스트
	private List<MiniGame> minigames;

	Set<Class<? extends Event>> possibleEventList;

	private Map<String, Object> setting;

	// 게임 끝나고 돌아갈 서버 스폰
	private Location serverSpawn;

	// 미니게임 파일 데이터
	MiniGameDataManager minigameDataM;

	// 이벤트의 관련있는 플레이어 변수 (메모리를 위해 멤버변수로 설정)
	List<Player> eventPlayers;

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
		return instance;
	}

	private void initSettingData() {
		// spawnLocation
		if (!this.setting.containsKey("spawnLocation")) {
			Map<String, Object> location = new HashMap<String, Object>();
			this.setting.put("spawnLocation", location);
			location.put("world", "world");
			location.put("x", 0.0);
			location.put("y", 4.0);
			location.put("z", 0.0);
			location.put("pitch", 90.0);
			location.put("yaw", 0.0);
		}

		// serverSpawn 설정
		@SuppressWarnings("unchecked")
		Map<String, Object> locationData = (Map<String, Object>) this.setting.get("spawnLocation");
		String world = (String) locationData.get("world");
		double x = (double) locationData.get("x");
		double y = (double) locationData.get("y");
		double z = (double) locationData.get("z");
		double pitch = (double) locationData.get("pitch");
		double yaw = (double) locationData.get("yaw");
		this.serverSpawn = new Location(Bukkit.getWorld(world), x, y, z, (float) pitch, (float) yaw);

		// signJoin
		if (!this.setting.containsKey("signJoin")) {
			this.setting.put("signJoin", true);
		}

	}

	public void joinGame(Player p, String title) {
		/*
		 * 플레이어가 미니게임에 참여하는 메소드
		 */
		MiniGame game = this.getMiniGame(title);
		game.joinGame(p);
	}

	public boolean isPossibleEvent(Event event) {
		// 미니게임에서 처리가능한 이벤트인지 체크
		return this.possibleEventList.contains(event.getClass());
	}

	public boolean processEvent(Event e) {
		// 허용되는 이벤트만 처리
		if (this.possibleEventList.contains(e.getClass())) {
			System.out.println("possible event");
			// 이벤트에서 플레이어 가져와서 플레이중인 미니게임에 이벤트 넘기기
			List<Player> players = this.getPlayersFromEvent(e);
			if (players.size() == 0) {
				System.out.println("can not get player from event");
				return false;
			}

			for (Player p : players) {
				MiniGame playingGame = this.getPlayingGame(p);
				// 미니게임 플레이 중인 플레이어 일때
				if (playingGame == null) {
					System.out.println("player is not playing game");
					return false;
				}
				playingGame.passEvent(e);
			}
		}
		return true;
	}

	public void processException(Player p) {
		MiniGame playingGame = this.getPlayingGame(p);
		if (playingGame != null) {
			playingGame.handleException(p);
		}
	}

	private MiniGame getMiniGame(String title) {
		for (MiniGame game : this.minigames) {
			if (game.getTitle().equalsIgnoreCase(title)) {
				return game;
			}
		}
		return null;
	}

	private MiniGame getPlayingGame(Player p) {
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
		// 같은 title이 있으면 등록 실패
		for (MiniGame game : this.minigames) {
			String newGameTitle = newGame.getTitle();
			if (game.getTitle().equalsIgnoreCase(newGameTitle)) {
				BroadcastTool.warn(newGameTitle + " minigame is not registered");
				BroadcastTool.warn("Cause: " + newGameTitle + " title is already exists");
				return false;
			}
		}

		// 게임 파일 데이터 적용
		if (this.minigameDataM.minigameDataExists(newGame.getTitle())) {
			this.minigameDataM.applyMiniGameData(newGame);
		} else {
			this.minigameDataM.addMiniGameData(newGame);
		}

		// 게임 등록
		this.minigames.add(newGame);

		BroadcastTool.info("" + ChatColor.GREEN + ChatColor.BOLD + newGame.getTitle() + ChatColor.WHITE
				+ " minigame is registered");
		return true;
	}

	public boolean removeMiniGame(String title) {
		// 등록된 미니게임 삭제
		for (MiniGame game : this.minigames) {
			if (game.getTitle().equalsIgnoreCase(title)) {
				BroadcastTool.info(title + " minigame is removed");
				return this.minigames.remove(game);
			}
		}
		return false;
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

	@SuppressWarnings("unchecked")
	@Override
	public void distributeData(String jsonString) {
		if (jsonString == null) {
			return;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		this.setting = gson.fromJson(jsonString, Map.class);
		// update gameSetting
		this.initSettingData();

	}

	@Override
	public Object getData() {
		return this.setting;
	}

	@Override
	public String getFileName() {
		return "setting.json";
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
