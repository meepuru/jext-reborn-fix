# JukeboxExtendedReborn - Fix

Jext Reborn Fix is my personal modified fork of original [JEXT](https://github.com/spartacus04/jext-reborn) project.

## What modified?

Now you can use this project behind a web proxy server like nginx.

When you filled in `override-web-interface-base-url` with url start with "https://" or "http://" in `config.json`, client will try to get resourcepack from this url directly instead of default `http://<override-web-interface-base-url>:<web-interface-port>`, then do everything you want.

## Dependencies

- ProtocolLib ([Spigot](https://www.spigotmc.org/resources/protocollib.1997/)-[Hangar](https://hangar.papermc.io/dmulloy2/ProtocolLib)-[Github](https://github.com/dmulloy2/ProtocolLib))
