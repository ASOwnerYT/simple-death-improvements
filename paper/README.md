# Death Improvements Plus (Paper)

The Paper plugin version of Death Improvements Plus. It is server-side only and runs on Paper 1.21.1 and newer (compiled against 1.21.1 and 26.3, bot-tested on 1.21.1 and 26.1.2).

## Building

From the repository root:

```bash
./gradlew -p paper build
```

The jar ends up in `paper/build/libs/`. Its version comes from `mod.version` in `stonecutter.properties.toml`. To check the code against the newest Paper API (needs Java 25):

```bash
./gradlew -p paper build -Ppaper.api=26.3.build.237-stable -Pjava.version=25
```

## Configuration

`plugins/DeathImprovementsPlus/config.yml` holds the same options as the mod's config. Run `/dip reload` (permission `deathimprovementsplus.reload`, op by default) after editing it.

## Differences from the Fabric/NeoForge mod

- **No accessory support.** Trinkets, Curios and Ohmega are mods, so `keep-accessories-on-death` does not exist.
- **Explosions** no longer damage dropped items, but can still push them around.
- **Safe spots are per world.** If a player dies in a different world than their last safe spot (e.g. right after going through a portal), items drop where they died.
- **Safe spots** must not have lava or fire in the block above, so teleporting straight into lava can't record the floor under it.
- **Airborne deaths** scan downward only until the first solid block, so lava hidden under a floor no longer counts as "above lava".
- **Experience** drops at the death location if no safe spot has been recorded yet, instead of at 0, 0, 0.
- **Drop timer** (`no-death-item-despawn: false`) counts in 1-second steps.

The plugin handles player drops at `HIGHEST` priority, after most other plugins. Grave or soulbound plugins that change the drop list at a lower priority still work.
