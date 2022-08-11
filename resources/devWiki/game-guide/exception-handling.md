# Exception handling
There could be some exceptions while playing the game like player left game while playing or special server event that need to end some games. 

API exceptions are divided into 3 types and have their `reason`. So the same exception can be distiguished by the `reason`.

# How to use
Override `onException(exception)` in the game class and don't remove `super.onException(exception)` because frame class could be using for its need.

**Exception ONLY called when a game is plyaing state, not waiting state**


## 1. MiniGamePlayerExceptionEvent
Called when a player needs to leave the game. After calling, only the player will leave the game and game could be finished if [`gameFinishCondition`](settings.md) is fit to finish automatically.

### Built-in reasons
- `Setting.PLAYER_EXCEPTION_QUIT_SERVER`: Called when a player quit the server.
<!-- - `Setting.PLAYER_EXCEPTION_INGAME_LEAVE`: Called when a player left the game while game is in waiting state. -->


```java
@Override
protected void onException(MiniGameExceptionEvent exception) {
    super.onException(exception);

    // check exception type
    if (exception instanceof MiniGamePlayerExceptionEvent) {
        MiniGamePlayerExceptionEvent playerException = (MiniGamePlayerExceptionEvent) exception;
        Player p = playerException.getPlayer();
        
        // change king if game started and left player is was a king
        if (isStarted() && p.equals(this.king)) {
            changeKing();
        }
    }
}
```

---

## 2. MiniGameExceptionEvent
Called when a minigame needs to be finished. After calling, playing players will be kicked from 
the game and the game will be finished

### Built-in reasons
<!-- - `Setting.GAME_EXCEPTION_DATA_UPDATE`: Called when a instance game data needs to be updated. (called by reload command generally) -->

```java
@Override
protected void onException(MiniGameExceptionEvent exception) {
    super.onException(exception);
    
    // check exception type
    if(exception instanceof MiniGameExceptionEvent) {
        String reason = exception.getReason();
        if(reason.equals(Setting.GAME_EXCEPTION_DATA_UPDATE)) {
            sendMessages("You've kicked from the game cause of game data needs update.");
        }
    }
}
```

---

## 3. MiniGameServerExceptionEvent
Called when all the games need to be finished by some reasons like server crashed or stop command by OPs. 

### Built-in reasons
- `Setting.SERVER_EXCEPTION_STOP_BY_PLAYER`: Called when server stopped by a player (e.g. command)
- `Setting.SERVER_EXCEPTION_STOP_BY_NON_PLAYER`: Called when server stopped by non player (e.g. console)
- `Setting.SERVER_EXCEPTION_PLUGIN_DISABLED`: Called when this plugin is disabled


```java
@Override
protected void onException(MiniGameExceptionEvent exception) {
    super.onException(exception);

    // check exception type
    if (exception instanceof MiniGameServerExceptionEvent) {
        if (exception.getReason().equals(Setting.SERVER_EXCEPTION_STOP_BY_PLAYER))
            sendMessages("Exception occured by OP's command");
    }
}
```

# ETC
To create and call exception, please see [3rd-party wiki](../3rd-party-guide/Home.md)