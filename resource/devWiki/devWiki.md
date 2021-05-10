# 설명
- 미니게임 메이커를 기반의 미니게임을 만들 제작자를 위한 글
- [유저 위키] 참고

# 동작원리
![MiniGameMakerDesign](../img/MiniGameMakerDesign.JPG)
- 미니게임메이커 플러그인을 기반으로 만든 미니게임 플러그인들은 서버가 시작되면 미니게임메이커에 게임이 등록되고, 관리됨 

# 환경 세팅 방법
- [Paper]
- [MiniGameMaker]
- [wbmMC] 
- 다운로드 후 build path 추가

## `plugin.yml`
- depend: `[MiniGameMaker]` 추가

# 주의사항

# 처리 이벤트 목록
※ `paper api` 기반으로 만들어졌기 때문에, paper기반 event도 사용가능(참고: [Paper API])  
※ 미니게임 이벤트는 해당 이벤트의 Player가 미니게임 플레이중인것이 확인되면 해당 미니게임 이벤트 처리 메소드로 넘어감  
※ 처리 이벤트의 하위 이벤트까지 모두 사용가능 (예. PlayerEvent의 PlayerJumpEvent)
- BlockBreakEvent
- BlockPlaceEvent
- PlayerEvent
- EntityEvent
- HangingEvent
- InventoryEvent
- InventoryMoveItemEvent
- InventoryPickupItemEvent
- PlayerLeashEntityEvent

# 코드 제작 방법
1. 미니게임 클래스 제작
```

```
2. 미니게임 메이커에 등록
```

```

# 미니게임 참여 방법 변경


# API


# minigames.json
- class 이름이 기준으로 등록됨
- 미니게임 생성자에서 설정한 기본값으로 설정됨



# 사용법 유튜브
[사용법]


# 연락
- [디스코드]

[유저 위키]: https://github.com/worldbiomusic/MiniGameMaker/blob/main/userWiki.md
[Paper]: https://papermc.io/
[MiniGameMaker]: https://github.com/worldbiomusic/MiniGameMaker/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[디스코드]: https://discord.com/invite/fJbxSy2EjA
[Paper API]: https://papermc.io/javadocs/paper/1.16/index.html?overview-summary.html
