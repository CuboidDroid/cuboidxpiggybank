package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.block;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile.XPiggyBankTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class XPiggyBankBlock extends XPiggyBankBlockBase {

    public XPiggyBankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 3;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new XPiggyBankTileEntity();
    }
}
