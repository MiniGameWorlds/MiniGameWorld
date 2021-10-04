# Description
- Minecraft minigame framework that can be used in various type of server

# Development environment
- Window 10
- Eclipse 2021-03
- Git / Github
- JDK 16

# Library dependencies
- [Paper]: Lastest version
- [wbmMC]: Minecraft util library
- [classgraph]: Using for register all `Event` handlers

# How to build
## 1. Maven
- Download `shade` plugin
- Run `mvn package` (uses `shade` plugin)
### Paper
- Add repository, dependency
```xml
<!-- repository -->
<repository>
    <id>papermc</id>
    <url>https://papermc.io/repo/repository/maven-public/</url>
</repository>

<!-- dependency -->
<dependency>
    <groupId>com.destroystokyo.paper</groupId>
    <artifactId>paper-api</artifactId>
    <version> "version" </version>
    <scope>provided</scope>
</dependency>
```

### wbmMC
- Add build path in eclipse 
- Put in `plugins` directory

### classgraph
- Add dependency
```xml
<dependency>
  <groupId>io.github.classgraph</groupId>
  <artifactId>classgraph</artifactId>
  <version>LATEST</version>
</dependency>
```


## 2. MANIFEST.MF
- Use `MANIFEST.MF` for add library class path
```mf
Manifest-Version: 1.0
Class-Path: . libs/classgraph.jar

```
- Build jar with `MANIFEST.MF` setting

### Paper
- Add build path

### WbmMC
- Add build path in eclipse 
- Put in `plugins` directory

### classgraph
- Add build path in eclipse
- Put in `plugins/libs/classgraph.jar`


## 3. Fat-Jar
- Put libraries to one jar
- Download `fat-jar` plugin
- Export to `fat-jar` plugin with `classgraph`

### Paper
- Add build path

### WbmMC
- Add build path in eclipse 
- Put in `plugins` directory

### classgraph
- Add build path in eclipse



# Development order
## Maven
1. Develop
2. Build with `shade` plugin (`mvn package`) in command
3. Copy `MiniGameWorld-x.x.x-SNAPSHOT-shaded.jar` to `pluins` from `target`
4. Start server

## MANIFEST.MF
1. Develop
2. Export with `MANIFEST.MF`
3. Start server

## Fat-Jar
1. Develop
2. Export to `fat-jar` plugin with `classgraph`
3. Start server


[Paper]: https://papermc.io/
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[classgraph]: https://github.com/classgraph/classgraph
