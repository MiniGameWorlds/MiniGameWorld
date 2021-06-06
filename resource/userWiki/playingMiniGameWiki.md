# 설명
- 서버에서 미니게임을 적용해서 플레이하기 위한 사람들을 대상으로 작성된 글
- 기본 미니게임들이 포함되어 있음

# 다운로드
- [미니게임 메이커]
- [wbmMC]

# 적용법
1. [미니게임 메이커]와 [wbmMC] 플러그인을 다운
2. 추가로 사용할 미니게임 플러그인 다운
3. 서버 plugins 폴더에 적용
4. 서버 실행 후 종료 후에 plugins 폴더에 생기는 MiniGameMaker 폴더로 이동
5. 미니게임 메이커에 대한 설정파일: `setting.json` 값 수정 또는
6. 추가된 미니게임의 속성값대 대한 설정파일: `minigames.json` 값 수정
7. 다시 실행

# 사용법
- 미니게임을 위한 장소는 직접 만들고, 위치를 지정해서 사용해야 함

# 플레이 
## 미니게임 참가
### 1. 표지판
```
┌────────────┐
│ [MiniGame] │
│  FitTool   │
│            │
│            │
└─────┬──────┘
      │
      │
```
- setting.json의 minigameSign이 true로 설정되야 함

### 2. 명령어
- `/minigame join <title>`
- setting.json의 minigameCommand가 true로 설정되야 함

## 미니게임 퇴장
### 1. 표지판
```

┌──────────────────┐
│ [Leave MiniGame] │
│                  │
│                  │
│                  │
└────────┬─────────┘
         │
         │

```
- setting.json의 minigameCommand이 true로 설정되야 함

### 2. 명령어
- `/minigame leave`
- setting.json의 minigameCommand가 true로 설정되야 함

# 미니게임 종류 예시
- 솔로
- 협동
- 개인 배틀
- 팀 배틀
- 등등...

# 사용법 유튜브
[사용법]

# 설정 파일
## `setting.json`
- 미니게임 메이커 세팅 파일
```json
{
  "spawnLocation": {
    "world": "world(월드 폴더 이름)",
    "x": 0.0,
    "y": 4.0,
    "z": 0.0,
    "pitch": 90.0,
    "yaw": 0.0
  },
  "minigameSign": true,
  "minigameCommand": true
}
```
- spawnLocation: 게임이 끝나고 서버의 스폰으로 돌아가는 위치
- minigameSign: 참나무 표지판 우클릭으로 미니게임을 참가/퇴장 수 있는 여부 (true / false)
- minigameCommand: 명령어로 미니게임 참가/퇴장 (true / false)


## `minigames.json`
- 미니게임 설정 파일(예시)
```json
{
  "FitTool": {
    "timeLimit": 30.0,
    "maxPlayerCount": 1.0,
    "waitingTime": 10.0,
    "location": {
      "world": "world",
      "x": 0.0,
      "y": 4.0,
      "z": 0.0,
      "pitch": 0.0,
      "yaw": 0.0
    },
    "title": "도구게임",
    "settingFixed": false,
    "actived": true
  }
}
```
- title: 게임 제목
- location: 입장 스폰 위치
- waitingTime: 대기시간
- maxPlayerCount: 최대 인원수
- timeLimit: 플레이 제한 시간
- actived: 게임 활성화 여부 (true/false)
- settingFixed: `waitingTime`, `maxPlayerCount`, `timeLimit` 값 고정 여부 (=minigame.json파일에서 임의로 수정 불가능)
- 플러그인으로 넣은 미니게임이 자동으로 `minigames.json`에 등록됨 
- 없는 미니게임은 서버가 종료된 후 `minigames.json`에서 삭제됨
- 각 미니게임에 맞게 기본값이 설정되어 있음 (각 미닉임의 기본값 함부로 변경시 작동 안할 수 있음)




# 연락
- [디스코드]

[미니게임 메이커]: https://github.com/worldbiomusic/MiniGameMaker/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[디스코드]: https://discord.com/invite/fJbxSy2EjA
