package com.cuboiddroid.cuboidxpiggybank.setup;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(XPiggyBankMod.MOD_ID)
@Mod.EventBusSubscriber(modid = XPiggyBankMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRegistration {
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

        // registry.register(new ResourceGeneratingRecipe.Serializer().setRegistryName(CuboidResourceGenMod.MOD_ID, "resource_generating"));
    }
}