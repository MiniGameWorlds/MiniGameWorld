# MiniGameMaker 메모

## 코드 수정할 것
> - sysout를 Logger 로 변경


## minigames.json에 추가할 것
> - MiniGame의 maxPlayerCount, timeLimit, waitingTime 값 변경 금지 설정 값 (항상 기본 세팅 값으로 플레이 되어야 할 경우) -> 변수 이름: settingFixed(초기값: false), 파일 data는 저장 x
> - MiniGame 마다 활성화 여부 값 -> 파일 데이터로 저장 o, 변수이름: actived(초기값: true)

## settings.json에 추가할 것

## 명령어
- 미니게임 세팅값 수정 명령어
- setting.json 값 수정 명령어

# 추가할 기능
- task 관리 기능
- 일반 사용법 위키 추가
- 개발 사용법 위키 추가
- 오류 캐쳐 기능 만들기(setting.json, minigames.json 오류 검사 기능)
- 솔로, 협동, 개인배틀, 팀배틀 abstract 클래스 만들기