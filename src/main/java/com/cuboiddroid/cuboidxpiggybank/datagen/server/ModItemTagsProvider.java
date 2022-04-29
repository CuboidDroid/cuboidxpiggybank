package com.cuboiddroid.cuboidxpiggybank.datagen.server;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(DataGenerator generatorIn, BlockTagsProvider blockTagsProvider, ExistingFileHelper existingFileHelper) {
        super(generatorIn, blockTagsProvider, XPiggyBankMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
    }
}