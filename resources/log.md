# 할 것
- Guava 사용해보기
- MiniGame에서 player에게 live 변수를 추가할 까 고민중
- MiniGameWorld 용도 유튜브 브랜드 채널 만들기 (튜토리얼)
- 미니게임 추가 (몬스터 많이 죽이기, 몬스터 많이 넘기기, 블럭 땅따먹기, 블럭 더 많이 자기 색깔로 바꾸기, 점프맵, 플레이어 보스레이드, 몬스터 레이드, 몬스터 증식 시기키(죽이면 2배씩 늘어나지만, 살아남기 힘들어짐))
- party GUI 메뉴 추가하기
- 범용 Reward 플러그인 만들기
- wiki: GUI, Party
- 유튜브 마인크래프트 미니게임 만들기 강좌 만들어 보기 (제작자임을 밣히지 말고 테스트로)
- 실제로 간단한 미니게임 서버, 미니게임 여러 종류 만들면서 플러그인에 추가할것 생각해보기
- 위키 한글/영어 두버전으로 폴더 만들기 (userWiki를 먼저 제작하고, devWiki는 나중에 외국인들에게 pull request받아서 서서히 제작)
- classgraph말고 다른 class graph 사용하는 library 사용해보기 (e.g. reflections)
- classgraph로 모든 이벤트에 핸들러 추가하지 말고, 추가하는 조건에 `PlayerEvent하위 이벤트, EntityEvent하위 이벤트, 다른 모드는 이벤트중에서 getEntity() 메소드를 가진 이벤트`를 추가해서 등록하기(왜냐하면  BlockShearEntityEvent같은것이 등록이 안되있음)
- 기본 미니게임들 .jar파일로 꺼내서 외부 jar로 작업하기 (시스템 통일성을 위해), (미니게임 클래스가 많이 바뀌므로 배포 직전에 작업하기)
- setting.yml에 한글, 영어 기능 language변수 
- java docs 만들기: MiniGameWorld, MiniGameAccess, 내부 docs도 모두다 만들기
- MiniGame에 custom color 기능 만들기(title, score 등 색 표시)
- 미니게임 설정값 설정 cmd 만들기 (loc같은것 지정하기 편함) (reload()도 자동 실행)

---

# 2020-09 ~ 2020-05-09
- `Relay Escape` 서버에서 만들었던 미니게임 모듈을 따로 분리해서 개발 (이전 기록: Relay Escape 로그 참고)

# 2021-05-10
- 스코어 출력을 스코어 기준 내림차순으로 변경
- MiniGame 생성자 Location 설정없으면 기본 world, 0, 4, 0으로 설정
- 미니게임 클래스 1개당 1미니게임 생성만 가능하게 디자인
- 없는 미니게임은 minigames.json에서 삭제되는 기능 추가
- 미니게임 활성화 여부 actived 추가
- 미니게임 세팅값(maxPlayerCount, timeLimit, waitingTime)고정 여부 settingFixed 추가
- MiniGame 생성자에서 initSetting() 호출 제거
- SoloMiniGame, CooperativeMiniGame, TeamBattleMiniGame 프레임워크 추가

# 2021-05-15
- MiniGame에 scoreNotifying 기능 추가(+1, -1 표시 기능)
- MiniGame 프레임 공사
- MiniGame에서 설정값을 set메소드로 설정 기능 추가 

# 2021-05-16
- handleException 더 범용적으로 변경, Exeption enum 추가
- MiniGame attributes 값 유효성 검사 처리 추가(checkAttributes())
- 프레임 미니게임 클래스 이름 변경: SoloMiniGame, TeamMiniGame, SoloBattleMiniGame, TeamBattleMiniGame
- SoloBattleMiniGame, TeamBattleMiniGame은 플레이어/팀 이 1개만 남았을 때 게임 종료('배틀'주제 미니게임이므로 상대가 없으면 게임 종료)
- runTaskBeforeStart, runTaskBeforeEnd 추가하기
- 플레이어가 join할 때 이미 다른 미니게임에 참여중인지 검사
- 각 프레임 미니게임 인원수 검사 
- 미니게임 예외처리 프레임 클래스에서 처리(인원수 관련 게임종료)

# 2021-05-22
- MiniGame의 세팅값 관리하는 MiniGame.Setting 클래스 생성(리팩토링)

# 2021-05-23
- waitingCounter, finishCounter 카운터 추가로 남은 시간 가져올 수 있게 변경
- frame MiniGame클래스에 편리 메소드 추가(점수, 메세지)
- frame MiniGame클래스마다 점수 출력 각 클래스에 맞게 override
- 꾸미기(title 색, 소리)
- ScoreClimbing 미니게임 추가

# 2021-05-30
- BukkitTaskManager 틀 작성

# 2021-06-05
- MiniGame에 BukkitTaskManager 적용
- 미니게임 참여, 퇴장 메세지 모두에게 전송
- 예외 MiniGameMaker를 통해 처리 메소드 추가
- 미니게임 퇴장 기능 추가(시작 전에만 허용)
- 명령어로 게임 참가/퇴장 기능(setting.json에 minigameCommand 추가)
- 표지판 퇴장 기능 추가 (signJoin->minigameSign 으로 변경)
- 커맨드 추가

# 2021-06-06
- dev위키를 개발 관련 위키로 변경하기(미니게임 하위 플러그인 위키는 userWiki에 분할해서 작성)
- registerTasks() 로 task등록 편의기능 추가
- forcePlayerCount 설정 기능 추가
- RockScissorPaper 미니게임 추가(forcePlayerCount사용)

# 2021-06-12
- MiniGameManager에서 사용하는 Event만 처음에 등록하는 EventHandler에 등록 (전체:321개, 사용하는것: 177개)
- 표지판으로 미니게임 관리하는 이벤트 processEvent()에 결합

# 2021-06-13
- 구현 미니게임 클래스에서 minigames.json 파일에 설정값 등록하고 사용할 수 있게 customData 기능 추가 
- EventHandler 모든 이벤트 등록으로 다시 변경
- 미니게임 삭제 조건 수정(title로 검사하지 말고 ClassName으로 검사하기)
- PVP 미니게임 추가

# 2021-06-27
- json포맷의 double과 long구분 해결 방법 못 찾아서, 일단 숫자값은 무조건 double로 불러오기
- minigames.json 파일에서 settingFixed 설정값 안보이게 설정 (미니게임 개발자들이 사용자들에게 settingFixed를 알려줘야 함)

# 2021-07-03
- wbmMC에 YamlManager 추가
- Yaml 정리(https://github.com/worldbiomusic/Blog/blob/main/Minecraft/plugin/making/YAML.md)

# 2021-07-04
- 데이터 관리 포맷 Json -> Yaml로 변경
 
# 2021-07-08
- RankManager를 wbmMC로 옮김
- BukkitTaskManager를 wbmMC로 옮김
- BukkitTaskManager에서 runTask~() 로 task를 사용하기 시작하면, 등록한 runnable 재사용못하게 삭제 기능 추가(에러 방지)
- 위키 문서작성

# 2021-07-10
- MiniGameMaker wrapper class 작성 
- MiniGameSetting의 `actived`를 `active` 로 이름 바꿈

# 2021-07-11
- MiniGameAcessor wrapper class 작성 
- MiniGame Observer 패턴 기능 추가(외부 플러그인에서 MiniGameMaker를 통해 MiniGameObserver를 등록해서 미니게임의 Event를 감지해 여러가지(ex.보상) 작업 가능)
- Player.sendMessage() 대신 MiniGame의 sendMessage() 기능 추가(prefix로 미니게임 [title] 이 쓰여짐) 
- MathGame 미니게임 외부 플러그인으로 만들어서 MiniGameMaker, MiniGameObserver 적용 테스트
- wrapper class 문서작업
- observer 시스템 문서작업

# 2021-07-17
- MiniGameDataManager에서 file관련 데이터 load, save 기능을 MiniGameSetting에 캡슐화
- MiniGame 관련 클래스 문서 작업, 리팩토링
- RemoveBlock(TeamMiniGame) 미니게임 추가
- yml file reload 기능 추가(YamlMember 클래스에서 reload구현하고, 명령어로 등록: `minigame reload`)
- wbmMC의 BroadcastTool의 log 관련 기능 전부 삭제 (log는 각 플러그인의 getLogger()를 이용해야 함)
- Setting 클래스에 플러그인 prefix 제공하는 sendMsg() 기능 추가
- Setting 클래스에 Logger관련 기능(log(), warning()) 추가
- jardescription.jardesc 추가

# 2021-07-25
- paper api 17로 변경
- MiniGameManager의 감지이벤트에서 InventoryPickupItemEvent 제거(Player와 관련 없음)

# 2021-09-08
- classgraph 오류 해결하기(JDK 16의 오류) (classgraph 문의로 trick사용해서 해결)

# 2021-09-09
- MiniGameSetting클래스로 List<String> tutorial 변수 옮기기 (MiniGame에서 저장되야 되는 데이터는 MiniGameSetting으로 옮겨서 관리하기), (wiki에 변경사항 기록: new String[] {"tutorial"} 사용)

# 2021-09-10
- CustomData를 MiniGameSetting 내부 변수로 옮기기
- settingFixed의 관리대상에 customData도 추가하기
- 메인 클래스 이름 변경 `Main` > `MiniGameMakerMain`
- Inventory관리 기능 추가하기(PlayerInvManager)
- 미니게임 스폰위치 변수 이름 변경 `spawnLocation` > `lobby`
- MiniGame클래스에서 오버라이딩 되서 수정되면 안되는 메소드들 final로 선언하기

# 2021-09-12
- minigame list 명령어 추가
- MiniGameSetting에서 lobby 관리
- setting.yml에 message prefix 설정값 `messagePrefix`변수 추가
- MiniGameAccessor에 MiniGameSetting에 추가된것들 추가
- wiki 정리

# 2021-09-13
- 플러그인 이름 MiniGameWorld 로 변경하기
```yaml
# 플러그인 이름 변경
- 메이커 > 월드, Maker > World
# 변경할 것
- [x] github에서 모든 문서 변경
- [x] 사진 이름 변경
- [x] ppt 변경 후, 이미지 추출
- [x] 소스코드(프로젝트 이름, 클래스 이름, 패키지 이름, 기타 코드) 변경
- [x] maven 이름 변경
- [x] plugin.yml
- [x] jardesc.jardesc 변경 (추출 이름)
```

# 2021-09-14
- 외부 플러그인에서 MiniGameSetting의 lobby를 setLobby()를 이용해서 바꿀 수 있는 위험이 있으므로, MiniGameSetting안에서 lobby변수가 MiniGameManager로 접근(singleton)해서 lobby 변수 설정하고, setLobby() 제거하기
- MiniGamePlayerData 클래스로 미니게임의 플레이어 관련 데이터 관리 분리
- `경험치(xp)`, `체력`, `배고픔`, `포션 효과`, `hiding`, `glowing` 미니게임 데이터(MiniGamePlayerDataManager)에서 관리

# 2021-09-15
- GUI 만들기(/mg gui 명령어로 열기)
- 각 미니게임 icon 추가(MiniGameSetting에 gui에 사용할 Material 변수)
- MiniGameWorld에 gui창 오픈하는 메서드 추가

# 2021-09-16
- command는 setting.yml의 minigameCommand가 true일 때 만 사용 가능
- MiniGameAccessor에 MiniGame의 icon 접근 method 추가
- `scoreNotifying`, `forceFullPlayer`를 minigames.yml에서 관리 대상으로 추가
- `forceFullPlayer`를 fixedSetting의 관리 대상에 추가
- `waitingTime`을 fixedSetting의 관리 대상에서 제외
- 게임 waitingTime이 끝나고 시작할 때 `forceFullPlayer`가 true이고, 만족되지 못할 경우 게임을 리셋하지 않고, 다시 waitingTask를 재시작하면서 다른 플레이어 기다리기

# 2021-09-17
- MiniGame에서 handleException()에 Observer의 감지Event 추가
- 주석 추가, 영어로 변경 (부분)

# 2021-09-18
- 주석 추가, 영어로 변경 (부분)
- GUI: player header 추가
- command tab completer 추가

# 2021-09-19
- party 시스템 디자인, 틀 제작

# 2021-09-20
- party 시스템 제작 (테스트 필요)
- Player의 서버 join, quit 때 필수 처리할 것들을 MiniGameManager에 모아두어 처리

# 2021-09-21
- party 시스템 제작
- party 시스템에 clickable chat 기능 추가

# 2021-09-22
- 주석 영어로 변경 (완료)
- GUI에 간단한 party member 목록 추가
- MiniGame에 `minPlayerCount` 속성 추가
- `forcedFullPlayer` 속성 제거 (`minPlayerCount` = `maxPlayerCount`와 같은 효과를 가지므로)
- party와 MiniGame 결합 (동반 입장, 퇴장) (일부 파티멤버가 다른 미니게임에 참여중일 때도, 남은 파티원끼리 미니게임 참여 가능)


# 2021-09-23
- GUI 클릭 버그 고치기
- WbmMC의 `TaskManager`의 Runnable 반복 등록 제거 (task실행 객체를 BukkitRunnable > Runnable로 넣어서 반복 등록 필요 없어짐)

# 2021-09-24
- TeamBattleMiniGame의 Team 관련 기능 세분화
- TeamBattleMiniGame의 groupChat을 customData에서 관리
- TeamBattleMiniGame의 TeamRegisterMethod를 customData에서 관리

# 2021-09-25
- checkAttribute() 기능 삭제
- MiniGameSetting의 scoreNotifying을 customData 영역으로 이동
- 채팅을 MiniGame에 있는 사람들끼리만 보이게 (customData에 넣기) (각 하위 미니게임 프레임에서 processChat() 오버라이딩해서 변형 가능)
- MiniGamePlayerData에 GameMode 추가
- TeamBattleMiniGame의 Team의 player에 `live` 변수 추가

























