package com.cuboiddroid.cuboidxpiggybank;

import com.cuboiddroid.cuboidxpiggybank.setup.ModBlocks;
import com.cuboiddroid.cuboidxpiggybank.setup.Registration;
import com.cuboiddroid.cuboidxpiggybank.util.XPiggyBankPaths;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(XPiggyBankMod.MOD_ID)
public class XPiggyBankMod
{
    public static final String MOD_ID = "cuboidxpiggybank";

    public static final ItemGroup XPIGGYBANK_ITEM_GROUP = (new ItemGroup("xpiggybank") {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.XPIGGYBANK.get());
        }
    });

    public static final Logger LOGGER = LogManager.getLogger();

    public XPiggyBankMod() {
        XPiggyBankPaths.createDirectories();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG, XPiggyBankPaths.MOD_CONFIG.resolve("xpiggybank.toml").toString());

        Registration.register();

        Config.loadConfig(Config.COMMON_CONFIG, XPiggyBankPaths.MOD_CONFIG.resolve("xpiggybank.toml"));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation getModId(String path) {
        return new ResourceLocation(MOD_ID + ":" + path);
    }
}
