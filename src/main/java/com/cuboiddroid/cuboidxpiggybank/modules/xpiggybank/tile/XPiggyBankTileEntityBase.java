package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile;

import com.cuboiddroid.cuboidxpiggybank.Config;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluidRegistry;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluid;
import com.cuboiddroid.cuboidxpiggybank.setup.ModFluids;
import com.cuboiddroid.cuboidxpiggybank.util.XPiggyBankHelpers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public abstract class XPiggyBankTileEntityBase extends TileEntity implements ITickableTileEntity {

    public XPiggyBankTileEntityBase(
            TileEntityType<?> tileEntityType,
            int ticksPerCheck,
            int pickupRadius) {
        super(tileEntityType);

        this.ticksPerCheck = ticksPerCheck;
        this.pickupRadius = pickupRadius;
        this.pickupHeight = Math.min(pickupRadius, 2);
    }

    protected XPiggyBankTank tank = xpiggyBankTank();
    private final LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> tank);
    private final int ticksPerCheck;
    private final int pickupRadius;
    private final int pickupHeight;
    private int ticker = 0;

    private XPiggyBankTank xpiggyBankTank() {
        return new XPiggyBankTank(Config.internalTankSize.get()) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                clientSync();
            }

            @Override
            public void setFluid(FluidStack stack) {
                super.setFluid(stack);
            }

            @Override
            public void setOutputFluid(FluidStack stack) {
                super.setOutputFluid(stack);
            }
        };
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide)
            return;

        ticker++;
        if (ticker < ticksPerCheck)
            return;

        ticker = 0;

        BlockPos cubePos = getBlockPos().immutable();
        TileEntity tile = level.getBlockEntity(cubePos);

        if (!(tile instanceof XPiggyBankTileEntityBase))
            return;

        XPiggyBankTileEntityBase cubeTileEntity = (XPiggyBankTileEntityBase) tile;

        AxisAlignedBB pickupArea = new AxisAlignedBB(
                cubePos.getX() - (double)pickupRadius - 0.5d,
                cubePos.getY() - (double)pickupHeight - 0.5d,
                cubePos.getZ() - (double)pickupRadius - 0.5d,
                cubePos.getX() + (double)pickupRadius + 0.5d,
                cubePos.getY() + (double)pickupHeight + 0.5d,
                cubePos.getZ() + (double)pickupRadius + 0.5d
        );

        List<Entity> list = level.getEntities((Entity) null, pickupArea, this::isExperienceOrb);

        for (Entity e : list)
        {
            if (cubeTileEntity.getSpace() > 0) {
                int amount = ((ExperienceOrbEntity)e).getValue();
                cubeTileEntity.fill(amount);
                e.remove();
            }
        }

        // set lit state if contains any fluid, or set to off if empty
        BlockState blockState = this.level.getBlockState(this.worldPosition);
        if (blockState.getValue(BlockStateProperties.LIT) != tank.getXpAmount() > 0) {
            this.level.setBlock(this.worldPosition, blockState.setValue(BlockStateProperties.LIT, tank.getXpAmount() > 0),
                    Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    private boolean isExperienceOrb(Entity entity)
    {
        return entity instanceof ExperienceOrbEntity;
    }

    public int fill(int amount)
    {
        int fillAmount = tank.fill(new FluidStack(ModFluids.LIQUID_XP.get(), amount), IFluidHandler.FluidAction.EXECUTE);

        if (level != null)
          level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);

        return fillAmount;
    }

    public void drain(int amount)
    {
        tank.drain(new FluidStack(ModFluids.LIQUID_XP.get(), amount), IFluidHandler.FluidAction.EXECUTE);
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    public void drainXp(int amount)
    {
        tank.drainXp(new FluidStack(ModFluids.LIQUID_XP.get(), amount), IFluidHandler.FluidAction.EXECUTE);
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    public void setFluid(int amount)
    {
        tank.setFluid(new FluidStack(FluidStack.EMPTY, amount));
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    public int getFluidAmount()
    {
        return tank.getFluidAmount();
    }

    public int getXpAmount()
    {
        return tank.getXpAmount();
    }

    public int getSpace()
    {
        return tank.getSpace();
    }

    public int getSpaceForXp()
    {
        return tank.getSpaceForXp();
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        tank.readFromNBT(tag);
        ticker = tag.getInt("ticker");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag = super.save(tag);

        tank.writeToNBT(tag);
        tag.putInt("ticker", ticker);

        return tag;
    }

    /**
     * implementing classes should do something like this:
     * <p>
     * return new TranslationTextComponent("cuboidxpiggybank.container.[identifier]");
     *
     * @return the display name
     */
    public abstract ITextComponent getDisplayName();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return handler.cast();

        return super.getCapability(cap, side);
    }

    public abstract Container createContainer(int i, World level, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity);

    /**
     * When the world loads from disk, the server needs to send the TileEntity information to the client
     * It uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
     * getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
     * getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
     *
     * @return the packet
     */
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbtTag = new CompoundNBT();
        this.save(nbtTag);
        this.setChanged();
        return new SUpdateTileEntityPacket(getBlockPos(), -1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (level != null) {
            CompoundNBT tag = pkt.getTag();
            this.load(level.getBlockState(worldPosition), tag);
            this.setChanged();
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition).getBlock().defaultBlockState(), level.getBlockState(worldPosition), 2);
        }
    }

    /* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client
     */
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
     */
    @Override
    public void handleUpdateTag(BlockState blockState, CompoundNBT parentNBTTagCompound) {
        this.load(blockState, parentNBTTagCompound);
    }

    public void clientSync() {
        if (this.level == null || this.level.isClientSide) return;

        ServerWorld world = (ServerWorld) this.getLevel();
        Stream<ServerPlayerEntity> entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false);
        SUpdateTileEntityPacket updatePacket = this.getUpdatePacket();
        if (updatePacket != null)
        entities.forEach(e -> {
            e.connection.send(updatePacket);
        });
    }

    public int getCapacity() {
        return tank.getCapacity();
    }

    public ITextComponent getFluidDisplayName() {
        ResourceLocation outputFluidId = tank.getOutputFluidId();
        return new FluidStack(XPiggyBankHelpers.getFluid(outputFluidId),1).getDisplayName();
    }

    public FluidStack getFluidStack() {
        return tank.getFluid();
    }

    public void nextOutputFluid() {
        ResourceLocation outputFluidId = tank.getOutputFluidId();
        XPiggyBankFluid[] fluids = new XPiggyBankFluid[0];
        fluids = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluids().toArray(fluids);

        int currentIndex = -1;
        for (int i = 0; i < fluids.length; i++)
        {
            if (fluids[i].getId().toString().equalsIgnoreCase(outputFluidId.toString()))
            {
                currentIndex = i;
                break;
            }
        }

        XPiggyBankFluid next;
        if (currentIndex < fluids.length-1)
            next = fluids[currentIndex+1];
        else
            next = fluids[0];

        tank.setOutputFluidId(next.getId());
    }

    public void prevOutputFluid() {
        ResourceLocation outputFluidId = tank.getOutputFluidId();
        XPiggyBankFluid[] fluids = new XPiggyBankFluid[0];
        fluids = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluids().toArray(fluids);

        int currentIndex = -1;
        for (int i = 0; i < fluids.length; i++)
        {
            if (fluids[i].getId().toString().equalsIgnoreCase(outputFluidId.toString()))
            {
                currentIndex = i;
                break;
            }
        }

        XPiggyBankFluid prev;
        if (currentIndex > 0)
            prev = fluids[currentIndex-1];
        else
            prev = fluids[fluids.length-1];

        tank.setOutputFluidId(prev.getId());
    }
}
