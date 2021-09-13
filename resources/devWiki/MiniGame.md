# 설명
- 모든 미니게임의 기반 클래스

# 기능
- 전체적인 미니게임 관리 기능
> 입장, 퇴장, 시작, 종료, 타이머, 랭킹, 이벤트, task, customData, observer 기능, 미니게임 세팅값 등
- MiniGameEventNotifier 인터페이스를 구현하는 observer패턴의 `subject`용도이므로 MiniGameEventNotifier의 MiniGameEvent에 맞는 상황일 때 
MiniGame클래스에서 `notifyObservers(MiniGameEvent)`를 호출해야 함


# 생명 주기
1. 플레이어 입장, 미니게임 초기화
2. 게임 시작
3. 게임 진행
4. 게임 종료, 미니게임 초기화
5. 플레이어 입장 대기(다시 1번)

# 사용법
- 유저위키 참고

# 보상
- 보상으로 지급될 수 있는것의 종류가 너무 많기 때문에 따로 MiniGameEventObserver를 구현해서 MiniGameEvent의 `FINISH`를 사용해 보상을 줘야 함

# 주의사항
- **되도록** 미니게임을 만들 때 성격에 맞게 `MiniGame`클래스가 아닌 `SoloMiniGame`, `SoloBattleMiniGame`, `TeamMiniGame`, `TeamBattleMiniGame`
클래스중 1개를 상속해서 만들어야 함
- 새로운 미니게임의 성격의 기반이 필요하거나, 세팅을 바꿔야 할 때는 `MiniGame`클래스를 상속해서 만들어도 됨
- 여러 기능이 복합적으로 같이 동작하므로 메소드간의 순서가 매우 중요함
- 외부 플러그인에서 사용될 때는 MiniGame클래스를 통해 수정할 수 있는 위험한 것들이 많으므로, [MiniGameAccessor](MiniGameAccessor.md)(API용도)를 통해 참고되어야 함

# 개선할 것
- 클래스 안에 단순히 변수와 메소드로 이루어진 여러 기능들을 클래스로 만들어서 분할하기(캡슐화)
