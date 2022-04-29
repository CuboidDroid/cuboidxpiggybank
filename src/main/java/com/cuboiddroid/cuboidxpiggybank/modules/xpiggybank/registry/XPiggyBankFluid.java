package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class XPiggyBankFluid {
    private final ResourceLocation id;
    private final int rate;
    private final XPiggyBankDirection direction;

    public XPiggyBankFluid(
            ResourceLocation id,
            int rate,
            XPiggyBankDirection direction
            ) {
        this.id = id;
        this.rate = rate;
        this.direction = direction;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getRate() {
        return rate;
    }

    public XPiggyBankDirection getDirection() { return direction; }

    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.id);
        buffer.writeVarInt(this.rate);
        buffer.writeEnum(this.direction);
    }

    public static XPiggyBankFluid read(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        int rate = buffer.readVarInt();
        XPiggyBankDirection direction = buffer.readEnum(XPiggyBankDirection.class);

        return new XPiggyBankFluid(id, rate, direction);
    }
}
