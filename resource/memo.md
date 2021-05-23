# MiniGameMaker 메모

## 코드 수정할 것
> - sysout를 Logger 로 변경


## minigames.json에 추가할 것
> - MiniGame의 maxPlayerCount, timeLimit, waitingTime 값 변경 금지 설정 값 (항상 기본 세팅 값으로 플레이 되어야 할 경우) -> 변수 이름: settingFixed(초기값: false), 파일 data는 저장 x
> - MiniGame 마다 활성화 여부 값 -> 파일 데이터로 저장 o, 변수이름: actived(초기값: true)

- fitPlayerCount변수(true/false) 추가: 인원수가 maxPlayer와 동일해야 진행 가능한 게임인 경우, 인원수가 maxPlayer와 맞지 않으면 게임 시작후 바로 종료(endGame()), 중간에 플레이어 나가면 handleException메소드에서 endGame()

## settings.json에 추가할 것

## 명령어
- 미니게임 세팅값 수정 명령어
- setting.json 값 수정 명령어

## 테스트 할 것
> - 미니게임 유형 프레임 클래스

# 추가할 기능
- task 관리 기능
- 일반 사용법 위키 추가
- 개발 사용법 위키 추가
- 오류 캐쳐 기능 만들기(setting.json, minigames.json 오류 검사 기능)
> - 솔로, 협동, 개인배틀, 팀배틀 abstract 클래스 만들기(프레임 미니게임 클래스)
- 미니게임 프레임 각각 테스트
> - runTaskBeforeStart, runTaskBeforeEnd 추가하기
> - 플레이어가 join할 때 이미 다른 미니게임에 참여중인지 검사
- MiniGame 변수들 클래스로 리팩토링
- setting.json에 한글, 영어 기능 language변수 
- setting.json에 message prefix 설정값 messagePrefix변수
> - 각 프레임 미니게임 인원수 검사 
> - 미니게임 예외처리 프레임 클래스에서 처리(인원수 관련 게임종료)
- api 접근제어자 설정(생각 많이, 경험도 많이)