package com.cuboiddroid.cuboidxpiggybank.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IWorldPosCallable;

public class ContainerHelper {
    public static boolean isWithinUsableDistance(IWorldPosCallable worldPos, PlayerEntity player, Block targetBlock) {
        return worldPos.evaluate((level, pos) ->
                level.getBlockState(pos).getBlock() == targetBlock && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

}