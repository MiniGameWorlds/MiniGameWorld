# 설명
- 미니게임월드 플러그인 중심 API 클래스
- MiniGameManager의 wrapper 클래스
- singleton

# 기능
- 미니게임 참가, 퇴장
- 미니게임 등록, 해제
- 이벤트 검사
- 미니게임 객체 가져오기
- 서버스폰 위치 가져오기
- 미니게임 옵저버 등록

# 사용법
## MiniGameWorld의 `create()`를 사용해서 객체를 생성해서 사용
```java
// create MiniGameWorld instance
MiniGameWorld minigameWorld = MiniGameWorld.create();
// register MiniGame
minigameWorld.registerMiniGame(new Game());
// register MiniGameObserver
minigameWorld.registerMiniGaameObserver(new RewardManager());
```
## 미니게임 참가, 퇴장 기능 활용
`joinGame()`, `leaveGame()`을 사용해서 미니게임 참가를 다른 방법으로 가능하게 할 수 있음
```java
// 예. 인벤토리 창에서 클릭한 아이템 이름의 미니게임 참가
@EventHandler
public void onPlayerJoinMiniGame(InventoryClickEvent e) {
  Player p = (Player) e.getWhoClicked();
  ItemStack item = e.getCurrentItem();
  String minigameTitle = item.getItemMeta().getDisplayName();
  MiniGameWorld minigameWorld = MiniGameWorld.create();
  minigameWorld.joinGame(p, minigameTitle);
}
```

# 주의사항
- 반드시 `MiniGameWorld.create()`로 객체를 생성해야 함
- `setMiniGameManager()`는 MiniGameWorld플러그인에서 처음 1회 MiniGameManager객체를 MiniGameWorld 클래스에 등록할 때 한번 사용됨

# 개선할 것
- API 문서 작성
