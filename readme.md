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
- `creative`, `survival` - aliases for `gamemode`
- `motd` - Display a long motd message to a player.
- `reload` - Reloads all configuration files and ban/op/white lists.
- `/spawn` - If a player is not an OP, tell him the co-ordinates of spawn. Otherwise, teleport the player to spawn.

## Extra Settings
These are all configurable either using /gamerule or server.properties.

- Admins keep inventories
- No TNT explosion
- No fire spread
- Kicks are tempbans
- Check players to see if they are using a proxy

### server.properties settings:
- `kick-tempban-length` (integer) - if `tempban-on-kicks` is true, a player who is kicked will be banned for this number of minutes.
- `log-ips` (boolean) - Whether IPs should be logged or not.
- `tell-ip` (boolean) - Whether the server should tell players their IP or not. If this is true, log-ips will be treated as false regardless of its value.
- `tempban-on-kicks` (boolean) - If true, players who are kicked will be banned for `kick-tempban-length` number of minutes.
- `vac-block-log-max-logs-per-second` (integer) - The maximum number of logs that will be logged from a player each second. It will record the first n logs.
- `vac-buildhack-threshold` (integer) - The number of blocks a player can build in a short timespan before getting reset.
- `vac-check-proxies` (boolean) - Whether to check if player IPs are proxies or not.
- `vac-check-proxies-mode` (integer) - If 0, if checking if an IP is a proxy fails, it will assume that the IP is not a proxy. If 1, it will assume that it is a proxy.
- `vac-diamond-notifications` (boolean) - If admins should be notified when players find diamonds.
- `vac-fastbreak-leeway` (double) - How much faster a player can break a block than normally possible. 1 = no protection, 0 = perfect timing required
- `vac-fastbreak-ratio-threshold` (double) - The ratio of blocks broken too fast to the number of blocks broken within the leeway threshold that is acceptable. If the ratio exceeds this, the player will get reset if he breaks blocks too fast.
- `vac-floating-ticks-threshold` (integer) - The number of ticks the player can be floating (flyhack, spider hack, glide hack)
- `vac-fly-reset-kick-threshold` (integer) - How many times a player can be reset for flying (also spider/glide hack) before getting kicked.
- `vac-fly-reset-log-threshold` (integer) - How many times a player can be reset for flying (also spider/glide hack) before getting logged.
- `vac-health-regen-tickcount` (integer) - How many ticks it should take a player to regenerate a heart of health. 80 is the normal amount.
- `vac-spam-cooldown-threshold` (integer) - How many identical messages in a row a player has to say before getting a spam cooldown.
- `vac-speed-limit-jumping-potion` (double) - The speed limit for someone who is jumping with a speed potion active.
- `vac-speed-limit-jumping` (double) - The speed limit for someone who is walking and jumping.
- `vac-speed-limit-potion` (double) - The speed limit for someone who is walking with a speed potion active.
- `vac-speed-limit-sneak` (double) - The speed limit for someone who is sneaking.
- `vac-speed-limit-sprinting-jumping-potion` (double) - The speed limit for someone who is sprinting and jumping (bunnyhopping) with a speed potion active.
- `vac-speed-limit-sprinting-jumping` (double) - The speed limit for someone who is sprinting and jumping (bunnyhopping).
- `vac-speed-limit-sprinting-potion` (double) - The speed limit for someone who is sprinting with a speed potion active.
- `vac-speed-limit-sprinting` (double) - The speed limit for someone who is sprinting.
- `vac-speed-limit` (double) - The speed limit for someone who is walking.
- `vac-speedhack-leeway` (double) - How much faster a player can exceed the speed limit by and not have it be counted.
- `vac-speedhack-ratio-threshold` (double) - The ratio of how many times a player can move too quickly to how many times a player can move at a reasonable speed. If the ratio exceeds this, the player will be set back if he moves too quickly.

## Block log
The block log keeps track of all blocks that are broken or placed, items that create blocks (such as buckets and signs), other items that are suspicious (like flint and steel), and all containers that are accessed (chests, furnaces, etc). It resides in `block-history.txt`.

### Format: `date playername state item x y z`

Examples:

- `Tue Apr 22 19:21:51 EDT 2014 afffsdd broke tile.stone 604 59 184`
- `Tue Apr 22 19:22:37 EDT 2014 afffsdd placed tile.stonebrick 597 54 177`
- `Tue Apr 22 19:25:32 EDT 2014 afffsdd accessed tile.chest 597 54 127`

The item name is generated from the item's unlocalized name. If the item is a block, the item that corresponds to the block is used.

There are three states: `broke`, `placed`, and `accessed`.

You can query the block log using `block-history.rb`.

## Building
Download MCP 9.03 RC1 and extract it. Then, download a vanilla Minecraft 1.7.2 server jar and rename it `minecraft_server.jar`. Put the server jar into the jars folder and run ``./decompile.bat --server``. Then, clone this repo and replace `src/minecraft_server` with the the one from this repo. Run `./recompile.bat --server`. Copy `minecraft_server.jar` into a folder along with the `lib` and `bin` folders from MCP. Finally, create a batch script with these contents:
```Batchfile
@echo off
java -cp "bin\minecraft_server;lib;lib\*;minecraft_server.1.7.2.jar" net.minecraft.server.MinecraftServer
pause
```
To start the server, execute the batch file.

It should look something like this:
```
server
│   minecraft_server.1.7.2.jar
│   start.bat
├───bin
│    └───a bunch of .class files and subdirectories
│
└───lib
```

## Contributing
Testing VAC is a total pain because there isn't really a way to automatically test Minecraft, so I have to go into an offline server and make sure everything is functioning. Even still, it's easy to miss some bugs. A great way to contribute is to test VAC.

Another more obvious way of contributing is just to choose an issue, fork VAC and send a pull request.