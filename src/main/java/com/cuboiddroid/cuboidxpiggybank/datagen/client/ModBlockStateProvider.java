package com.cuboiddroid.cuboidxpiggybank.datagen.client;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, XPiggyBankMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }
}