# Simple Death Improvements (Paper)

The Paper plugin version of Simple Death Improvements. It is server-side only and runs on Paper 1.21.1 and newer (compiled against 1.21.1 and 26.3, bot-tested on 1.21.1, 1.21.11 and 26.1.2).

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

`plugins/SimpleDeathImprovements/config.yml` holds the same options as the mod's config. Run `/sdi reload` (permission `simpledeathimprovements.reload`, op by default) after editing it.

## Differences from the Fabric/NeoForge mod

- **No accessory support.** Trinkets, Curios and Ohmega are mods, so `keep-accessories-on-death` does not exist and `keep-damageable-items-on-death` only covers the player inventory.
- **Explosions** no longer damage dropped items, but can still push them around.
- **Safe spots** must not have lava or fire in the block above, so teleporting straight into lava can't record the floor under it.
- **Drop timer** (`no-death-item-despawn: false`) counts in 1-second steps.

The plugin handles player drops at `HIGHEST` priority, after most other plugins. Grave or soulbound plugins that change the drop list at a lower priority still work.
