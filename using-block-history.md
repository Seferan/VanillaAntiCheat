# Using block-history.rb
`block-history.rb` is a tool to quickly query the block log. It might take a while to load the block log completely, because the block log could be very long. It's pretty crappy, but it works for now.

## Options:
- `-f` - Specify a specific log file. This option is required
- `-to` - Look for logs with a time older than the time specified.
- `-te` - Look for logs with a time earlier than the time specified.
- `-p` - Look for logs for a specific player.
- `-s` - Look for logs with a specific state (`broke`, `placed`, `accessed`)
- `-i` - Look for logs for a specific item (ex: `tile.chest`)
- `--pos1` - Look for logs with positions that are greater than the one given. Format: `x.y.z` (ex: `100.0.256`). Must be used with `--pos2`.
- `--pos2` - Look for logs with positions that are lesser than the one given. FOrmat: `x.y.z` (ex: `200.100.512`). Must be used with `--pos1`.
- `--interactive` - Interactive mode. After loading the block log, it will give you a prompt where you can type in commands. This is useful when you know you will make multiple queries so you don't have to load the block log each time. Examples of commands that you would enter at the prompt: `-p afffsdd -s broke -i tile.chest`, `-s accessed --pos1 0.0.0 --pos2 50.256.123`