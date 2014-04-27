# VanillaAntiCheat
VanillaAntiCheat is a non-Bukkit anticheat that also adds many other useful features to the server software, such as commands like ``/feed``, and checking if player IPs are proxies or not.

## Features
 - Anti-speedhack (Sneak and Sprint hacks)
 - Anti-fly
 - Diamond Notifications (helps identify xrayers)
 - Anti-fastbreak (Speedy Gonzales)
 - Anti-fastplace (Buildhack)
 - Anti-spam (autobans)
 - Anti-spider hack
 - Anti-glide hack
 - Block history log (can be used to find griefers)
 - Logging and admin notifications
 - Disabling certain commands for OPs and only allowing 'owners' to use them (like /stop)
 - Extra game rules
 - Fully configurable
 - Better performance than the default vanilla Minecraft server jar
 - Extra commands
 
## Extra Commands
- `butcher` - kills all useless entities. Includes arrows, hostile mobs, items, etc
- `feed` - feeds a player by filling his hunger meter.
- `heal` - heals a player.
- `spawn` - teleports an OP back to spawn, and tells the coordinates of spawn to non-OPs.
- `myip` - tells a player his IP.
- `item` - reflexive /give
- `gc` - force Minecraft to perform garbage collection
- `creative`, `survival` aliases for `gamemode`

## Extra Settings
These are all configurable either using /gamerule or server.properties.

- Admins keep inventories
- No TNT explosion
- No fire spread
- Kicks are tempbans
- Check players to see if they are using a proxy

## Contributing
Testing VAC is a total pain because there isn't really a way to automatically test Minecraft, so I have to go into an offline server and make sure everything is functioning. Even still, it's easy to miss some bugs. A great way to contribute is to test VAC.

Another more obvious way of contributing is just to choose an issue, fork VAC and send a pull request.