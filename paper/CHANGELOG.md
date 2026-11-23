# Changelog

Changes to the Paper plugin. Versions follow Simple Death Improvements, see the root `CHANGELOG.md` for the mod's own changes.

## 1.7.0

- Added the "Keep items with durability on death" option (`keep-damageable-items-on-death`, `kept-item-durability-loss-percent`, `damageable-item-drop-list`). Items with durability are kept on death, but lose a configurable percentage of their total durability (20% by default). This can never break an item. Items kept by other keep options don't lose durability.
- Experience is now always dropped at the same position as items.
- Spectators no longer drop extra experience on death when `player-drop-more-xp-on-death` is enabled.
- The last safe position is no longer recorded while in spectator mode or in midair, and tracking starts right after joining.
- Fixed the bottom layer of the world not counting as safe ground when checking for a safe spot below the player.
- Renamed to Simple Death Improvements to match the mod. The plugin is now `SimpleDeathImprovements`, its command is `/sdi reload` and its permission is `simpledeathimprovements.reload`. Configs from Death Improvements Plus are not carried over.
