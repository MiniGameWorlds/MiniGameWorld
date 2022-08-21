# Settings and utils
There are some static hard coded setting values in `Setting` class. And also static util methods in `Utils` class.

# Setting
Almost values are declared as final. But not final values are the options of `settings.yml` config, so do not change them to another value but just use them as a value.

```java
if(Setting.DEBUG_MODE) {
    // something to debug
}
```

# Utils
In generally, `Utils` class is used for logging and sending messages to players with prefix. (Prefix = `Setting.MESSAGE_PREFIX`)
```java
// message: [MiniGameWorld] Game is ready
Utils.sendMsg(player, "Game is ready");

// red text in the console when Setting.DEBUG_MODE is true: [MiniGameWorld] debug log
Utils.debug("debug log");
```