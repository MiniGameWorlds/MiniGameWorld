# 설명
- MiniGameObserver와 함께 사용하는 옵저버 패턴 시스템의 `Subject` 역할
- MiniGame class에 이미 구현되어 있는 있으므로 3rd party에서 관리할 필요 없음

# 기능
- observer 관리 메소드(`registerObserver()`, `unregisterObserver()`)
- 특정 순간에 해당 이벤트로 모든 옵저버에게 알리는 메소드(`notifyObserver()`)

# 사용법
```java
public abstract class MiniGame implements MiniGameEventNotifier {
	// observer list
	private List<MiniGameObserver> observerList;
  
	@Override
	public void registerObserver(MiniGameObserver observer) {
		if (!this.observerList.contains(observer)) {
			this.observerList.add(observer);
		}
	}

	@Override
	public void unregisterObserver(MiniGameObserver observer) {
		this.observerList.remove(observer);
	}

	@Override
	public void notifyObservers(MiniGameEvent event) {
		this.observerList.forEach(obs -> obs.update(event, new MiniGameAccessor(this)));
	}

}
```

# 주의사항
- MiniGameMaker를 통해서 MiniGameObserver를 등록하는것은 모든 미니게임에 대한 옵저버로서 등록되는 것임

# 개선할 것
- MiniGameEvent 종류 다양화
