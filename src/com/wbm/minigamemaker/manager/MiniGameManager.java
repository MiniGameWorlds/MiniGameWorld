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
import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataMember;

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

	public boolean joinGame(Player p, String title) {
		/*
		 * 플레이어가 미니게임에 참여하는 메소드
		 */
		MiniGame game = this.getMiniGame(title);
		if (game == null) {
			return false;
		} else {
			return game.joinGame(p);
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

	boolean processEvent(Event e) {
		/*
		 * 이벤트에서 플레이어 가져와서 플레이어가 참여중인 미니게임으로 이벤트 넘기기
		 */
		// 허용되는 이벤트만 아닐 시 false 반환
		if (!this.isPossibleEvent(e)) {
			return false;
		}

		// 이벤트에서 플레이어 추출
		List<Player> players = this.getPlayersFromEvent(e);

		// 미니게임과 관련된 이벤트가 아닐 경우 처리 안함
		if (players.isEmpty()) {
//			BroadcastTool.warn("can not get player from event: " + e.getEventName());
			return false;
		}

		// 이벤트에서 얻은 플레이어들에 대한 미니게임 이벤트 처리
		for (Player p : players) {
			MiniGame playingGame = this.getPlayingGame(p);
			// 플레이어가 플레이중인 미니게임이 없으면 반환
			if (playingGame == null) {
//				BroadcastTool.warn("player is not playing game: ");
				return false;
			}
			playingGame.passEvent(e);
		}

		// 성공 반환
		return true;
	}

//	public void processException(Player p) {
//		MiniGame playingGame = this.getPlayingGame(p);
//		if (playingGame != null) {
//			playingGame.handleException(p);
//		}
//	}

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
		// 이미 같은 미니게임이 있으면 등록 실패
		// 주의: 미니게임.title로 비교하면 안됨 -> 클래스 이름으로 비교해야 함,
		// 왜냐면 minigames.json파일에서 클래스 이름으로 구분하기 때문
		String newGameClassName = newGame.getClassName();
		for (MiniGame game : this.minigames) {
			String existGameClassName = game.getClassName();
			if (existGameClassName.equalsIgnoreCase(newGameClassName)) {
				BroadcastTool.warn(newGame.getTitleWithClassName() + " minigame is not registered");
				BroadcastTool.warn("Cause: the same class " + game.getTitleWithClassName() + " minigame is already registered");
				return false;
			}
		}

		// 게임 파일 데이터 적용 (이미 minigames.json 파일에 newGame의 데이터가 있으면,
		// 등록하는 newGame미니게임 인스턴스에 데이터 적용
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
