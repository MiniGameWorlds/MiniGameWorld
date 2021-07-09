# 설명
- MiniGameMaker 기반의 미니게임 플러그인 제작에 관한 글
- MiniGameMaker 사용법은 [사용 위키] 참고
- MiniGame의 내부 구조, 동작은 [개발 위키] 참고

# 동작원리
![MiniGameMakerDesign](../img/MiniGameMakerDesign.JPG)
- 미니게임메이커 플러그인을 기반으로 만든 미니게임 플러그인들은 서버가 시작되면 미니게임메이커에 게임이 등록되고, 관리됨 

# 환경 세팅 방법
- [Paper]
- [MiniGameMaker]
- [wbmMC] 
- 다운로드 후 build path 추가

## `plugin.yml`
- depend: `[MiniGameMaker]` 추가

# 주의사항

# 처리 이벤트 목록
※ `paper api` 기반으로 만들어졌기 때문에, paper기반 event도 사용가능(참고: [Paper API])  
※ 미니게임 이벤트는 해당 이벤트의 Player가 미니게임 플레이중인것이 확인되면 해당 미니게임 이벤트 처리 메소드로 넘어감  
※ 처리 이벤트의 하위 이벤트까지 모두 사용가능 (예. PlayerEvent의 PlayerJumpEvent)
- BlockBreakEvent
- BlockPlaceEvent
- PlayerEvent
- EntityEvent
- HangingEvent
- InventoryEvent
- InventoryMoveItemEvent
- InventoryPickupItemEvent
- PlayerLeashEntityEvent

# 제작 방법
## 미니게임 제작
1. 미니게임 클래스 제작
- 미니게임의 특성에 따라 상속할 미니게임 클래스 결정 후, 유의사항에 맞게 클래스 제작 
- `SoloMiniGame`: 1인 플레이
- `SoloBattleMiniGame`: 개인전 플레이
- `TeamMiniGame`: 팀 플레이
- `TeamBattleMiniGame`: 팀 대전 플레이
```

```
2. 미니게임 메이커에 등록
- MiniGameManager.getInstace()로 객체를 가져와서 registerMiniGame() 메소드로 미니게임 등록
```java
MiniGameManager minigameManager = MiniGameManager.getInstance();
this.minigameManager.registerMiniGame(new FitTool());
```

## 미니게임 Task 관리
- getTaskManager()로 TaskManager를 가져와서 사용
- `태스크 등록`: getTaskManager().registerTask("name", new BukkitRunnable() { // code });
- (태스크 등록은 registerTasks() 메서드에서 작성되야 함)
- Example code
```java
@Override
protected void registerTasks() {
  // register task
  this.getTaskManager().registerTask("task1", new BukkitRunnable() {

    @Override
    public void run() {
      // code
    }
  });
}
```
- `태스크 호출`: getTaskManager().runTask("name");
- Example code
```java
@Override
protected void processEvent(Event event) {
  // code
  this.getTaskManager().runTask("task1");
}
```

- BukkitRunabble에 등록해서 사용한(run) task는 다시 사용 불가능([BukkitRunnable 참고]) (registerTasks()메소드가 항상 게임시작전에 실행되서 새로운 객체로 등록됨)
- MiniGame의 기본 시스템 관련 task(`_waitingTimer`, `_finishTimer`)는 등록, 사용 금지

## 기본적인 MiniGame의 오버라이딩 메소드 설명
- `initGameSetting()`: 미니게임 설정값 세팅메소드로 시작되기 전에 한번씩 꼭 실행되는 메소드
- `runTaskAfterStart()`: 미니게임이 실제로 시작 된 직후 실행되는 메소드
- `processEvent()`: 미니게임에 참여중인 플레이어의 이벤트를 처리해야 하는 메소드
- `getGameTutorialStrings()`: 미니게임 튜토리얼 출력할 문자 반환 메소드

# CustomData
- 미니게임 개발자가 임의로 커스텀 변수를 추가해서 미니게임 사용자가 변수를 바꿀 수 있게 도와주는 도구
- ❗주의사항: 아직 Json포맷의 정수, 소수 구분문제가 있어서 숫자는 무조건 (double)로 로드해야 함
- ❗주의사항: 아직 데이터관리를 Json으로 하는중이라(yml으로 바꿀 예정) 기본 타입밖에 저장이 안됨(마크 class serialize, deserialize 안되있어서)
1. MiniGame구현 클래스에서 `registerCustomData()` 메소드 오버라이딩 후 커스텀 데이터 추가
```java
@Override
protected void registerCustomData() {
  Map<String, Object> customData = this.getCustomData();
  customData.put("health", 30.0);
  List<ItemStack> items = new ArrayList<>();
  items.add(new ItemStack(Material.STONE_SWORD));
  customData.put("items", items);
}
```
2. 커스템 데이터 사용은 `getCustomData()`로 접근해서 어디에서나 사용 가능
```java
@SuppressWarnings("unchecked")
@Override
protected void initGameSetting() {
  this.health = (double) this.getCustomData().get("health");
}
```
or
```java
@Override
protected void processEvent(Event event) {
  // ~~~
  player.setHealthScale((double) this.getCustomData().get("health"));
  }
}
```


## 설정값
- MiniGameSetting으로 게임에 대해 다양하게 값을 설정할 수 있음
- 일부 설정값은 minigames.json 파일에서 수정 

## 미니게임 종료
- `endGame()` 메소드 사용

# 미니게임 참여/퇴장 방법 변경
- `참여`: MiniGameManager의 joinGame() 메소드 사용
- `퇴장`: MiniGameManager의 leaveGame() 메소드 사용

# API DOC


# minigames.json
- class 이름이 기준으로 등록됨
- 미니게임 생성자에서 설정한 기본값으로 설정됨

# 예외
- handleException()를 오버라이딩 해서 사용한다
- 게임 내 버그, 예외처리는 구현하는 미니게임에서 처리해야 

# 사용법 유튜브
[사용법]


# 연락
- [디스코드]

[사용 위키]: playingMiniGameWiki.md
[개발 위키]: ../devWiki/home.md
[Paper]: https://papermc.io/
[MiniGameMaker]: https://github.com/worldbiomusic/MiniGameMaker/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[디스코드]: https://discord.com/invite/fJbxSy2EjA
[Paper API]: https://papermc.io/javadocs/paper/1.16/index.html?overview-summary.html
[BukkitRunnable 참고]: https://www.spigotmc.org/threads/prevent-already-scheduled-as-xxx-error.202486/#post-2103877