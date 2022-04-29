package com.cuboiddroid.cuboidxpiggybank.util;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class XPiggyBankHelpers {
    public static Fluid getFluid(ResourceLocation fluidLocation)
    {
        Set<Map.Entry<RegistryKey<Fluid>, Fluid>> entries = ForgeRegistries.FLUIDS.getEntries();

        Stream<Map.Entry<RegistryKey<Fluid>, Fluid>> entryStream = entries.stream().filter(f -> {
            String regLoc = f.getKey().location().toString();
            String fLoc = fluidLocation.toString();
            return regLoc.equalsIgnoreCase(fLoc);
        });

        Optional<Map.Entry<RegistryKey<Fluid>, Fluid>> first = entryStream.findFirst();

        return first.get().getValue();
    }

    public static Fluid getFluidByName(String fluidName)
    {
        return getFluid(new ResourceLocation(fluidName));
    }

    public static int levelsToXP(int levels){
        if (levels <= 16) {
            return (int) (Math.pow(levels, 2) + 6 * levels);
        } else if (levels <= 31) {
            return (int) (2.5 * Math.pow(levels, 2) - 40.5 * levels + 360);
        } else if (levels >= 32) {
            return (int) (4.5 * Math.pow(levels, 2) - 162.5 * levels + 2220);
        }
        return 0;
    }

    public static int xpToLevels(long xp){
        if (xp < 394) {
            return (int) (Math.sqrt(xp + 9) - 3);
        } else if (xp < 1628) {
            return (int) ((Math.sqrt(40.0d * xp - 7839) + 81) * 0.1d);
        } else if (xp >= 1628) {
            return (int) ((Math.sqrt(72.0d * xp - 54215) + 325) / 18.0d); //when xp >~2980k, breaks int value limit
        }
        return 0;
    }
}
