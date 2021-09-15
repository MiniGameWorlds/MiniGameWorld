# 설명
- [MiniGameEventNotifier](MiniGameEventNotifier.md)와 함께 사용하는 옵저버 패턴 시스템중의 `Observer`역할


# 기능
- 옵저버를 등록해놓으면, 미니게임에서 특정 이벤트가 발생시 옵저버들에게 알려줌


# 사용법
## 1.MiniGameObserver interface 구현 클래스 제작
```java
public class RewardManager implements MiniGameObserver {
	// 보상 매니저
	@Override
	public void update(MiniGameEvent event, MiniGameAccessor minigame) {
    		// "PVP" 미니게임이 끝날 때 1등에게 경험지 1000증정
		if (minigame.getClassName().equals("PVP") && minigame.getevent == MiniGameEvent.FINISH) {
			// 랭킹 데이터 가져옴
			List<Entry<Player, Integer>> rank = minigame.getScoreRanking();
			Player fir = rank.get(0).getKey();
			fir.giveExp(1000);
			fir.sendMessage("you rewarded exp");
		}
	}

}
```
## 2.MiniGameWorld를 통해 모든 미니게임에 observer 등록
```java
MiniGameWorld minigameWorld = MiniGameWorld.create();
minigameWorld.registerMiniGameObserver(new RewardManager());
```

# 주의사항
- MiniGameNotifier의 각 상황에 맞는 MiniGameEvent일 때 동작함(옵저버에게 알림)
```java
public enum MiniGameEvent {
  START, FINISH, ...
}
```

- MiniGameWorld를 통해 등록하면 모든 미니게임에 대한 이벤트를 감지함
- MiniGameAccessor를 받아서 class 이름과 같은것으로 미니게임 구분 가능

# 개선할 것
