# 설명
마인크래프트 plugin.yml 파일

# 기능


# 사용법
```yaml
name: MiniGameWorld
main: com.minigameworld.MiniGameWorldMain
version: 1.0
author: worldbiomusic
description: worldbiomusic@gmail.com
api-version: 1.17
depend: [wbmMC]

commands:
  minigame:
    aliases: mg
    usage: |
      Usage
      /minigame join <title>
      /minigame leave
      /minigame list
      /minigame reload
```


# 주의사항
- wbmMC 라이브러리가 항상 같은 폴더에 필요함

# 개선할 것
- command permission 알아보기
- api-version 17 변경
