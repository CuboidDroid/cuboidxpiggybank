package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class XPiggyBankFluid {
    private final ResourceLocation id;
    private final int rate;

    public XPiggyBankFluid(
            ResourceLocation id,
            int rate) {
        this.id = id;
        this.rate = rate;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getRate() {
        return rate;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.id);
        buffer.writeVarInt(this.rate);
    }

    public static XPiggyBankFluid read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        int rate = buffer.readVarInt();

        return new XPiggyBankFluid(id, rate);
    }
}
