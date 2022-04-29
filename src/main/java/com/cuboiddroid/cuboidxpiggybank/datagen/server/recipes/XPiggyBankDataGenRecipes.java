package com.cuboiddroid.cuboidxpiggybank.datagen.server.recipes;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.datagen.server.ModRecipeProvider;
import com.cuboiddroid.cuboidxpiggybank.setup.ModBlocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class XPiggyBankDataGenRecipes {
    public static void build(ModRecipeProvider provider, Consumer<IFinishedRecipe> consumer)
    {
        // default shaped recipe for making the XPiggy Bank
        ShapedRecipeBuilder.shaped(ModBlocks.XPIGGYBANK.get())
                .define('E', Items.EMERALD)
                .define('D', Items.DIAMOND)
                .define('G', Items.GLOWSTONE)
                .pattern("EDE")
                .pattern("DGD")
                .pattern("EDE")
                .unlockedBy("has_item", provider.hasItem(Items.GLOWSTONE))
                .save(consumer, XPiggyBankMod.getModId("xpiggybank"));
    }
}
