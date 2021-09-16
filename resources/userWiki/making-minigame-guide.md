# 처리 가능 이벤트 목록
※ `paper api` 기반으로 만들어졌기 때문에, paper기반 event도 사용가능(참고: [Paper API])  
※ 미니게임 이벤트는 해당 이벤트의 Player가 미니게임 플레이중인것이 확인되면 해당 미니게임 이벤트 처리 메소드로 넘어감  
※ 하위 이벤트까지 모두 사용가능 (예. PlayerEvent의 PlayerJumpEvent)
```yaml
- BlockBreakEvent
- BlockPlaceEvent
- PlayerEvent
- EntityEvent
- HangingEvent
- InventoryEvent
- InventoryMoveItemEvent
- PlayerLeashEntityEvent
```



# 제작 순서
## 1. 플러그인 설정
- plugin.yml: depend



## 2. 미니게임 구현
### 기본적인 MiniGame의 오버라이딩 메소드 설명
- `initGameSetting()`: 미니게임 설정값 세팅메소드로 시작되기 전에 한번씩 꼭 실행되는 메소드
- `runTaskAfterStart()`: 미니게임이 실제로 시작 된 직후 실행되는 메소드
- `processEvent()`: 미니게임에 참여중인 플레이어의 이벤트를 처리해야 하는 메소드
- `registerTutorial()`: 미니게임 튜토리얼 글

### 미니게임 클래스 프레임 선택
- 미니게임의 특성에 따라 상속할 미니게임 클래스 결정 후, 유의사항에 맞게 클래스 제작 
#### `SoloMiniGame`
- 1인 플레이


#### `SoloBattleMiniGame`
- 개인전 플레이


#### `TeamMiniGame`
- 팀 플레이


#### `TeamBattleMiniGame`
- 팀 대전 플레이




## 3. MiniGameWorld에 등록
- MiniGameWorld.create()로 객체를 가져와서 registerMiniGame() 메소드로 미니게임 등록
```java
MiniGameWorld world = MiniGameWorld.create();
world.registerMiniGame(new FitTool());
```



# 옵션
## MiniGameSetting
- `getSetting()`으로 다양한 옵션 관리 가능



## Task
- TaskManager를 사용 (`getTaskManager()`)
- `태스크 등록`: `getTaskManager().registerTask("name", new BukkitRunnable() { // code });`
- 태스크 등록은 반드시 registerTasks() 메서드에서 작성되야 함
- `태스크 호출`: getTaskManager().runTask("name");
- BukkitRunabble에 등록해서 사용한(run) task는 다시 사용 불가능([BukkitRunnable 참고]) (registerTasks()메소드가 항상 게임시작전에 실행되서 새로운 객체로 등록됨)
- MiniGame의 기본 시스템 관련 task(`_waitingTimer`, `_finishTimer`)는 등록, 사용 금지
### How to register task
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
### How to run task
```java
@Override
protected void processEvent(Event event) {
  // code
  this.getTaskManager().runTask("task1");
}
```



## Exception handling
- handleException()를 오버라이딩 해서 사용
- 게임 내 버그, 예외처리는 구현하는 미니게임에서 처리해야 함



## Custom Data
- 미니게임 개발자가 임의로 커스텀 변수를 추가해서 미니게임 사용자가 변수를 바꿀 수 있게 도와주는 도구 ([minigames.yml](playingMiniGameWiki.md#minigamesyml) 참고)
### 데이터 설정하는 법
MiniGame구현 클래스에서 `registerCustomData()` 메소드 오버라이딩 후 커스텀 데이터 추가
```java
@Override
protected void registerCustomData() {
  Map<String, Object> customData = this.getCustomData();
  customData.put("health", 30);
  List<ItemStack> items = new ArrayList<>();
  items.add(new ItemStack(Material.STONE_SWORD));
  customData.put("items", items);
}
```
### 데이터 사용하는 법
커스템 데이터 사용은 `getCustomData()`로 접근해서 미니게임의 **생성자를 제외한** 어디에서나 사용 가능
```java
@SuppressWarnings("unchecked")
@Override
protected void initGameSetting() {
  // set health scale
  this.health = (int) this.getCustomData().get("health");
  // give kit tool
  List<ItemStack> items = (List<ItemStack>) this.getCustomData().get("items");
  items.forEach(item -> p.getInventory().addItem(item));
}
```
or
```java
@Override
protected void processEvent(Event event) {
    player.setHealthScale((int) this.getCustomData().get("health"));
  }
}
```


## etc
### MiniGameSetting으로
- MiniGameSetting으로 게임에 대해 다양한 설정을 줄 수 있음
- `settingFixed` 설정값제외한 값들은 minigames.yml 파일에서 수정가능
- `settingFixed`: `forceFullPlayer`, `maxPlayerCount`, `timeLimit`, `customData` 값의 고정 여부 (minigame.yml파일에서 유저의 수정이 적용안됨)
- `scoreNotifying`: 
- `foceFullPlayer`: 


### 미니게임 종료
- `endGame()` 사용



# 주의사항
## 참가 플레이어 데이터 관리
- MiniGameWorld플러그인에서는 기본적인 플레이어 데이터를 미니게임 참가/퇴장에 따라서 저장/복구 한다
> 기본 관리 데이터: `인벤토리`, `체력`, `배고픔`, `hide`, `glowing`, `모든 포션 효과`
- 미니게임에서 따로 설정값(ex. 플레이어 체력)을 바꿔서 플레이 한것이 있다면, 미니게임 종료 전(runTaskBeforeFinish()) 원래 상태로 설정값을 복구시켜야 함

## minigames.yml
- class 이름이 기준으로 등록됨
- 생성자에서 설정한 기본값으로 설정됨




