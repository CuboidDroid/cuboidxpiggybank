package com.cuboiddroid.cuboidxpiggybank.network.message;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile.XPiggyBankTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static com.cuboiddroid.cuboidxpiggybank.util.XPiggyBankHelpers.levelsToXP;

public class UpdateToServerMessage {

    public static BlockPos pos;
    public static int xp;
    public RequestType requestType;
    private int playerXp;
    private int finalXp;

    public UpdateToServerMessage(BlockPos pos, int xp, RequestType requestType) {
        this.pos = pos;
        this.xp = xp;
        this.requestType = requestType;
    }

    public enum RequestType {
        STORE,
        RETRIEVE,
        NEXT,
        PREV,
        ACTIVE
    }

    public UpdateToServerMessage(PacketBuffer buffer) {
        pos = buffer.readBlockPos();
        xp = buffer.readVarInt();
        requestType = buffer.readEnum(RequestType.class);
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeVarInt(xp);
        buffer.writeEnum(requestType);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        final AtomicBoolean success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            assert sender != null;
            TileEntity serverEntity = sender.level.getBlockEntity(pos);

            if (serverEntity instanceof XPiggyBankTileEntity) {
                XPiggyBankTileEntity tile = (XPiggyBankTileEntity) serverEntity;

                if (requestType == RequestType.ACTIVE) {
                    tile.setPickupActive(xp != 0);
                }
                else if (requestType == RequestType.NEXT) {
                    // go to the next available fluid type
                    tile.nextOutputFluid();
                }
                else if (requestType == RequestType.PREV) {
                    // go to the previous available fluid type
                    tile.prevOutputFluid();
                }
                else {
                    playerXp = levelsToXP(sender.experienceLevel) + (int) Math.round(sender.experienceProgress * sender.getXpNeededForNextLevel());

                    if (requestType == RequestType.STORE) {
                        // can't store more than the player actually has
                        int xpToStore = Math.min(xp, playerXp);

                        // if xp is zero, then we want to transfer all if possible
                        if (xpToStore == 0 && xp == 0) xpToStore = Math.max(0, playerXp);

                        int spaceForXp = tile.getSpaceForXp();
                        if (spaceForXp == 0) {
                            // no space in tank - do nothing
                        } else if (xpToStore > spaceForXp) {
                            // some room, but not enough
                            sender.giveExperiencePoints(-tile.fill(spaceForXp));
                        } else {
                            sender.giveExperiencePoints(-tile.fill(xpToStore));
                        }
                    } else if (requestType == RequestType.RETRIEVE) {
                        int xpAvailable = tile.getXpAmount();

                        if (xp == 0) {
                            // requested everything!
                            sender.giveExperiencePoints(xpAvailable);
                            tile.drainXp(xpAvailable);
                        } else if (xpAvailable >= xp) {
                            // we have more than asked for, so just give it
                            sender.giveExperiencePoints(xp);
                            tile.drainXp(xp);
                        } else if (xpAvailable < xp) {
                            // we have less than asked for - just give what we have
                            sender.giveExperiencePoints(xpAvailable);
                            tile.drainXp(xpAvailable);
                        }
                    }
                }

                success.set(true);
            }
        });

        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
