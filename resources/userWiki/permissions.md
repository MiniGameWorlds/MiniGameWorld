# Link
- [plugin.yml](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/src/plugin.yml)

```yaml
permissions:
  minigameworld.*:
    description: Grants all permissions
    children: 
      minigameworld.menu: true
      minigameworld.signblock: true
      minigameworld.allcommands: true
      minigameworld.play.*: true
      minigameworld.party.*: true
      minigameworld.config.*: true
      
  minigameworld.menu:
    description: Can open a GUI menu (can access play, party)
    default: true
    
  minigameworld.signblock:
    description: Can play minigame with sign block (can access play, party)
    default: true
    
  minigameworld.allcommands:
    description: Can use all commands (can access play, party)
    default: true
    
  minigameworld.play.*:
    description: Grants all play permissions
    children:
      minigameworld.play.join: true
      minigameworld.play.leave: true
      minigameworld.play.view: true
      minigameworld.play.unview: true
      minigameworld.play.list: true
  minigameworld.play.join:
    description: Can join a minigame
    default: true
  minigameworld.play.leave:
    description: Can leave a playing minigame
    default: true
  minigameworld.play.view:
    description: Can view a minigame
    default: true
  minigameworld.play.unview:
    description: Can leave a viewing minigame
    default: true
  minigameworld.play.list:
    description: Can print all minigame list
    default: true
    
  minigameworld.party.*:
    description: Grants all party permissions
    children:
      minigameworld.party.invite: true
      minigameworld.party.accept: true
      minigameworld.party.ask: true
      minigameworld.party.allow: true
      minigameworld.party.leave: true
      minigameworld.party.kickvote: true
      minigameworld.party.msg: true
      minigameworld.party.list: true
  minigameworld.party.invite:
    description: Can invite a player
    default: true
  minigameworld.party.accept:
    description: Can accept invitation
    default: true
  minigameworld.party.ask:
    description: Can ask to join a player's party
    default: true
  minigameworld.party.allow:
    description: Can allow player to join
    default: true
  minigameworld.party.leave:
    description: Can leave a party
    default: true
  minigameworld.party.kickvote:
    description: Can kickvote a member
    default: true
  minigameworld.party.msg:
    description: Can send message to member
    default: true
  minigameworld.party.list:
    description: Can print member list
    default: true
  
  minigameworld.config.*:
    description: Grants all config permissions
    children:
      minigameworld.config.reload: true
      minigameworld.config.settings: true
      minigameworld.config.minigames: true
  
  minigameworld.config.reload:
    description: Can reload all configs
    default: op
  minigameworld.config.settings:
    description: Can set settings config
    default: op
  minigameworld.config.minigames:
    description: Can set minigame config
    default: op
```