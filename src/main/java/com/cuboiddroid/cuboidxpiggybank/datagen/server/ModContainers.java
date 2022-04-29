package com.cuboiddroid.cuboidxpiggybank.datagen.server;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.inventory.XPiggyBankContainer;
import com.cuboiddroid.cuboidxpiggybank.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ModContainers {
    static void register() {
    }

    // XPiggy Bank
    public static final RegistryObject<ContainerType<XPiggyBankContainer>> XPIGGYBANK =
            Registration.CONTAINERS.register("xpiggybank", () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();  // possibly level instead?
                return new XPiggyBankContainer(windowId, world, pos, inv, inv.player);
            }));
}