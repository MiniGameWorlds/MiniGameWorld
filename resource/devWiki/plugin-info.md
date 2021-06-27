# 설명
- 마인크래프트의 다양한 서버환경에서 사용할 수 있는 독립적인 마인크래프트 미니게임 프레임워크
- 
- Json를 이용하여 데이터 관리함

# 주의
- 버킷 reload 대신 stop 이용

# 파일 구성


# 개발 환경
- Window 10
- Eclipse 2020-03
- Git / Github
- JDK 8

# 사용 라이브러리
- [Paper]: 1.16.5 기준
- [wbmMC]: worldbiomusic 마인크래프트 라이브러리
- [classgraph]: 마인크래프트의 모든 이벤트 핸들러 등록할 떄 사용됨

※ wbmMC는 plugins디렉토리에 넣어놓고, eclipse의 build path추가로 사용  
※ plugin.yml파일에 `depend: [wbmMC]` 추가해야 함  
※ Maven으로 관리중인 lib(Paper, classgraph)은 따로 Jar 파일을 다운로드 받아서 MANIFEST.MD 파일의 Class-Path설정으로 외부lib을 직접참조로 빌드해도 됨  
※ Maven의 shade 플러그인 사용해서 빌드 (명령어: `mvn package`)  

# Maven
## `classgraph`
```xml
<dependency>
  <groupId>io.github.classgraph</groupId>
  <artifactId>classgraph</artifactId>
  <version>LATEST</version>
</dependency>
```

## `paper`
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
    <version>1.16.5-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

# 개발 순서
1. 코드 작성
2. shade플러그인으로 메이븐 빌드 (명령어 `mvn package`)
3. target 디렉토리의 `MiniGameMaker-x.x.x-SNAPSHOT-shaded.jar` 플러그인 복사
4. 서버 버킷 디렉토리의 plugins 디렉토리에 붙여넣기
5. 서버 버킷 시작

# wbmMC 개발 순서
1. wbmMC 소스코드 수정
2. 특정 폴더(마인크래프트 라이브러리 or 자바 라이브러리)에 jar 파일 추출
3. MiniGameMaker프로젝트를 F5 연타해서 새로고침하거나, `Project > Clean > MiniGameMaker` 로 업데이트하기 (참조중인 라이브러리가 업데이트 됨)
4. wbmMC 사용하는 서버가 있다면 plugins폴더에 새로 추출한 jar 파일 붙여넣기


# 커뮤니티
- [Discord]



[Paper]: https://papermc.io/
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[classgraph]: https://github.com/classgraph/classgraph
[Discord]: https://discord.com/invite/fJbxSy2EjA
[Paper API]: https://papermc.io/javadocs/paper/1.16/index.html?overview-summary.html


