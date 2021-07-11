# 설명
미니게임의 대부분을 관리하는 클래스

# 기능
- Singleton pattern 사용
- 서버 스폰(로비) 위치
- 미니게임 참가, 퇴장
- 미니게임 이벤트 넘기기
- 미니게임 등록, 제거
- MiniGameMaker 프레임워크의 전체 세팅값(setting.yml)

# 사용법
- `getPlayersFromEvent()`: 정해진 이벤트로부터 Player를 추출하는 작업

# 주의사항
## 데이터 관련
- Yaml을 이용해서 데이터 관리
- YamlMember로서 `setData()`와 `getFileName()`을 구현함
- YamlMember의 구조는 넘겨받은 FileConfiguration을 이용해서 데이터를 수정하고, 되돌려 주지 않는 구조이므로 config를 직접 수정하면서 사용해도 되지만,
Map을 사용하면 데이터를 관리하기 쉽기 때문에, 밑의 코드처럼 먼저 데이터를 Map에 가져온다음에 config에 Map을 다시 `set()`으로 데이터를 동기화해서 관리한다
```java
	@Override
	public void setData(YamlManager yamlM, FileConfiguration config) {
		// sync config setting with variable setting
		if (config.isSet("setting")) {
			this.setting = YamlHelper.ObjectToMap(config.getConfigurationSection("setting"));
		}
		config.set("setting", this.setting);

		// check setting has basic values
		this.initSettingData();
	}
```
- 위의 코드처럼 가장 최상단 Map 데이터(this.setting)를 `YamlHelper.ObjectToMap()`으로 [YamlHelper](https://github.com/worldbiomusic/wbmMC/blob/main/src/com/wbm/plugin/util/data/yaml/YamlHelper.java)의 도움으로 변환해서 가져오면 하위(child)의 모든 Map은 단순한 
형변환으로 사용할 수 있다
- 마지막에 `this.initSettingData()`을 하는 이유: 기본적인 세팅값이 없어졌을 때 자동 복구 기능
- 파일 리로드하려면 YamlManager의 `reload()` 사용하면 됨

# 개선할 것
- 접근하면 위험한 것들이 많아서 wrapper class(MiniGameMaker) 제작 예정