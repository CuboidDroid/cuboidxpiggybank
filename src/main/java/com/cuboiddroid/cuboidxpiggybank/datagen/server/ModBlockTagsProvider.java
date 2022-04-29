package com.cuboiddroid.cuboidxpiggybank.datagen.server;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, XPiggyBankMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
    }
}