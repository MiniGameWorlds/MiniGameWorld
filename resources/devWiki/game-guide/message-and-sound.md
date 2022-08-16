# Message and sound
To send message and play sound to players, there are some util methods for easy using. But these methods are not sent to the viewers.

# Message
If you send messages using this way, **game title prefix** will be contained by default.
- `sendMessage(Player, String)`: send message to a player with prefix
- `sendMessages(String)`: send message to all playing players with prefix



# Title
There are also title util methods that is sent with default timeout. (fade in = 4 ticks / stay = 12 ticks / fade out = 4 ticks)
- `sendTitle(Player, String, String)`: send title and sub title to a player 
- `sendTitles(String, String)`: send title and sub title to all playing players



# Sound
- `playSound(Player, Sound)`: Play sound to a player
- `playSounds(Sound)`: Play sound to all playing players