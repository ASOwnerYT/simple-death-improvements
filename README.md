# Death Improvements Plus

Less frustrating deaths in Minecraft: no item despawn, no item splatter, item safeguards and more. Small, vanilla-friendly tweaks to player death, as a lightweight alternative to gravestone mods. Everything is configurable.

> **This is a fork.** Death Improvements Plus is based on [Simple Death Improvements](https://github.com/pajicadvance/simple-death-improvements) by **pajic**, used under the MIT License. All of the original mod's features and design are pajic's work. Please report problems with this fork [here](https://github.com/ASOwnerYT/simple-death-improvements/issues), not to the original project.

## What's different from Simple Death Improvements

- **Paper server plugin.** Besides the Fabric and NeoForge mods, the same features are available as a Paper plugin for Paper 1.21.1 and newer. See [paper/README.md](paper/README.md).
- **New mod id** (`death_improvements_plus`), so it doesn't clash with the original mod. Configs from Simple Death Improvements are not carried over.
- **Builds and releases** are published automatically on [GitHub Releases](https://github.com/ASOwnerYT/simple-death-improvements/releases).

## Downloads

| Platform | Minecraft versions | Requires |
| --- | --- | --- |
| Fabric | 1.21–1.21.1, 26.1–26.3 | Fabric API, Fzzy Config |
| NeoForge | 1.21–1.21.1, 26.1–26.2 | Fzzy Config |
| Paper (plugin) | 1.21.1 and newer | — |

Grab the jar for your platform and Minecraft version from the [latest release](https://github.com/ASOwnerYT/simple-death-improvements/releases/latest). The mod is server-side only; clients don't need it.

## Features

### Items dropped on death never despawn

Items you drop on death stay where they dropped forever, so there's no rush to get back to them.

### No item and experience splatter

Your items and experience drop together at the exact spot you died, instead of scattering everywhere and possibly flying into nearby lava.

### Lava and Void item safeguarding

If you die somewhere your items aren't safe (in or above lava or the Void), they drop at the last safe spot you walked over instead. If you turn off "no item splatter", some items may still be destroyed.

### Explosion resistant items

Explosions no longer destroy dropped items. This is especially useful in fights with the Wither.

### Lose less experience on death

You drop a percentage of your total experience on death (80% by default) instead of the much smaller vanilla amount.

### Keep items on death (off by default)

Optionally keep armor, hotbar, offhand or equipped accessory items on death, each with a list of items that drop anyway. These settings are ignored when the `keepInventory` gamerule is on. Accessory support (Trinkets, Curios, Ohmega) is only available in the Fabric and NeoForge mods.

## Building

Requires Java 25 (Gradle downloads the toolchains it needs).

```bash
./gradlew buildAndCollect
```

Mod jars for every loader and Minecraft version end up in `build/libs/<version>/`. To build the Paper plugin:

```bash
./gradlew -p paper build
```

## Releasing

Bump `mod.version` in `stonecutter.properties.toml`, add a section to `CHANGELOG.md`, then push a matching tag:

```bash
git tag v1.0.0 && git push origin v1.0.0
```

GitHub Actions builds every target and publishes a GitHub Release with all the jars, using that version's `CHANGELOG.md` section as the release notes.

## AI disclosure

This fork was made with heavy use of AI. The Paper plugin, the rename to Death Improvements Plus, the build changes and the GitHub Actions workflows were written by Claude Code (Anthropic's AI coding assistant), directed by ASOwnerYT. The Paper plugin was checked with automated bot tests on Paper 1.21.1 and 26.1.2. The Fabric and NeoForge mod code is pajic's original work; the fork only renamed its identifiers.

## Credits and license

- **pajic**: author of [Simple Death Improvements](https://github.com/pajicadvance/simple-death-improvements), which this fork is based on, including the mod icon.
- **ASOwnerYT**: maintainer of this fork.

Released under the [MIT License](LICENSE). The original copyright notice is kept as the license requires.
