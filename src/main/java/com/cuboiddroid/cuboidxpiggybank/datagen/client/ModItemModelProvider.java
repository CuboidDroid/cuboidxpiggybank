package com.cuboiddroid.cuboidxpiggybank.datagen.client;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, XPiggyBankMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerBlockItemModels();
    }

    private void registerBlockItemModels() {
        withExistingParent("xpiggybank", modLoc("block/xpiggybank"));
    }
}
