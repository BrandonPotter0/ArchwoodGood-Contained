# Archwood Good

**Archwood Good** is a companion mod for **[Ars Nouveau](https://github.com/Ars-Nouveau/Ars-Nouveau)** and **[Ars Elemental](https://github.com/Alexthw46/Ars-Elemental)** for Minecraft 1.21.1.

It expands and completes the Archwood ecosystem with additional Archwood colors, wood sets, trees, forests, world generation, structures, recipes, compatibility, and atmospheric features. It is designed to make the Archwood forests feel like a larger, more varied part of an Ars Nouveau / Ars Elemental world while filling in missing content and improving how Archwood behaves in natural generation.

> **Current version:** 1.2.75  
> **Minecraft:** 1.21.1  
> **Mod Loader:** NeoForge

## What Does Archwood Good Add?

Archwood Good focuses on expanding the Archwood family and making the different Archwood colors behave consistently throughout world generation and tree growth.

### 🌳 Complete Archwood Tree Growth

All seven Archwood colors are supported:

- 🔴 Red
- 🔵 Blue
- 🟢 Green
- 🟣 Purple
- 🟡 Yellow
- 🟠 Orange
- ⚪ White

The Archwood tree system supports:

- Natural **1×1 Archwood tree** growth.
- Natural **2×2 Giant Archwood Tree** growth.
- Bonemeal acceleration for both normal and Giant Archwood trees.
- Consistent Giant Tree behavior across the Archwood color family.
- Larger, fuller Giant Archwood canopies.
- Additional Orange and White Archwood tree generation so they can occur naturally alongside the original Ars Nouveau / Ars Elemental colors.
- Rare mixed Orange/White Archwood generation for additional forest variety.

### 🪵 Expanded Archwood Building Blocks

Archwood Good fills out the missing wood-building content for the Archwood family, including additional Orange and White Archwood blocks and their associated items.

Supported building components include:

- Logs
- Stripped logs
- Wood
- Stripped wood
- Planks
- Stairs
- Slabs
- Doors
- Trapdoors
- Signs
- Hanging signs
- Boats and related boat content
- Saplings
- Leaves
- Barrels
- Other associated Archwood building/decorative content

### 🔨 Archwood Crafting & Recipes

Archwood Good adds and expands recipes for Archwood construction and processing.

Features include:

- A dedicated **Archwood Crafting Table**.
- Expanded recipes for Orange and White Archwood.
- Recipes for converting regular Archwood wood/log variants into their **Fading Archwood** counterparts using an Amethyst Shard.
- Support for the Archwood colors supplied by Ars Nouveau and Ars Elemental.
- Appropriate tags, loot tables, advancements, and recipe data for the expanded blocks.

## 🌲 Expanded Archwood Forests

Archwood Good expands the Archwood world-generation ecosystem with additional forests and specialized environments.

### Fading Archwood Forest

A darker, decayed Archwood environment featuring:

- Fading Archwood.
- Cobwebs.
- Skulls.
- Wither Roses.
- Dead bushes.
- Cobblestone mounds.
- White Ash particles.
- A hostile atmosphere with undead-heavy spawning.
- **No background music while inside the biome.**
- Soul Sand Valley-style ambient sounds and additions in place of music.

The Fading Forest is intended to feel eerie, abandoned, and unnaturally quiet rather than like a conventional overworld forest.

### Glacial Archwood Caves

A specialized underground Archwood environment featuring:

- Frozen Archwood cave generation.
- Glacial Archwood trees.
- Blue and White Archwood cave growth.
- Ice and packed-ice formations.
- Snow accumulation.
- Frozen waterways and fluids.
- Icy Mage Lights.
- Specialized cave tunnels and caverns.
- Glacial tree replacement and cave-specific generation rules.
- Optimized freezing behavior to prevent excessive or erratic surface-water freezing.

### Orange and White Archwood Forest Generation

Orange and White Archwood receive dedicated world-generation support so they are not limited to player cultivation.

This includes:

- Dedicated Orange and White Archwood forests.
- Dense Orange/White Archwood generation.
- Rare Orange/White Archwood mixtures.
- Natural tree variants that follow the same growth rules as the other Archwood colors.

## 🌳 Giant Archwood Forests

Giant Archwood Trees can be added to Archwood Forest generation through configuration.

- **Enabled by default.**
- Can be disabled through the Archwood Good configuration.
- Uses the expanded Giant Tree generation system.
- Produces larger natural Archwood canopies.
- Supports the full seven-color Archwood family.

## 🏛️ Ancient Archwood Tree

Archwood Good adds the **Ancient Archwood Tree**, a rare landmark structure found in the Archwood Forest ecosystem.

The Ancient Archwood is a massive, multi-trunk Archwood landmark featuring:

- A huge interconnected tree structure.
- Multiple tiers of branches.
- Large roots.
- Mixed Archwood foliage.
- Ars Nouveau and Ars Elemental Archwood colors.
- Magical lighting elements.
- A dedicated structure/world-generation system.

### Ancient Archwood Exclusion Zone

Ancient Archwood Trees have their own protected growing space.

Regular Archwood trees are prevented from generating directly around an Ancient Archwood Tree, preserving its silhouette and giving the area room to breathe.

- The exclusion boundary is **circular**, rather than a square.
- The default buffer is **5 blocks**.
- The exclusion can be disabled through configuration.
- The radius is configurable.
- The system checks the actual Ancient Archwood structure rather than excluding an entire biome.
- Ancient Archwood structure blocks are protected from the associated ground-dressing pass.
- The generation logic is optimized with caching so the structure search is not repeated unnecessarily for every tree-placement attempt.

## ⚙️ World-Generation Optimization

Archwood Good contains a number of optimizations aimed at making its custom world generation behave more efficiently.

Recent optimization work includes:

- Reduced repeated Ancient Archwood structure searches during tree placement.
- Cached Ancient Archwood bounding-box lookups during world-generation passes.
- More efficient exclusion-zone checks.
- Improved Glacial Archwood freezing behavior.
- Reduced unnecessary generation work around specialized Archwood environments.
- More controlled interaction between Archwood tree features and surrounding world generation.

The goal is to keep the expanded Archwood generation without making the mod needlessly expensive during chunk generation.

## 🧩 Ars Nouveau & Ars Elemental Integration

Archwood Good is built specifically as a companion to **Ars Nouveau** and **Ars Elemental**.

It works with the Archwood ecosystem provided by those mods rather than treating its own Orange and White Archwood content as a completely separate wood family.

The mod expands compatibility across the Archwood colors and world-generation systems supplied by both mods, including the additional **Yellow Archwood** provided by Ars Elemental.

This makes Archwood Good especially suited for modpacks centered around Ars Nouveau and its addons.

- [Ars Nouveau](https://github.com/Ars-Nouveau/Ars-Nouveau)
- [Ars Elemental](https://github.com/Alexthw46/Ars-Elemental)

## ⚙️ Configuration

Archwood Good includes configuration options for major world-generation features.

Current configurable features include:

- **Giant Archwood Trees** — enable or disable Giant Archwood Trees in Archwood Forests.
- **Ancient Archwood Exclusion** — enable or disable the exclusion zone around Ancient Archwood Trees.
- **Ancient Archwood Exclusion Radius** — configure the size of the circular buffer around Ancient Archwood Trees.

The default configuration is designed to provide the full expanded Archwood experience.

## 🛠️ Compatibility

Archwood Good is intended to work alongside the Ars Nouveau ecosystem and supports compatibility with other common Minecraft mods where appropriate.

Optional integrations and compatibility support are included for various tools and mods used around the Ars ecosystem, including recipe viewers and configuration tools.

## 📦 Version Information

| Version | Minecraft | Mod Loader |
|---|---|---|
| **1.2.75** | **1.21.1** | **NeoForge** |

## ❤️ Credits

Archwood Good is based on the original **Archwood Good** project by **Alexthw46**.

This project continues that work with additional fixes, compatibility improvements, world-generation changes, new content, optimizations, and quality-of-life improvements.

Special thanks to the developers of:

- [Ars Nouveau](https://github.com/Ars-Nouveau/Ars-Nouveau)
- [Ars Elemental](https://github.com/Alexthw46/Ars-Elemental)

## 📚 Development

This repository contains the source for the Archwood Good 1.21.1 NeoForge project.

For development, issue reports, feature discussion, or contributions, see the repository's GitHub issues and pull requests.

---

**Archwood Good — making the Archwood forests bigger, stranger, and a little more magical.** 🌳✨
