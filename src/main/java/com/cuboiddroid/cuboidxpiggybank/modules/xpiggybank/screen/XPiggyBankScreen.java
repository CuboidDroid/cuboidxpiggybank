package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.screen;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.inventory.XPiggyBankContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class XPiggyBankScreen extends XPiggyBankScreenBase<XPiggyBankContainer> {
    public XPiggyBankScreen(XPiggyBankContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }
}
