package com.cuboiddroid.cuboidxpiggybank.setup;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile.XPiggyBankTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModTileEntities {
    public static void register() {
    }

    // Resource Generators

    public static final RegistryObject<TileEntityType<XPiggyBankTileEntity>> XPIGGYBANK = register(
            "xpiggybank", XPiggyBankTileEntity::new, ModBlocks.XPIGGYBANK);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, RegistryObject<? extends Block> block) {
        return Registration.TILE_ENTITIES.register(name, () -> {
            //noinspection ConstantConditions - null in build
            return TileEntityType.Builder.of(factory, block.get()).build(null);
        });
    }
}