package com.cuboiddroid.cuboidxpiggybank.network;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.network.message.AcknowledgeMessage;
import com.cuboiddroid.cuboidxpiggybank.network.message.SyncXPiggyBankFluidsMessage;
import com.cuboiddroid.cuboidxpiggybank.network.message.UpdateToServerMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NetworkHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(XPiggyBankMod.MOD_ID, "main"), () -> "1.0", (s) -> true, (s) -> true);
    private static int id = 0;

    public static void onCommonSetup() {

        INSTANCE.messageBuilder(SyncXPiggyBankFluidsMessage.class, id())
                .loginIndex(SyncXPiggyBankFluidsMessage::getLoginIndex, SyncXPiggyBankFluidsMessage::setLoginIndex)
                .encoder(SyncXPiggyBankFluidsMessage::write)
                .decoder(SyncXPiggyBankFluidsMessage::read)
                .consumer((message, context) -> {
                    BiConsumer<SyncXPiggyBankFluidsMessage, Supplier<NetworkEvent.Context>> handler;
                    if (context.get().getDirection().getReceptionSide().isServer()) {
                        handler = FMLHandshakeHandler.indexFirst((handshake, msg, ctx) -> SyncXPiggyBankFluidsMessage.onMessage(msg, ctx));
                    } else {
                        handler = SyncXPiggyBankFluidsMessage::onMessage;
                    }

                    handler.accept(message, context);
                })
                .markAsLoginPacket()
                .add();

        INSTANCE.messageBuilder(AcknowledgeMessage.class, id())
                .loginIndex(AcknowledgeMessage::getLoginIndex, AcknowledgeMessage::setLoginIndex)
                .encoder(AcknowledgeMessage::write)
                .decoder(AcknowledgeMessage::read)
                .consumer((message, context) -> {
                    BiConsumer<AcknowledgeMessage, Supplier<NetworkEvent.Context>> handler;
                    if (context.get().getDirection().getReceptionSide().isServer()) {
                        handler = FMLHandshakeHandler.indexFirst((handshake, msg, ctx) -> AcknowledgeMessage.onMessage(msg, ctx));
                    } else {
                        handler = AcknowledgeMessage::onMessage;
                    }

                    handler.accept(message, context);
                })
                .markAsLoginPacket()
                .add();

        INSTANCE.registerMessage(
                id(),
                UpdateToServerMessage.class,
                UpdateToServerMessage::encode,
                UpdateToServerMessage::new,
                UpdateToServerMessage::handle);
    }

    private static int id() {
        return id++;
    }
}
