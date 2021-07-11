# 설명
- MiniGameEventNotifier와 함께 사용하는 옵저버 패턴 시스템


# 기능
- 옵저버로 등록하면, 미니게임에서 특정 이벤트가 발생시 옵저버들에게 알려줌


# 사용법
## 1.MiniGameObserver interface 구현 클래스 제작
```java
public class RewardManager implements MiniGameObserver {

	@Override
	public void update(MiniGameEvent event, MiniGameAccessor minigame) {
    // 미니게임이 끝날 때
		if (event == MiniGameEvent.FINISH) {
      // 1등에게 경험지 1000증정
			System.out.println(minigame.getTitle() + " Give Reward!");
      // 랭킹 데이터 가져옴
			List<Entry<Player, Integer>> rank = minigame.getScoreRanking();
			Player fir = rank.get(0).getKey();
			fir.giveExp(1000);
			fir.sendMessage("you rewarded exp");
		}
	}

}
```
## 2.MiniGameMaker를 통해 모든 미니게임에 observer 등록
```java
MiniGameMaker maker = MiniGameMaker.create();
maker.registerMiniGameObserver(new RewardManager());
```

# 주의사항
- MiniGameNotifier의 MiniGameEvent에 맞춰서 동작됨
```java
public enum MiniGameEvent {
  START, FINISH, ...
}
```

- MiniGameMaker를 통해 등록하면 모든 미니게임에 옵저버로 등록됨
- MiniGameAccessor를 받아서 class 이름과 같은것으로 미니게임 구분 가능

# 개선할 것

