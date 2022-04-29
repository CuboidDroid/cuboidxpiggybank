package com.cuboiddroid.cuboidxpiggybank.network.message;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluid;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluidRegistry;
import com.cuboiddroid.cuboidxpiggybank.network.NetworkHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class SyncXPiggyBankFluidsMessage implements IntSupplier {
    private List<XPiggyBankFluid> fluids = new ArrayList<>();
    private int loginIndex;

    public SyncXPiggyBankFluidsMessage() {}

    @Override
    public int getAsInt() {
        return this.loginIndex;
    }

    public int getLoginIndex() { return this.loginIndex; }

    public void setLoginIndex(int loginIndex) { this.loginIndex = loginIndex; }

    public List<XPiggyBankFluid> getFluids() { return this.fluids; }

    public static SyncXPiggyBankFluidsMessage read(PacketBuffer buffer) {
        SyncXPiggyBankFluidsMessage message = new SyncXPiggyBankFluidsMessage();

        message.fluids = XPiggyBankFluidRegistry.getInstance().readFromBuffer(buffer);

        return message;
    }

    public static void write(SyncXPiggyBankFluidsMessage message, PacketBuffer buffer) {
        XPiggyBankFluidRegistry.getInstance().writeToBuffer(buffer);
    }

    public static void onMessage(SyncXPiggyBankFluidsMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            XPiggyBankFluidRegistry.getInstance().loadFluids(message);

            NetworkHandler.INSTANCE.reply(new AcknowledgeMessage(), context.get());
        });

        context.get().setPacketHandled(true);
    }
}
