# 미니게임 메이커
- 미니게임 프레임워크

# 필요한 라이브러리
- [wbmMC](https://github.com/worldbiomusic/wbmMC)

# 작동 원리
- 미니게임 플러그인을 만들어서미니게임 메이커 플러그인을 API로서 사용하면서 작동

# 미니게임 제작 주의사항
- 미니게임은 MiniGame 클래스를 상속해서 만든다
- 미니게임에서 변수 세팅은(예. 시간 재는 task) MiniGame 클래스의 initGameSetting() 메소드를 오버라이드해서 세팅해야 한다.
- 플레이어 예외처리는 미니게임 handleGameException() 에서 후속 처리 가능
- 게임내 버그, 예외처리는 구현하는 미니게임에서 담당해서 처리해야 함
- 이벤트 처리 조건은 단순히 미니게임에 참여중인 플레이어만 확인하므로, 미니게임 환경을 신경써서 잘 조성해야 함
> 예시: 돌과 모래를 번갈아 바뀌며 부수는 미니게임이면 건축요소에 왠만하면 돌과 모래를 사용하지 않는다
- PVP관련에서 플레이어가 죽게 놯두지 말고, 체력이 0이하로 떨어졌을 떄 죽음으로 판정한다
- 게임세팅값들은 config값으로 수정 가능 (각 서버마다 다른 환경값을 사용해야 하기 때문) (json 파일 사용해서 관리)


# 사용가능한 이벤트 목록
- PlayerInteractEvent
- BlockBreakEvent
- BlockPlaceEvent
- EntityDamageEvent(EntityDamageByEntityEvent포함, EntityDamageByBlockEvent 포함)

# 추가 예정인 이벤트 목록
- Player 관련 이벤트 (미니게임 Player 검사O)
- Entity 관련 이벤트 (미니게임 Player 검사X) (ex.화살 데미지 받은 엔티티의 화살 쏜 플레이어가 필요할 수도 있음)
- Inventory 관련 이벤트 (미니게임 Player 검사O)
- Command 관련 이벤트 (미니게임 Player 검사O)
- Block의 하위 특정 이벤트 (미니게임 Player 검사O)
- API 보면서 더 추가 예정...

# 미니게임 종류 예시 세팅 방법
1. 솔로
> - 인원: 1
> - 점수: 개인마다 부여
2. 협동
> - 인원: 2명 이상
> - 점수: 모든 플레이어에게 동일하게 부여
3. 배틀
> - 인원: 2명 이상
> - 점수: 개인마다 부여
4. 팀전
> - 인원: 2명 이상 (각 팀 변수에 플레이어 추가해서 사용)
> - 점수: 팀마다 부여

※ 솔로, 협동, 배틀, 팀전 미니게임 클래스 제작 예정

# 미니게임 종류 예시
1. 블럭 부수기
2. 블럭 설치하기
3. 개인 PVP
4. 팀 PVP
5. 점프맵
6. 밀어서 떨어뜨리기
7. 순발력
8. 활 맟추기

# Config
## `setting.json`
```
{
  "spawnLocation": {
    "world": "world",
    "x": 0.0,
    "y": 4.0,
    "z": 0.0,
    "pitch": 90.0,
    "yaw": 0.0
  },
  "signJoin": true
}
```
- spawnLocation: 게임이 끝나고 서버의 스폰으로 돌아가는 위치
- signJoin: 기본세팅인 참나무 표지판으로 미니게임을 참여할 수 있는 여부 (true / false)

## `minigames.json`
```
{
  "FitTool": {
    "timeLimit": 10.0,
    "maxPlayerCount": 1.0,
    "waitingTime": 3.0,
    "location": {
      "world": "world",
      "x": 10.0,
      "y": 4.0,
      "z": 0.0,
      "pitch": 0.0,
      "yaw": 0.0
    },
    "title": "FitTool"
  }
}
```
- 플러그인으로 넣은 미니게임이 자동으로 config에 등록됨 (없는 미니게임은 삭제됨)
- 미니게임에 맞게 값이 설정되어 있음 (기본값 변경시 작동 안할 가능성 있음)
- title: 게임 제목
- location: 입장 위치
- waitingTime: 대기시간
- maxPlayerCount: 최대 입장 인원수
- timeLimit: 플레이 제한 시간
