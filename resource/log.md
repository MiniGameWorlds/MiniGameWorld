# 2021/5/10
- 스코어 출력을 스코어 기준 내림차순으로 변경
- MiniGame 생성자 Location 설정없으면 기본 world, 0, 4, 0으로 설정
- 미니게임 클래스 1개당 1미니게임 생성만 가능하게 디자인
- 없는 미니게임은 minigames.json에서 삭제되는 기능 추가
- 미니게임 활성화 여부 actived 추가
- 미니게임 세팅값(maxPlayerCount, timeLimit, waitingTime)고정 여부 settingFixed 추가
- MiniGame 생성자에서 initSetting() 호출 제거
- SoloMiniGame, CooperativeMiniGame, TeamBattleMiniGame 프레임워크 추가

# 2021/5/15
- MiniGame에 scoreNotifying 기능 추가(+1, -1 표시 기능)
- MiniGame 프레임 공사
- MiniGame에서 설정값을 set메소드로 설정 기능 추가 

# 2021/5/16
- handleException 더 범용적으로 변경, Exeption enum 추가
- MiniGame attributes 값 유효성 검사 처리 추가(checkAttributes())
- 프레임 미니게임 클래스 이름 변경: SoloMiniGame, TeamMiniGame, SoloBattleMiniGame, TeamBattleMiniGame
- SoloBattleMiniGame, TeamBattleMiniGame은 플레이어/팀 이 1개만 남았을 때 게임 종료('배틀'주제 미니게임이므로 상대가 없으면 게임 종료)
- runTaskBeforeStart, runTaskBeforeEnd 추가하기
- 플레이어가 join할 때 이미 다른 미니게임에 참여중인지 검사
- 각 프레임 미니게임 인원수 검사 
- 미니게임 예외처리 프레임 클래스에서 처리(인원수 관련 게임종료)

# 2021/5/22
- MiniGame의 세팅값 관리하는 MiniGame.Setting 클래스 생성(리팩토링)

# 2021/5/23
- waitingCounter, finishCounter 카운터 추가로 남은 시간 가져올 수 있게 변경
- frame MiniGame클래스에 편리 메소드 추가(점수, 메세지)
- frame MiniGame클래스마다 점수 출력 각 클래스에 맞게 override
- 꾸미기(title 색, 소리)
- ScoreClimbing 미니게임 추가

# 2021/5/30
- BukkitTaskManager 틀 작성

# 2021/6/5
- MiniGame에 BukkitTaskManager 적용
- 미니게임 참여, 퇴장 메세지 모두에게 전송
- 예외 MiniGameMaker를 통해 처리 메소드 추가
- 미니게임 퇴장 기능 추가(시작 전에만 허용)
- 명령어로 게임 참가/퇴장 기능(setting.json에 minigameCommand 추가)
- 표지판 퇴장 기능 추가 (signJoin->minigameSign 으로 변경)
- 커맨드 추가

# 2021/6/6
- dev위키를 개발 관련 위키로 변경하기(미니게임 하위 플러그인 위키는 userWiki에 분할해서 작성)
- registerTasks() 로 task등록 편의기능 추가
- forcePlayerCount 설정 기능 추가
- RockScissorPaper 미니게임 추가(forcePlayerCount사용)

# 2021/6/12
- MiniGameManager에서 사용하는 Event만 처음에 등록하는 EventHandler에 등록 (전체:321개, 사용하는것: 177개)
- 표지판으로 미니게임 관리하는 이벤트 processEvent()에 결합

# 2021/6/13
- 구현 미니게임 클래스에서 minigames.json 파일에 설정값 등록하고 사용할 수 있게 customData 기능 추가 
- EventHandler 모든 이벤트 등록으로 다시 변경
- 미니게임 삭제 조건 수정(title로 검사하지 말고 ClassName으로 검사하기)
- PVP 미니게임 추가

# 2021/6/27
- json포맷의 double과 long구분 해결 방법 못 찾아서, 일단 숫자값은 무조건 double로 불러오기
- minigames.json 파일에서 settingFixed 설정값 안보이게 설정 (미니게임 개발자들이 사용자들에게 settingFixed를 알려줘야 함)

# 2021/7/3
- wbmMC에 YamlManager 추가
- Yaml 정리(https://github.com/worldbiomusic/Blog/blob/main/Minecraft/plugin/making/YAML.md)

# 2021/7/4
- 데이터 관리 포맷 Json -> Yaml로 변경
 
# 2021/7/8
- RankManager를 wbmMC로 옮김
- BukkitTaskManager를 wbmMC로 옮김
- BukkitTaskManager에서 runTask~() 로 task를 사용하기 시작하면, 등록한 runnable 재사용못하게 삭제 기능 추가(에러 방지)
- 위키 문서작성

# 2021/7/10
- MiniGameMaker wrapper class 작성 
- MiniGameSetting의 `actived`를 `active` 로 이름 바꿈

# 2021/7/11
- MiniGameAcessor wrapper class 작성 
- MiniGame Observer 패턴 기능 추가(외부 플러그인에서 MiniGameMaker를 통해 MiniGameObserver를 등록해서 미니게임의 Event를 감지해 여러가지(ex.보상) 작업 가능)
- Player.sendMessage() 대신 MiniGame의 sendMessage() 기능 추가(prefix로 미니게임 [title] 이 쓰여짐) 
- MathGame 미니게임 외부 플러그인으로 만들어서 MiniGameMaker, MiniGameObserver 적용 테스트
- wrapper class 문서작업
- observer 시스템 문서작업

# 2021/7/17
- MiniGameDataManager에서 file관련 데이터 load, save 기능을 MiniGameSetting에 캡슐화
- MiniGame 관련 클래스 문서 작업, 리팩토링
- RemoveBlock(TeamMiniGame) 미니게임 추가
- yml file reload 기능 추가(YamlMember 클래스에서 reload구현하고, 명령어로 등록: `minigame reload`)
- wbmMC의 BroadcastTool의 log 관련 기능 전부 삭제 (log는 각 플러그인의 getLogger()를 이용해야 함)
- Setting 클래스에 플러그인 prefix 제공하는 sendMsg() 기능 추가
- Setting 클래스에 Logger관련 기능(log(), warning()) 추가
- jardescription.jardesc 추가
- 
