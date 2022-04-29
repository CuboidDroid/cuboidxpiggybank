package com.cuboiddroid.cuboidxpiggybank.compat.jei;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class XPiggyBankJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = XPiggyBankMod.getModId("plugin/main");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }
}
