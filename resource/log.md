# 20210510
- 스코어 출력을 스코어 기준 내림차순으로 변경
- MiniGame 생성자 Location 설정없으면 기본 world, 0, 4, 0으로 설정
- 미니게임 클래스 1개당 1미니게임 생성만 가능하게 디자인
- 없는 미니게임은 minigames.json에서 삭제되는 기능 추가
- 미니게임 활성화 여부 actived 추가
- 미니게임 세팅값(maxPlayerCount, timeLimit, waitingTime)고정 여부 settingFixed 추가
- MiniGame 생성자에서 initSetting() 호출 제거
- SoloMiniGame, CooperativeMiniGame, TeamMiniGame 프레임워크 추가