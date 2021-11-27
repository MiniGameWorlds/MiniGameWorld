# Description
- Document for plugin design



# Design
<img src="operation-structure.png" width="49.5%"></img>
<img src="api-design.png" width="49.5%"></img>
![MiniGameWorld_plugin_design](inner-design.png)
- All minigames managed by `MiniGameManager` after registered
- Use YamlManager of `WbmMC` library for managing config



# Rules
- **Must** use `LinkedHashMap` instead of `HashMap` if it related with config operation to sort key order
- Minigame always processes `Event` in last (`Priority.HIGHEST`) (Use `Priority.MONITOR` for important thing)
- No Plugin can edit other MiniGame data (but inevitably, players can be accessed)
- API returns not synced **copied** instance for data security