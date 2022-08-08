# Chat
Chat is isolated between in the game and out of the game by default. But you can set `true`/`false` with `isolated-chat` option in `settings.yml` config. (Default: `true`)


## General
`Outer`: not play any game players
- Can send chat message to `Outer` only (if `isolated-chat` is true)

`Player`: playing game players
- Can send chat message to `Player` and `Viewer` only (if `isolated-chat` is true)

`Viewer`: viewing game players
- Can send chat message to `Viewer` only


## Party
Party members can chat with `/mw party msg <message>` command.


## Custom
Can disable chat in the game by setting the `chat` option in `custom-data` of game config.