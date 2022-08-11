# Event handler
To handle minigame events normally, you have to check somethings like the event is related with this minigame or minigame is started or not. So with this API, new event handler is supported for more easy use. 

# 1. GameEvent
Using `@GameEvent` annotation, you don't have to check the event is related the minigame or not if the event player is detectable by the below list. 

Event is only passed if...
- player is the player of `PlayerEvent`
- player is the entity of `EntityEvent`
- player is the entity of `HangingEvent`
- player is the inventory viewer of `InventoryEvent`
- player is the inventory viewer of `InventoryMoveItemEvent`
- player is the player of `PlayerLeashEntityEvent`
- player is the sender of `TabCompleteEvent`
- player is related with some events of `BlockEvent`, `EntityEvent` and `VehicleEvent` ([Detailed](detectable-event-list.md))


So, if you want to handle `PlayerBedEnterEvent` add method with `@GameEvent` annotation and **shoud use `protected` or `public` modifier.**
```java
@GameEvent
public void onEnterBed(PlayerBedEnterEvent e) {
    e.getPlayer().sendMessage("you entered a bed");
}
```

Generally, event will be passed game is only playing, not waiting. But using waiting state of `@GameEvent` annotation, event is passed when the game is waiting.
```java
@GameEvent(state = State.WAIT)
public void onEnterBed(PlayerBedEnterEvent e) {
    e.getPlayer().sendMessage("you entered a bed");
}
```
Or `State.ALL`(WAIT + PLAY) also exists.
```java
@GameEvent(state = State.ALL)
public void onEnterBed(PlayerBedEnterEvent e) {
    e.getPlayer().sendMessage("you entered a bed");
}
```





# Warning
If the event needs to be called frequently, implement it with `@EventHandler`.


