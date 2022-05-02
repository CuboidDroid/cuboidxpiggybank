package com.cuboiddroid.cuboidxpiggybank.datagen.server;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.setup.ModFluids;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
        super(generatorIn, XPiggyBankMod.MOD_ID, existingFileHelper);
    }

    public static final ResourceLocation EXPERIENCE_TAG_NAME = new ResourceLocation("forge:experience");

    public static final Tags.IOptionalNamedTag<Fluid> EXPERIENCE_TAG = ForgeTagHandler.createOptionalTag(ForgeRegistries.FLUIDS, EXPERIENCE_TAG_NAME);

    @Override
    protected void addTags() {
        // Actually, we're not going to add the forge:experience tag because those all tend to be 20mB per XP
        // and we do not really want to reduce our tank size by a factor of 20!
        // tag(EXPERIENCE_TAG).add(ModFluids.LIQUID_XP.get());
    }
}
