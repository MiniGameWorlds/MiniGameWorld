# 설명
- MiniGameMaker 기반의 미니게임 플러그인 제작에 관한 글
- MiniGameMaker 사용법은 [사용 위키] 참고
- MiniGame의 내부 구조, 동작은 [개발 위키] 참고



# API 구조
<!-- <img src="api-design.png" width="49.5%"></img> -->
![](api-design.png)
- 미니게임메이커 플러그인을 기반으로 만든 미니게임 플러그인들과 3rd party 플러그인은 API(MiniGameMaker)로 미니게임메이커와 연결됨
## API class
- `MiniGameMaker`: `MiniGameMaker.create()`로 객체를 생성해서 미니게임을 등록, MiniGameMaker 플러그인에 대한 정보를 얻을 수 있음
- `MiniGameAccessor`: MiniGameMaker에 등록된 미니게임에 대한 정보를 얻어 활용할 수 있음



# 환경 세팅 방법
- [Paper]: 마인크래프트 버킷
- [MiniGameMaker]: 미니게임 메이커 플러그인
- [wbmMC]: 마인크래프트 플러그인 개발시 여러 기능을 작성해놓은 도와주는 라이브러리
- 다운로드 후 build path 추가
- `plugin.yml`: depend에 `[MiniGameMaker]` 추가



# 제작
## 미니게임 제작
- [making-minigame-guide](making-minigame-guide.md)

## 3rd party 플러그인 제작
- [making-3rd-party-guide](making-3rd-party-guide.md)



# 유튜브 가이드
- [가이드]



# 연락
- [디스코드]



[사용 위키]: playingMiniGameWiki.md
[개발 위키]: ../devWiki/home.md
[Paper]: https://papermc.io/
[MiniGameMaker]: https://github.com/worldbiomusic/MiniGameMaker/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[디스코드]: https://discord.com/invite/fJbxSy2EjA
[Paper API]: https://papermc.io/javadocs/paper/1.16/index.html?overview-summary.html
[BukkitRunnable 참고]: https://www.spigotmc.org/threads/prevent-already-scheduled-as-xxx-error.202486/#post-2103877
