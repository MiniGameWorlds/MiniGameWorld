# 설명
`minigames.yml`파일로 MiniGame데이터를 관리하는 클래스


# 기능
- 새로운 미니게임 데이터 등록
- 미니게임에 저장되있던 미니게임 데이터 적용(변경)



# 사용법


# 주의사항
## 데이터 관련
- Yaml을 이용해서 데이터 관리
- YamlMember로서 `setData()`와 `getFileName()`을 구현함
- YamlMember의 구조는 넘겨받은 FileConfiguration을 이용해서 데이터를 수정하고, 되돌려 주지 않는 구조이므로 config를 직접 수정하면서 사용해도 되지만,
Map을 사용하면 데이터를 관리하기 쉽기 때문에, 밑의 코드처럼 먼저 데이터를 Map에 가져온다음에 config에 Map을 다시 `set()`으로 데이터를 동기화해서 관리한다
```java
@Override
public void setData(YamlManager yamlM, FileConfiguration config) {
  // sync config minigames with variable minigames
  if (config.isSet("minigames")) {
    this.minigameData = YamlHelper.ObjectToMap(config.getConfigurationSection("minigames"));
  }
  config.set("minigames", this.minigameData);
}
```
- 위의 코드처럼 가장 최상단 Map 데이터를 `YamlHelper.ObjectToMap()`으로 [YamlHelper](https://github.com/worldbiomusic/wbmMC/blob/main/src/com/wbm/plugin/util/data/yaml/YamlHelper.java)의 도움으로 변환해서 가져오면 하위(child)의 모든 Map은 단순한 
형변환으로 사용할 수 있다
- 파일 리로드하려면 YamlManager의 `reload()` 사용하면 됨


# 개선할 것











