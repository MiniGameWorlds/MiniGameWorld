# 설명
- MiniGame wrapper 클래스(민감한 정보 조작 막음)
- MiniGame을 생성자로 넣어서 wrapper로서 사용

# 기능
- 미니게임 세팅값 정보
- 미니게임 현재 상태 정보
- 스코어 정보
- 참여 플레이어 정보

# 사용법
## 특정 미니게임 가져오기
- `title`로 접근 가능
```java
MiniGameWorld minigameWorld = MiniGameWorld.create();
MiniGameAccessor minigame = minigameWorld.getMiniGameWithClassName("FitTool");
```
## 랭킹 확인
```java
List<Entry<Player, Integer>> rank = minigame.getScoreRanking();
// 1st
Player firstPlayer = rank.get(0).getKey();
firstPlayer.sendMessage("You won the game");
```



# 주의사항


# 개선할 것
- API문서
