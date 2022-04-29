package com.cuboiddroid.cuboidxpiggybank.setup;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.block.XPiggyBankBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {

    // XPiggy Bank
    public static final RegistryObject<XPiggyBankBlock> XPIGGYBANK = register(
            "xpiggybank", () ->
                    new XPiggyBankBlock(AbstractBlock.Properties.of(Material.METAL)
                            .strength(4, 20)
                            .sound(SoundType.METAL)));

    // Liquid XP Block
    public static final RegistryObject<FlowingFluidBlock> LIQUID_XP_BLOCK = registerNoItem(
            "liquid_xp", () ->
                    new FlowingFluidBlock(
                            () -> ModFluids.LIQUID_XP.get(),
                            AbstractBlock.Properties.of(Material.WATER)
                                    .noCollission()
                                    .strength(100f)
                                    .noDrops()));

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    static void register() {
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> blockSupplier) {
        RegistryObject<T> ret = registerNoItem(name, blockSupplier);
        Registration.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(XPiggyBankMod.XPIGGYBANK_ITEM_GROUP)));
        return ret;
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Supplier<Callable<ItemStackTileEntityRenderer>> renderMethod) {
        return register(name, sup, block -> item(block, renderMethod));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        RegistryObject<T> ret = registerNoItem(name, sup);
        Registration.ITEMS.register(name, itemCreator.apply(ret));
        return ret;
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> blockSupplier) {
        return Registration.BLOCKS.register(name, blockSupplier);
    }

    private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block, final Supplier<Callable<ItemStackTileEntityRenderer>> renderMethod) {
        return () -> new BlockItem(block.get(), new Item.Properties().tab(XPiggyBankMod.XPIGGYBANK_ITEM_GROUP).setISTER(renderMethod));
    }
}
