# Task management
Using API, you can manage task easily without cancelling them in the game finish. And you can also use several game timing hook methods to reserve some actions.

# 1. Task Manager
The way how to use `task manager` is simple. 

First, register `new Runnable()` instance with name in the constructor. **NEVER use `new BukkitRunnable()`**, it won't work.
```java
// constructor
public GameA() {
    // ...

    // send message to everyone
    taskManager().registerTask("task1", new Runnable() {
        @Override
        public void run() {
            sendMessages("task1");
        }
    });

    // or can use simply using lambda
    taskManager().registerTask("task1", () -> sendMessages("task1 with lambda"));
}
```

Second, just call the registered task with name using the need task method  wherever you need. You don't have to cancel any of them.
```java
@Override
	protected void onStart() {
		super.onStart();

        // call task1 instantly
        taskManager().runTask("task1");

        // call task1 3 secs later
        taskManager().runTaskLater("task1", 20 * 3);

        // call task1 in every 1 sec
		taskManager().runTaskTimer("task1", 0, 20);
	}
```



# 2. Timing hook
Here is a another way to reserve tasks with the hook methods of frame class.
- `onStart()`: Called at game start
- `onFinishDelay()`: Called at game starts finishing delay (before `onFinish()`)
- `onFinish()`: Called at game the game instance is removed (after `onFinishDelay()`)
- `onJoin(Player)`: Called when a player joined game
- `onLeave(Player)`: Called when a player leave game
- `onView(Player)`: Called when a player starts to view game
- `onUnview(Player)`: Called when a player exits the view

Override `onStart()` method and recommend to not remove `super.onStart()`. Because any frame classes can override them previous for need.
```java
// Called at every game start
@Override
protected void onStart() {
    super.onStart();
    
    // send message to everyone
    sendMessages("Game Start!");

    // give stone sword to everyone
    players().forEach(p -> p.getInventory().addItem(new ItemStack(Material.STONE_SWORD)));
}
```

