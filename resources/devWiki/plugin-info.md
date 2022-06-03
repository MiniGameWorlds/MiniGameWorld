# Description
- Minecraft minigame framework that can be used in various type of server

# Development environment
- Window 10
- Eclipse
- Git / Github
- JDK 16

# Library dependencies
- [Spigot]: Minecraft server software
- [wbmMC]: Minecraft util library
- [classgraph]: Using for register all `Event` handlers

# How to build
## With Maven
- Download `shade` plugin
- Run `mvn package` (uses `shade` plugin) using console in the project directory 

# pom.xml
- Depends `spigot-api`, `classgraph` and `github-api`
- Use `maven-shade-plugin`

### wbmMC
- Add build path in eclipse 
- Put in `plugins` directory

---

## With MANIFEST.MF
- Use `MANIFEST.MF` for add library class path
```mf
Manifest-Version: 1.0
Class-Path: . libs/classgraph.jar

```
- Build jar with `MANIFEST.MF` setting

### Spigot
- Add build path

### WbmMC
- Add build path in eclipse 
- Put in `plugins` directory

### classgraph
- Add build path in eclipse
- Put in `plugins/libs/classgraph.jar`

---

## With Fat-Jar
- Put libraries to one jar
- Download `fat-jar` plugin
- Export to `fat-jar` plugin with `classgraph`

### Spigot
- Add build path

### WbmMC
- Add build path in eclipse 
- Put in `plugins` directory

### classgraph
- Add build path in eclipse

---

# Development order
## With Maven
1. Develop
2. Build with `shade` plugin (`mvn package`) in command
3. Copy `MiniGameWorld-x.x.x-SNAPSHOT-shaded.jar` to `pluins` from `target`
4. Start server

## With MANIFEST.MF
1. Develop
2. Export with `MANIFEST.MF`
3. Start server

## With Fat-Jar
1. Develop
2. Export to `fat-jar` plugin with only `classgraph`
3. Start server



# **Must check before release**
- Increase `minor` version(`#.x.#`), if api changed in `plugin.yml`
- Increase `patch` version(`#.#.x`), if api not changed in `plugin.yml`
- Test all compatible versions (can compile test with changing `spigot-x.x.x.jar` patch path)
- Regenerate `javadoc`
- Change release file name with version like `MiniGameWorld-x.x.x.jar`


[Spigot]: https://getbukkit.org/download/spigot
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[classgraph]: https://github.com/classgraph/classgraph
