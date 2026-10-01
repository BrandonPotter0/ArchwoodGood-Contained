package com.alexthw.archwood_good.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Vanilla {@link CraftingMenu} immediately closes unless the block at the
 * access position is {@code Blocks.CRAFTING_TABLE}. This wrapper opens the
 * same 3x3 grid but keeps the menu valid for this custom table.
 */
public class ArchwoodCraftingTableBlock extends CraftingTableBlock {
    private static final Component TITLE = Component.translatable("container.crafting");

    public ArchwoodCraftingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) ->
                new ArchwoodCraftingMenu(id, inventory, ContainerLevelAccess.create(level, pos), this),
                TITLE);
    }

    public static class ArchwoodCraftingMenu extends CraftingMenu {
        private final BlockPos pos;
        private final Block table;

        public ArchwoodCraftingMenu(int id, net.minecraft.world.entity.player.Inventory inventory,
                                    ContainerLevelAccess access, Block table) {
            super(id, inventory, access);
            this.table = table;
            this.pos = access.evaluate((lvl, p) -> p).orElse(BlockPos.ZERO);
        }

        @Override
        public boolean stillValid(Player player) {
            return stillValid(ContainerLevelAccess.create(player.level(), pos), player, table);
        }
    }
}
