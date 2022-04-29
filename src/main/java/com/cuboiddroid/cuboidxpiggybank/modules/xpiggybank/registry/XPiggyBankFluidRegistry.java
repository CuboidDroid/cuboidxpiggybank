package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry;

import com.cuboiddroid.cuboidxpiggybank.Config;
import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.network.message.SyncXPiggyBankFluidsMessage;
import com.cuboiddroid.cuboidxpiggybank.setup.ModFluids;
import com.cuboiddroid.cuboidxpiggybank.util.XPiggyBankHelpers;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public final class XPiggyBankFluidRegistry {
    private static final XPiggyBankFluidRegistry INSTANCE = new XPiggyBankFluidRegistry();

    private ResourceLocation defaultFluidName;
    private static Map<ResourceLocation, XPiggyBankFluid> fluidMap = new LinkedHashMap<>();

    public ResourceLocation getDefaultFluidName()
    {
        return defaultFluidName;
    }

    public void setDefaultFluidName(ResourceLocation fluidName) throws Exception {
        if (fluidMap.containsKey(fluidName))
            this.defaultFluidName = fluidName;
        else
            throw new Exception("Unable to set default fluid to '" + fluidName + "' - not registered.");
    }

    public Fluid getDefaultAsFluid()
    {
        return XPiggyBankHelpers.getFluid(defaultFluidName);
    }

    public XPiggyBankFluid getXPiggyBankFluid(ResourceLocation id)
    {
        return fluidMap.get(id);
    }

    public ArrayList<XPiggyBankFluid> getXPiggyBankFluids()
    {
        return new ArrayList<>(fluidMap.values());
    }

    public Fluid getAsFluid(ResourceLocation fluid)
    {
        return XPiggyBankHelpers.getFluid(fluid);
    }

    public List<Fluid> getAsFluids()
    {
        if (fluidMap.isEmpty())
            loadFluids();

        List<Fluid> results = new ArrayList<>();

        for (XPiggyBankFluid xpiggyBankFluid : fluidMap.values())
        {
            Fluid fluid = XPiggyBankHelpers.getFluid(xpiggyBankFluid.getId());
            if (!fluid.isSame(Fluids.EMPTY))
                results.add(fluid);
        }

        return Lists.newArrayList(results);
    }

    public void registerFluid(ResourceLocation fluidName, int rate, XPiggyBankDirection direction) throws Exception {
        Fluid f = XPiggyBankHelpers.getFluid(fluidName);

        fluidMap.put(f.getRegistryName(), new XPiggyBankFluid(f.getRegistryName(), rate, direction));
    }

    public boolean isRegisteredForOutput(ResourceLocation id) {
        if (!fluidMap.containsKey(id)) return false;

        XPiggyBankFluid fluid = fluidMap.get(id);
        return fluid.getDirection() == XPiggyBankDirection.BOTH
                || fluid.getDirection() == XPiggyBankDirection.OUTPUT;
    }

    public boolean isRegisteredForInput(ResourceLocation id) {
        if (!fluidMap.containsKey(id)) return false;

        XPiggyBankFluid fluid = fluidMap.get(id);
        return fluid.getDirection() == XPiggyBankDirection.BOTH
                || fluid.getDirection() == XPiggyBankDirection.INPUT;
    }

    public static XPiggyBankFluidRegistry getInstance()
    {
        return INSTANCE;
    }

    public void loadFluids() {
        if (!this.fluidMap.isEmpty())
        {
            XPiggyBankMod.LOGGER.info("{} Fluid Types already loaded", fluidMap.size());
            return;
        }

        if (Config.verboseLogging.get())
            XPiggyBankMod.LOGGER.info("Loading fluid types...");

        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            registerFluid(ModFluids.LIQUID_XP.getId(), 1000, XPiggyBankDirection.BOTH);
        } catch (Exception exception) {
            XPiggyBankMod.LOGGER.error("Error registering Liquid Experience! This ain't gonna work!\n{}", exception);
        }

        // get list of additional fluid(s) from config, which is in a list of fluid
        // resource location and relative rate/weight - e.g.
        //   industrialforegoing:essence=100;mobgrindingutils:experience=500
        String[] fluidTypesAndRates = Config.fluidTypes.get().split(";");

        for (String fluidTypeAndRate : fluidTypesAndRates)
        {
            String[] typeAndRate = fluidTypeAndRate.split("=");

            if (typeAndRate.length != 2) {
                XPiggyBankMod.LOGGER.warn("'{}' is an invalid fluid specification. Must be in 'mod:location=rate:direction' format. e.g. 'industrialforegoing:essence=100:IO'");
            } else {
                try {
                    String[] rateAndDirection = typeAndRate[1].split(":");
                    if (rateAndDirection.length != 2)
                    {
                        XPiggyBankMod.LOGGER.warn("'{}' is an invalid fluid specification. Must be in 'mod:location=rate:direction' format. e.g. 'industrialforegoing:essence=100:IO'");
                    } else if (!(rateAndDirection[1].equalsIgnoreCase("IO") || rateAndDirection[1].equalsIgnoreCase("I") || rateAndDirection[1].equalsIgnoreCase("O"))) {
                        XPiggyBankMod.LOGGER.warn("'{}' is an invalid fluid specification. Must be in 'mod:location=rate:direction' format and direction must be IO, I or O. e.g. 'industrialforegoing:essence=100:IO'");
                    }

                    XPiggyBankDirection dir = rateAndDirection[1].equalsIgnoreCase("IO")
                            ? XPiggyBankDirection.BOTH
                            : (
                                    rateAndDirection[1].equalsIgnoreCase("I")
                                    ? XPiggyBankDirection.INPUT
                                    : XPiggyBankDirection.OUTPUT
                              );

                    registerFluid(new ResourceLocation(typeAndRate[0]), Integer.parseInt(rateAndDirection[0]), dir);
                } catch (Exception exception) {
                    XPiggyBankMod.LOGGER.info("Error registering fluid '{}'.\n{}", fluidTypeAndRate, exception);
                }
            }
        }

        stopwatch.stop();
        XPiggyBankMod.LOGGER.info("Loaded {} Fluid Types.", fluidMap.size());
    }

    // ---> network sync methods...

    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeVarInt(this.fluidMap.size());

        this.fluidMap.forEach((fluidName, xpiggyBankFluid) -> {
            xpiggyBankFluid.write(buffer);
        });
    }

    public List<XPiggyBankFluid> readFromBuffer(PacketBuffer buffer) {
        List<XPiggyBankFluid> fluids = new ArrayList<>();

        int size = buffer.readVarInt();

        for (int i = 0; i < size; i++)
        {
            XPiggyBankFluid fluid = XPiggyBankFluid.read(buffer);

            fluids.add(fluid);
        }

        return fluids;
    }

    public void loadFluids(SyncXPiggyBankFluidsMessage message) {
        Map<ResourceLocation, XPiggyBankFluid> fluids = message.getFluids()
                .stream()
                .collect(Collectors.toMap(XPiggyBankFluid::getId, s -> s));

        this.fluidMap.clear();
        this.fluidMap.putAll(fluids);

        XPiggyBankMod.LOGGER.info("Loaded {} fluids from the server.", fluids.size());
    }
}
