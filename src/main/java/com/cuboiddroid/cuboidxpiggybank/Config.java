package com.cuboiddroid.cuboidxpiggybank;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;

    public static final String CATEGORY_XPIGGYBANK = "xpiggybank";

    public static ForgeConfigSpec.IntValue ticksPerCheck;
    public static ForgeConfigSpec.IntValue pickupRadius;
    public static ForgeConfigSpec.BooleanValue pickupEnabled;
    public static ForgeConfigSpec.IntValue internalTankSize;
    public static ForgeConfigSpec.ConfigValue<? extends String> fluidTypes;
    public static ForgeConfigSpec.BooleanValue onlyAcceptMultiples;

    // --- MISC CATEGORY ---
    public static final String CATEGORY_MISC = "misc";
    public static ForgeConfigSpec.BooleanValue verboseLogging;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("XPiggy Bank Settings").push(CATEGORY_XPIGGYBANK);
        setupXPiggyBankConfig(COMMON_BUILDER);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Misc").push(CATEGORY_MISC);
        verboseLogging = COMMON_BUILDER
                .comment(" Logs additional information when loading.")
                .define("verbose_logging", false);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    private static void setupXPiggyBankConfig(ForgeConfigSpec.Builder COMMON_BUILDER) {
        // -- XPiggy Bank --

        ticksPerCheck = COMMON_BUILDER
                .comment(" The number of ticks between checks for XP orbs in the collection area.\n" +
                        " Default: 10 (1/2 a second)")
                .defineInRange("ticks_per_check", 10, 1, 1200);

        pickupEnabled = COMMON_BUILDER
                .comment(" Whether the pickup of XP orbs in the area is enabled. If set to false, then \n" +
                        " the XPiggy Bank can only be used as a tank for converting fluids and storing XP\n" +
                        " through the GUI.")
                .define("pickup_enabled", true);

        pickupRadius = COMMON_BUILDER
                .comment(" The radius around the XPiggy Bank to collect free-floating experience orbs.\n" +
                        " e.g. a radius of 0 would mean a 1x1 area, a radius of 1 would be 3x3, a radius of 2 would be 5x5, etc.\n" +
                        " and the default of 4 is a 9x9 area centred on the XPiggy Bank. The max is 12 (25x25) area.\n" +
                        " For performance reasons, be careful about increasing this too far without also increasing the" +
                        " ticks per check.\n" +
                        " Default: 4")
                .defineInRange("pickup_radius", 7, 0, 12);

        internalTankSize = COMMON_BUILDER
                .comment(" The internal tank size in mB - each mB of Liquid Experience = 1xp.\n\n" +
                        " You should probably just stick to the default of 20000000 (20000 buckets of Liquid Experience, which is about 2125 levels!)")
                .defineInRange("internalTankSize", 20000000, 16000, 21474000);

        fluidTypes = COMMON_BUILDER
                .comment(" A semi-colon separated list of additional fluid types, their rate of conversion from 1000 mB to XP, \n" +
                        " and whether the fluid can be input, output or both.\n\n" +
                        " For example:\n" +
                        "   sophisticatedbackpacks:xp_still=1000:IO;industrialforegoing:essence=10:I;cofh_core:experience=50:IO;minecraft:water=1:O\n" +
                        " This example would mean that: \n" +
                        "   - every 1000mb of Sophisticated Backpack's XP would be equivalent to 50XP (20:1 conversion) and can be input or output\n" +
                        "   - every 1000mb of Industrial Foregoing's Essence would be equivalent to 10XP (100:1 conversion) but can only be input\n" +
                        "   - every 1000mb of COFH Core's Essence of Knowledge  would be equivalent to 50XP (20:1 conversion) and can be input or output\n" +
                        "   - every 1000mb of water would be equivalent to 10XP (1000:1 conversion) but can only be output\n\n" +
                        " Some common XP-like fluids and their recommended rates include:\n" +
                        "   - cofh_core:experience=50 - 'Essence of Knowledge' from Thermal\n" +
                        "   - sophisticatedbackpacks:xp_still=50 - 'Experience' from Sophisticated Backpacks\n" +
                        "   - cyclic:xpjuice=50 - 'Experience' from Cyclic\n" +
                        "   - mob_grinding_utils:fluid_xp=50 - 'Fluid XP' from Mob Grinding Utils\n" +
                        "   - industrialforegoing:essence=10 - 'Essence' from Industrial Foregoing")
                .define("fluids", "cofh_core:experience=50:IO;sophisticatedbackpacks:xp_still=50:IO;cyclic:xpjuice=50:IO;mob_grinding_utils:fluid_xp=50:IO;industrialforegoing:essence=10:IO");

        onlyAcceptMultiples = COMMON_BUILDER
                .comment(" A flag indicating whether to only accept full multiples of the calculated input rate \n" +
                        " for incoming fluids. This will stop the loss of XP due to pipe transfer rates not transferring\n" +
                        " full XP values of fluid at a time, but could mean that not all of a source of incoming fluid is\n" +
                        " transferred.\n\n" +
                        " By default, this should probably be left as 'true', even if sometimes this might mean that\n" +
                        " it is unclear to players why the 'last bit' of a tank is not transferred.\n\n" +
                        " To be clear, if set to false, then there is a good chance resources will be lost. For example,\n " +
                        " if a pipe's transfer rate is 50 mB/tick, then for any of the fluids where 1XP = 20mB, every\n" +
                        " tick the player would lose 1XP because 10 mB would be piped out, but would not equate to a full\n" +
                        " XP in the internal tank, and so would be lost due to rounding.")
                .define("onlyAcceptMultiples", true);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        XPiggyBankMod.LOGGER.debug("Loading config file {}", path);

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        XPiggyBankMod.LOGGER.debug("Built TOML config for {}", path.toString());
        configData.load();
        XPiggyBankMod.LOGGER.debug("Loaded TOML config file {}", path.toString());
        spec.setConfig(configData);
    }
}

