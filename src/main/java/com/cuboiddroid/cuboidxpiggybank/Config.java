package com.cuboiddroid.cuboidxpiggybank;

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

    // --- JEI CATEGORY ---
    public static final String CATEGORY_JEI = "jei";
    public static ForgeConfigSpec.BooleanValue enableJeiPlugin;
    public static ForgeConfigSpec.BooleanValue enableJeiCatalysts;
    public static ForgeConfigSpec.BooleanValue enableJeiClickArea;

    // --- MISC CATEGORY ---
    public static final String CATEGORY_MISC = "misc";
    public static ForgeConfigSpec.BooleanValue verboseLogging;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("XPiggy Bank Settings").push(CATEGORY_XPIGGYBANK);
        setupXPiggyBankConfig(COMMON_BUILDER);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("JEI Settings").push(CATEGORY_JEI);
        setupJEIConfig(COMMON_BUILDER);
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
                .comment(" The internal tank size in mB - each mB = 1xp.\n" +
                        " This may need to be adjusted based on the conversion rates of the supported fluids. Bear in mind that the\n" +
                        " fluid with the worst conversion rate will determine the sensible maximum for this value, and the maximum is\n" +
                        " therefore actually the MAXINT rounded down to nearest 1000 (2147483000) divided by (1000 / (worst rate)) rounded down.\n" +
                        "\n" +
                        " Effectively, if all rates are 1000 then you could have 2147483000 as the internal tank size, but if a fluid\n" +
                        " has a rate lower than 1000, this needs to be adjusted. For example, if you have 'industrialforegoing:essence=100'\n" +
                        " in your list of fluid types, then the max internal tank size is actually:\n" +
                        "\n" +
                        " 2147483000 / (1000 / 100) = 214748300 Mb (214748.3 buckets)\n" +
                        "\n" +
                        " However - it's probably not a good idea to have something that large anyway, so in all likelihood you should\n" +
                        " stick to the default of 20000000 (20000 buckets of Liquid Experience, which is about 2125 levels!)")
                .defineInRange("internalTankSize", 20000000, 16000, 2147483000);

        fluidTypes = COMMON_BUILDER
                .comment(" A semi-colon separated list of additional fluid types, their rate of conversion from 1000 mB to XP, \n" +
                        " and whether the fluid can be input, output or both.\n\n" +
                        " For example:\n" +
                        "   sophisticatedbackpacks:xp_still=1000:IO;industrialforegoing:essence=100:I;cofh_core:experience=1000:IO;minecraft:water=10:O\n" +
                        " This example would mean that: \n" +
                        "   - every 1000mb of Sophisticated Backpack's XP would be equivalent to 1000XP (1:1 conversion) and can be input or output\n" +
                        "   - every 1000mb of Industrial Foregoing's Essence would be equivalent to 100XP (10:1 conversion) but can only be input\n" +
                        "   - every 1000mb of COFH Core's Experience would be equivalent to 1000XP (1:1 conversion) and can be input or output\n" +
                        "   - every 1000mb of water would be equivalent to 10XP (100:1 conversion) but can only be output")
                .define("fluids", "sophisticatedbackpacks:xp_still=1000:IO;industrialforegoing:essence=100:IO;cofh_core:experience=1000:IO");
    }

    private static void setupJEIConfig(ForgeConfigSpec.Builder COMMON_BUILDER) {
        enableJeiPlugin = COMMON_BUILDER
                .comment(" Enable or disable the JeiPlugin of Cuboid machines.")
                .define("enable_jei", true);

        enableJeiCatalysts = COMMON_BUILDER
                .comment(" Enable or disable the Catalysts in Jei for Cuboid machines.")
                .define("enable_jei_catalysts", true);

        enableJeiClickArea = COMMON_BUILDER
                .comment(" Enable or disable the Click Area inside the GUI in all Cuboid machines.")
                .define("enable_jei_click_area", true);
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

