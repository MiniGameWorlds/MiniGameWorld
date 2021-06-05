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
