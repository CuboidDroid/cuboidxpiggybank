package com.cuboiddroid.cuboidxpiggybank.setup;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluidRegistry;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.screen.XPiggyBankScreen;
import com.cuboiddroid.cuboidxpiggybank.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
    public static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = create(ForgeRegistries.CONTAINERS);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = create(ForgeRegistries.TILE_ENTITIES);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = create(ForgeRegistries.RECIPE_SERIALIZERS);
    public static final DeferredRegister<Fluid> FLUIDS = create(ForgeRegistries.FLUIDS);

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        CONTAINERS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        FLUIDS.register(modEventBus);

        // class-load the registry object holder classes.
        ModBlocks.register();
        ModContainers.register();
        ModItems.register();
        ModRecipeTypes.register();
        ModRecipeSerializers.register();
        ModTileEntities.register();
        ModFluids.register();

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            // Client setup
            //modEventBus.register(new ColorHandler());
        });
    }

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, XPiggyBankMod.MOD_ID);
    }

    @Mod.EventBusSubscriber(modid = XPiggyBankMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Common {
        private Common() {}

        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event)
        {
            event.enqueueWork(() -> {
                NetworkHandler.onCommonSetup();
                XPiggyBankFluidRegistry.getInstance().loadFluids();
            });
        }
    }

    @Mod.EventBusSubscriber(value=Dist.CLIENT, modid = XPiggyBankMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Client {
        private Client() {}

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ScreenManager.register(ModContainers.XPIGGYBANK.get(), XPiggyBankScreen::new);

            event.enqueueWork(() -> {
                RenderTypeLookup.setRenderLayer(ModFluids.LIQUID_XP.get(), RenderType.translucent());
                RenderTypeLookup.setRenderLayer(ModFluids.LIQUID_XP_FLOWING.get(), RenderType.translucent());
                RenderTypeLookup.setRenderLayer(ModBlocks.LIQUID_XP_BLOCK.get(), RenderType.translucent());
            });
        }
    }
}

