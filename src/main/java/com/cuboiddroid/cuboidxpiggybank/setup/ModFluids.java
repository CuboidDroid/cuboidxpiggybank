package com.cuboiddroid.cuboidxpiggybank.setup;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

public class ModFluids {
    // this register() is only used to load the class so that the deferred register stuff works
    static void register() {}

    // ---- Liquid XP ----
    public static final ResourceLocation liquidXpStillTexture = XPiggyBankMod.getModId("block/liquid_xp_still");
    public static final ResourceLocation liquidXpFlowingTexture = XPiggyBankMod.getModId("block/liquid_xp_flow");
    public static final ResourceLocation waterOverlay = XPiggyBankMod.getModId("block/liquid_xp_overlay");

    public static final RegistryObject<FlowingFluid> LIQUID_XP
            = Registration.FLUIDS.register("liquid_xp", () -> new ForgeFlowingFluid.Source(ModFluids.LIQUID_XP_PROPERTIES));

    public static final RegistryObject<FlowingFluid> LIQUID_XP_FLOWING
            = Registration.FLUIDS.register("liquid_xp_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.LIQUID_XP_PROPERTIES));

    public static final ForgeFlowingFluid.Properties LIQUID_XP_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> LIQUID_XP.get(),
            () -> LIQUID_XP_FLOWING.get(),
            FluidAttributes.builder(liquidXpStillTexture, liquidXpFlowingTexture)
                    .density(1000)
                    .luminosity(10)
                    .viscosity(1200)
                    .temperature(300)
                    .color(0xfc3bff3b)  // ARGB
                    .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                    .overlay(waterOverlay))
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .bucket(() -> ModItems.LIQUID_XP_BUCKET.get())
            .block(() -> ModBlocks.LIQUID_XP_BLOCK.get());

    // ---- helpers ----

}
