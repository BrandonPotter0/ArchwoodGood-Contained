# Archwood Good

<p align="center">
  <img src="src/generated/resources/assets/Archwood%20Wizard%20Logo%20with%20Magical%20Cubes.png" alt="Archwood Good — wizard and magical Archwood cubes" width="100%">
</p>

<p align="center">
  <strong>More Archwood. More variety. More magic.</strong>
</p>

<p align="center">
  <a href="https://github.com/BrandonPotter0/ArchwoodGood-Contained/releases/latest">Latest Release</a> ·
  <a href="https://github.com/BrandonPotter0/ArchwoodGood-Contained/issues">Report an Issue</a> ·
  <a href="https://github.com/BrandonPotter0/ArchwoodGood-Contained">Source Code</a>
</p>

**Archwood Good** is an expansion and companion mod for [Ars Nouveau](https://github.com/Ars-Nouveau/Ars-Nouveau) and [Ars Elemental](https://github.com/Alexthw46/Ars-Elemental), for Minecraft **1.21.1** on **NeoForge**.

The mod expands the Archwood ecosystem with additional wood content, tree growth, forest and cave environments, world-generation features, recipes, and compatibility improvements. Its goal is to make Archwood feel like a complete and varied part of a magical Minecraft world.

## Version & Downloads

| | |
|---|---|
| **Project version** | **1.2.10** |
| **Latest published release** | **1.2.10** |
| **Minecraft** | **1.21.1** |
| **Mod loader** | **NeoForge** |
| **NeoForge build** | 21.1.233 |
| **License** | GNU Lesser General Public License v3.0 |

**[⬇ Download Archwood Good 1.2.10](https://github.com/BrandonPotter0/ArchwoodGood-Contained/releases/tag/1.2.10)**

For previous versions, see the [Releases page](https://github.com/BrandonPotter0/ArchwoodGood-Contained/releases).

## Features

### 🌳 Expanded Archwood family

Archwood Good supports the seven Archwood colours:

- 🔴 **Blazing (Red)**
- 🔵 **Cascading (Blue)**
- 🟢 **Flourishing (Green)**
- 🟣 **Vexing (Purple)**
- 🟡 **Flashing (Yellow)**
- 🟠 **Dawning (Orange)**
- ⚪ **Blinding (White)**

It fills out the Archwood wood family with additional building and decorative content, including logs, stripped logs, wood, planks, stairs, slabs, doors, trapdoors, signs, hanging signs, boats, saplings, leaves, barrels, and related blocks and items.

### 🌱 Tree growth

The expanded tree system includes:

- Natural 1×1 Archwood tree growth.
- Natural 2×2 Giant Archwood Tree growth.
- Bonemeal support for normal and Giant Archwood trees.
- Consistent Giant Tree behaviour across the supported Archwood colours.
- Fuller Giant Archwood canopies.
- Additional natural generation for Dawning and Blinding Archwood.
- Rare mixed-colour forest generation.

### 🌲 Forests and world generation

Archwood Good adds and expands specialized Archwood environments.

#### Dawning and Blinding Archwood forests

Dawning (Orange) and Blinding (White) Archwood have dedicated world-generation support, including forests, dense tree generation, and rare mixed variants.

#### Fading Archwood Forest

A darker, decayed forest environment featuring Fading Archwood, cobwebs, skulls, Wither Roses, dead bushes, cobblestone mounds, and white ash particles. Its atmosphere is intended to feel eerie and abandoned, with undead-heavy spawning and distinctive ambient presentation.

#### Glacial Archwood Caves

A specialized underground environment with glacial Archwood growth, blue and white cave vegetation, ice and packed-ice formations, snow, icy Mage Lights, and custom cave generation. Its freezing behaviour has been tuned to make fluid changes more controlled and natural.

#### Giant Archwood forests

Giant Archwood Trees can generate naturally in Archwood Forests. This feature is enabled by default and can be disabled in the mod configuration.

### 🏛️ Ancient Archwood Tree

The Ancient Archwood Tree is a rare, large-scale landmark structure in the Archwood ecosystem. It features an enormous interconnected tree with multiple branches, large roots, mixed Archwood foliage, and magical lighting.

A configurable circular exclusion zone helps preserve the space around the structure by preventing regular Archwood trees from generating too close to it. The exclusion can be disabled, and its radius can be adjusted in the configuration.

### 🔨 Recipes and crafting

Archwood Good expands crafting and processing options for the Archwood family, including:

- The Archwood Crafting Table.
- Additional Dawning and Blinding Archwood recipes.
- Fading Archwood conversion recipes using an Amethyst Shard.
- Supporting block tags, loot tables, and recipe data.

### ✨ Ars Elemental ritual integration

Archwood Good registers its Dawning and Blinding Archwood saplings and Archfruit pods with Ars Elemental's shared Archwood variant registry. This supports the Tablet of Forestation ritual and allows Dawnberry and Lightchee pods to augment **Conjure Island: Archwood Forest**, selecting the Dawning and Blinding Archwood biome keys respectively. Fading Archwood remains available through the Forestation ritual, while mixed Archwood forest generation retains the five original Ars Elemental variants alongside Dawning and Blinding.

## Installation

1. Install **Minecraft 1.21.1** with **NeoForge**.
2. Install the required dependencies listed below.
3. Download the latest Archwood Good JAR from the [Releases page](https://github.com/BrandonPotter0/ArchwoodGood-Contained/releases).
4. Place the JAR in your Minecraft instance's `mods` folder.
5. Launch the game using your NeoForge profile.

Use the release asset ending in `.jar`; do not install a `-sources.jar` file if one is provided.

## Dependencies

The following mods are required:

- [Ars Nouveau](https://github.com/Ars-Nouveau/Ars-Nouveau) — provides the core Archwood ecosystem.
- [Ars Elemental](https://github.com/Alexthw46/Ars-Elemental) — provides additional Archwood variants and integration.

Optional integrations:

- **TerraBlender** — enables natural world generation for the Dawning, Blinding, Fading, and Glacial Archwood biomes. It is optional, so Archwood Good can still launch without it; however, these four biomes will not generate naturally unless TerraBlender is installed.
- **Cloth Config** — provides an in-game configuration screen for Giant Archwood Trees.

Make sure you download dependency versions compatible with Minecraft 1.21.1 and your NeoForge installation. The exact dependency versions used for the current development build are listed in `gradle.properties`.

## Configuration

Archwood Good provides configuration options for selected world-generation features, including:

- **Giant Archwood Trees** — enable or disable natural Giant Archwood Tree generation.
- **Ancient Archwood exclusion zone** — enable or disable the protected area around Ancient Archwood Trees.
- **Exclusion radius** — adjust the size of the circular protected area.

Configuration files are created in the instance's `config` folder after the mod has been launched. Available settings may vary by release.

## Compatibility

Archwood Good is designed for the Ars Nouveau and Ars Elemental ecosystem. It also includes compatibility and data support for the wider Archwood family, including recipes, tags, loot tables, and world-generation interactions.

- Minecraft: **1.21.1**
- Mod loader: **NeoForge**
- Required mods: **Ars Nouveau** and **Ars Elemental**

## Credits & License

Archwood Good builds on the original Archwood Good project by **Alexthw** and **Xel'Bayria**.

Current project authors: **Alexthw, Xel'Bayria, and TheFloofyPoofy**.

Special thanks to the developers and contributors of:

- [Ars Nouveau](https://github.com/Ars-Nouveau/Ars-Nouveau)
- [Ars Elemental](https://github.com/Alexthw46/Ars-Elemental)
- The NeoForge project and the wider Minecraft modding community.

This project is licensed under the **GNU Lesser General Public License v3.0 (LGPL-3.0)**. See the repository's license file for the full license text.

## Issues & Contributions

Found a bug or have an idea? Please open an issue in the [GitHub issue tracker](https://github.com/BrandonPotter0/ArchwoodGood-Contained/issues). When reporting a problem, include your Minecraft version, NeoForge version, Archwood Good version, relevant dependency versions, and a crash report or log when available.

---

<p align="center">
  <strong>Archwood Good — making the Archwood forests bigger, stranger, and a little more magical. 🌳✨</strong>
</p>
