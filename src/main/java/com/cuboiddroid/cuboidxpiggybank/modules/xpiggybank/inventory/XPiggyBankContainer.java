package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.inventory;

import com.cuboiddroid.cuboidxpiggybank.setup.ModBlocks;
import com.cuboiddroid.cuboidxpiggybank.setup.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.cuboiddroid.cuboidxpiggybank.util.ContainerHelper.isWithinUsableDistance;

public class XPiggyBankContainer extends XPiggyBankContainerBase {
    public XPiggyBankContainer(int windowId, World level, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModContainers.XPIGGYBANK.get(), windowId, level, pos, playerInventory, player);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return isWithinUsableDistance(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, ModBlocks.XPIGGYBANK.get());
    }
}
