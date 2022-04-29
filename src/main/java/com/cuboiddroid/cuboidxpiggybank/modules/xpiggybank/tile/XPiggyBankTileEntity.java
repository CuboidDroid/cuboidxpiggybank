package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile;

import com.cuboiddroid.cuboidxpiggybank.Config;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.inventory.XPiggyBankContainer;
import com.cuboiddroid.cuboidxpiggybank.setup.ModTileEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class XPiggyBankTileEntity extends XPiggyBankTileEntityBase {
    public XPiggyBankTileEntity() {
        super(ModTileEntities.XPIGGYBANK.get(),
                Config.ticksPerCheck.get(),
                Config.pickupRadius.get());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank");
    }

    @Override
    public Container createContainer(int i, World level, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new XPiggyBankContainer(i, level, worldPosition, playerInventory, playerEntity);
    }
}
