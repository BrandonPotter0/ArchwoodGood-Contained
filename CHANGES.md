## 1.2.56 grindstone
- Removed the shaped recipe for `archwood_good:archwood_crafting_table`.
- Grindstone converts blue, red, green, purple, yellow, orange, and white archwood logs into fading archwood logs, and the matching wood blocks into fading archwood wood. Stripped stays stripped. One slot filled, the other empty. 2 experience.


## Ancient Archwood Tree
- Structure `archwood_good:ancient_archwood_tree`: a 73×80×73 braided landmark, 64 blocks from the soil line to the crown.
- Seven trunks twist around each other: blue, red, purple, and green (Ars Nouveau), yellow (Ars Elemental), white and orange (Archwood Good).
- Leaves stay with their log color. Huge humps and grooves on the trunk, three tiers of branches, buttress roots.
- Orange and white leaves are placed `persistent=true` so they do not decay (they are MagicLeaves / LeavesBlock; the blockstate file just has no model variants).
- Ars Nouveau magelights (`ars_nouveau:light_block`, level 15) sit in the grooves, along the branches, and under the crown, tinted to the nearest wood.
- Extremely rare surface structure, only in `ars_nouveau:archwood_forest` (spacing 320, separation 200). Piece origin is 16 blocks below the heightmap so the roots bury.
- Place by hand: `/function archwood_good:place_ancient_archwood_tree` or `/place template archwood_good:ancient_archwood_tree`.

# Archwood Good 1.21.1-1.0.3 — source changes

Built on https://github.com/Alexthw46/ArchwoodGood (1.21.1 branch).

## Worldgen
- Orange (Dawning) and White (Blinding) Archwood trees generate in Ars Nouveau Archwood Forest biomes.
- JSON configured/placed features plus a NeoForge biome modifier targeting `#ars_nouveau:archwood_biome`.
- Java: `AWGConfiguredFeatures`, `AWGPlacedFeatures`.

## Barrels
- One barrel per wood type (blue/red/green/purple/fading in `ars_nouveau`, orange/white in `archwood_good`, yellow in `ars_elemental`).
- Vanilla `BarrelBlock` + `BlockEntityType.BARREL` valid-block extension in `AWGModSetup`.
- Blockstates use vanilla lid rotations (`x: 90` on horizontal facing) so the lid follows placement direction.

## Archwood Crafting Table
- Registered as `archwood_good:archwood_crafting_table`.
- `ArchwoodCraftingTableBlock` opens a `CraftingMenu` subclass whose `stillValid` checks this block instead of `Blocks.CRAFTING_TABLE` (vanilla menu would close instantly otherwise).

## Build
- `neoForge { enable { disableRecompilation = true } }` avoids Vineflower decompile OOM on low-RAM machines.
- Version `1.0.3` in `gradle.properties`.

Compile: `./gradlew build` (NeoForge, Minecraft 1.21.1).

## 1.0.4 — Dawning Forest
- New overworld biome `archwood_good:dawning_forest`.
- Dense Dawning (orange) Archwood trees.
- Orange grass / foliage tint (`grass_color` 14251700, `foliage_color` 12864538).
- Cobblestone boulder mounds (vanilla forest-rock feature, extra count), same idea as Ars Nouveau mossy-stone piles.
- TerraBlender region (weight 2) replaces savanna climate cells in that region slice.
- Tagged `#minecraft:is_forest`, `#minecraft:is_overworld`, `#ars_nouveau:archwood_biome`.
- Locate with `/locate biome archwood_good:dawning_forest`. Requires a new world (or new chunks).
## 1.0.6 — Blinding Forest
- New biome archwood_good:blinding_forest with dense white Archwood only.
- Grey grass/foliage, pale fog, white-ash mist particles.
- TerraBlender region weight 2 replaces birch forest climate cells.
- Not in #ars_nouveau:archwood_biome, so orange trees are not injected.

- Renamed biome display names to Dawning Archwood Forest and Blinding Archwood Forest (IDs unchanged).
## 1.0.8 — Glacial Archwood Caves
- New cave biome archwood_good:glacial_archwood_caves.
- White Archwood + blue Cascading Archwood trees on cave floors.
- Packed ice / blue ice / snow replacing stone, ice spikes, snow piles, freeze layer.
- White and blue Ars Nouveau mage lights (icy_mage_light feature).
- Snowflake particles, icy fog. Replaces dripstone-cave climate cells (TB weight 3).
- Locate: /locate biome archwood_good:glacial_archwood_caves

- Cave trees now use vegetation patches that lay dirt on ice/stone, then grow white/blue Archwood with force_dirt.
- Removed snowflake biome particles (they spawned underwater).

- Larger underground rooms via air-pocket ores. Trees use 1-block dirt pads + extra floor scans. Less dirt, more trees.

- Blinding Archwood Forest sky/fog now use Pale Garden colors (#B9B9B9 / #817770).

- Added lang for Faded Archwood Log / Fading Archwood Leaves (and stripped/wood variants).

- Renamed Fading Archwood Leaves to Faded Archwood Leaves.
- Giant 2x2 sapling trees (4x height, 2x width) for all Archwood colors.

- Giant trees ~half height (~18-24), more irregular branches, more archfruit on trunk and limbs.

- Giant trees use original MagicTrunkPlacer pattern (2x2 trunk, same branches) at 2x height (18).

- Giant trees now same height as normal Archwood (base 9); 2x2 still required.

- Giant trees: height 12, 3x3 trunk (+1 width), original Archwood branch/canopy pattern.

- Giant trunks lean, skip corners, and sprout face bulges instead of a straight 3x3 chimney.

- Dawning Archwood Forest grass/sky/water match Dappled Forest (#DF6827 grass, #7CA3FF sky).

- Archwood Keep is now a rare worldgen structure in Archwood / Dawning / Blinding forests (mansion-like spacing).

- Archwood Keep only generates in ars_nouveau:archwood_forest.

- Keep courtyard includes Flashing tree; wings are two-storey and furnished; chests spaced on walls.

- Fixed worldgen feature-order cycle with Ars Elemental by matching AN feature order.

- Fixed empty Archwood Keep: structure NBT list compounds were encoded incorrectly.

- Keep rooms: solid two floors, real stairwells, windows only in walls, AN decor; towers no longer eat wing floors.

