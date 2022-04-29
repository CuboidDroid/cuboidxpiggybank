package com.cuboiddroid.cuboidxpiggybank.setup;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;

import static net.minecraft.item.Items.BUCKET;

public class ModItems {
    // this register() is only used to load the class so that the deferred register stuff works
    static void register() {}

    public static BucketItem liquidXpBucket() {
        return new BucketItem(
                () -> ModFluids.LIQUID_XP.get(),
                new Item.Properties()
                        .tab(XPiggyBankMod.XPIGGYBANK_ITEM_GROUP)
                        .craftRemainder(BUCKET)
                        .stacksTo(1)
                        .rarity(Rarity.UNCOMMON));
    }

    public static final RegistryObject<Item> LIQUID_XP_BUCKET = Registration.ITEMS.register(
            "liquid_xp_bucket",
            () -> liquidXpBucket());
}
