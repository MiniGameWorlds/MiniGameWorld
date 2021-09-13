# Wrapper class API 고민 
※ API로 접근한 데이터 수정으로 실제 데이터가 수정되면 안됨(clone데이터 반환해야 함)

## MiniGameMaker
- MiniGameManager의 wrapper class
- API로 쓸 메소드들만 정리
- `joinGame`: 다른 플러그인을 이용해서 다른 입장방식을 사용하려 할 때 필요(하지만, 악용이 가능하므로 yml로 허용플러그인으로 관리할 지 고민중)
- `leaveGame`: 다른 플러그인을 이용해서 다른 퇴장방식을 사용하려 할 때 필요(하지만, 악용이 가능하므로 yml로 허용플러그인으로 관리할 지 고민중)
- `handleException`: 다른 플러그인에서 여러 예외상황을 메소드로 넘기기 위해 필요
- `isPossibleEvent`: 미니게임에서 사용가능한 이벤트인지 체크할 때 필요
- `checkPlayerIsPlayingMiniGame`: 플레이어가 미니게임 하는중인지 체크 필요
- `getPlayingGame`: 플레이어가 플레이중인 게임 체크 필요(MiniGameAccessor로 반환하기)
- `getMiniGameWithClassName`: 미니게임 반환(MiniGameAccessor로 반환하기)
- `getMiniGameList`: 미니게임 리스트 체크 필요(MiniGameAccessor로 반환하기)
- `registerMiniGame`: 미니게임 등록 필요
- `unregisterMiniGame`: 미니게임 해제 필요
- `getServerSpawn`: 서버 스폰(로비)로 필요




## MiniGameAccessor
- MiniGame의 wrapper class
- API로 쓸 메소드들만 정리
- `isEmpty`: 
- `isFull`: 
- `containsPlayer`: 
- `getPlayers`: 
- `getPlayerCount`: 
- `getScore`: 
- `handleException`: 
- `getTitle`: 
- `getLocation`: 
- `getWaitingTime`: 
- `getTimeLimit`: 
- `getMaxPlayerCount`: 
- `isActive`: 
- `isSettingFixed`: 
- `getClassName`: 
- `getLeftWaitTime`: 
- `getLeftFinishTime`: 
- `getEveryoneName`: 
- `getScoreRanking`: 



