# Language Support
- MiniGameWorld supports various languages
- Each players can select their language
- [Languages folder]
- ❗ **Every messages are NOT yet translated (on working)**



# Supported languages
- English
- Korean
- Russian



# Downlaod
- [AdvancedMultiLanguage]



# Commands
- `/language <language>`: Change player's own laguage to `<language>` (`EN`, `KO`, `RU` ... etc)



# How to edit message
- Change `edit-messages` option in `settings.yml` config to **false** and change messages of language configs in the `messages` folder
- **Warning**: if messages updated with plugin update, you need to set `edit-messages` option to update messages (**all messages will be overwritten**)



# How to contribute
- You can contribute with translating the language files in **[Languages folder]** and pull request.
- Each message key in language file belongs to its `Class file name` (search a class in the [javadoc API] and follow the package)
- If your language file doesn't exist, create file with language name in the [list](#language-list) like `DE.yml` and copy and paste all the content of `EN.yml`, then you can translate any of them.



# System
- Default language can be set in `defaultLanguage` key in the `plugins/AdvancedMultiLanguage/config.yml` file
- Support various languages with [Github-AdvancedMultiLanguage] plugin API
- If run your server without `AdvancedMultiLanguage` plugin, only **English** will be supported
- Messages are managed by google translator or other contributers



# Placeholder
- Each message has different placeholders with `<` and `>` (Search the placeholder used in EN.yml(**standard**))
> e.g. `join-message: '<player> joined the <minigame>'`  
> e.g. `play-time-in-rule: 'Play time: <play-time> <sec>' `

## Custom placeholder
- Each language file can have different `custom` placeholders
- Anyone can add own custom placeholders (do the pull request)
- Custom placeholder can be used for language features like below examples

```yaml
# [English]
messages:
  Example:
    break-block: You <cn> break the block!
    # └─ After replaced: You can not break the block!
    welcome-message: <thx> for coming my server!
    # └─ After replaced: Thank you for coming my server!
  custom:
    cn: can not
    thx: Thank you


# [Korean]
messages:
  Example:
    break-block: 당신<n> 이 블럭을 부수지 못합니다!
    # └─ After replaced: 당신은(는) 이 블럭을 부수지 못합니다!
    welcome-message: 서버에 접속해주셔서 <thx>!
    # └─ After replaced: 서버에 접속해주셔서 감사합니다!
  custom:
    n: 은(는)
    thx: 감사합니다
```


# Language list
```yaml
# Supported by AdvancedMultiLanguage
For English: EN
For French: FR
For German: DE
For Dutch: NL
For Spanish: ES
For Russian: RU
For Latvian: LV
For Italian: IT
For Bulgarian: BG
For Hungarian: HU
For Dansk: DK
For Chinese: CHS
For Polish: PL
For Portuguese: PT
For Slovenia: SLO
For Lithuanian: LT
For Turkish: TK
For Slovak: SK
For Czech: CS
For Korean: KO
For Romanian: RO
More coming soon*
```

[Languages folder]: https://github.com/MiniGameWorlds/MiniGameWorld/tree/main/src/resources/messages
[javadoc API]: https://minigameworlds.github.io/MiniGameWorld/
[Github-AdvancedMultiLanguage]: https://github.com/smessie/AdvancedMultiLanguage
[AdvancedMultiLanguage]: https://www.spigotmc.org/resources/advanced-multi-language.21338/