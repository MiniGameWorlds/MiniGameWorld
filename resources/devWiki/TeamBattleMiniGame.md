# 설명
- 팀 대항 미니게임

# 기능
## 유틸 기능
### 팀 등록
#### 방법1. 팀에 플레이어를 직접 등록
1. 생성자에서 `teamCount`와 `teamSize`를 설정
2. `registerAllPlayersToTeam()`를 오버라이딩해서 각 플레이어를 팀에 가입시켜야(`registerPlayerWithTeam()`) 함
> 보스 미니게임을 예시로 들 때: 1대5로 싸우므로 팀 배분을 1대5로 지정해야 함
```java
public MoreHit() {
  super("Boss", 6, 500, 60, 2, 5); // 5명씩  2팀
  this.getSetting().setScoreNotifying(true);
  this.setGroupChat(true);
}

@Override
protected void registerAllPlayersToTeam() {
  // boss player
  Player boss = this.randomPlayer();

  List<Player> challengers = this.getPlayers();
  // remove boss from challengers
  challengers.remove(boss);

  // register boss with team
  this.registerPlayerWithTeam(boss, 0);

  // register challengers with team
  challengers.forEach(p -> this.registerPlayerWithTeam(p, 1));
}
```

#### 방법2. 팀 자동 등록 기능 사용
- 최대한 모든 팀이 같은 멤버 수를 같도록 자동 분배됨
1. 생성자에서 `teamCount`와 `teamSize`를 설정
2. 생성자에서 `this.setAutoTeamSetup(true);` 선언

### 팀 관리
- `fixTeamCount()`와 `fixTeamSize()`를 사용하여 게임 플레이 도중 팀 설정값을 변경할 수 있음
- 팀 관리 유틸 메소드
- Team 클래스를 이용한 팀 관리 메소드

### 그룹 채팅
- 채팅이 팀끼리만 보이는 기능
- 생성자에서 `this.setAutoTeamSetup(true);` 선언


# 사용법
## 예시 미니게임
- [MoreHit]()

# 주의사항
- `initGameSetting()` 오버라이딩 할 때 `super.initGameSetting()` 필수
- `autoTeamSetup`가 false일 때, `registerAllPlayersToTeam()`를 오버라이딩해서 각 플레이어를 팀에 가입시켜야(`registerPlayerWithTeam()`) 함
- `runTaskAfterStart()`: 오버라이딩 할 때 `super.runTaskAfterStart()`를 꼭 호출해야 함
- `processEvent()` 오버라이딩 할 떄 `super.processEvent()) 필수

# 개선할 것

