package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile;

import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluidRegistry;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.registry.XPiggyBankFluid;
import com.cuboiddroid.cuboidxpiggybank.setup.ModFluids;
import com.cuboiddroid.cuboidxpiggybank.util.XPiggyBankHelpers;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class XPiggyBankTank implements IFluidHandler, IFluidTank {

    protected Predicate<FluidStack> validator;
    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    private FluidStack outputFluid;
    private ResourceLocation outputFluidId;

    public XPiggyBankTank(int capacity) {
        this.capacity = capacity;
        this.setValidator(this::fluidValidator);

        // default the output fluid to our own liquid XP
        this.outputFluid = new FluidStack(ModFluids.LIQUID_XP.get(), 1);
        this.outputFluidId = ModFluids.LIQUID_XP.getId();
    }

    private boolean fluidValidator(FluidStack fluidStack)
    {
        return XPiggyBankFluidRegistry.getInstance().isRegisteredForInput(fluidStack.getFluid().getRegistryName());
    }

    public XPiggyBankTank setCapacity(int capacity)
    {
        XPiggyBankFluid f = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluid(outputFluidId);

        this.capacity = outputToInternalAmount(capacity, f.getRate());
        return this;
    }

    private XPiggyBankTank setValidator(Predicate<FluidStack> validator)
    {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidStack stack)
    {
        return validator.test(stack);
    }

    public int getCapacity()
    {
        XPiggyBankFluid f = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluid(outputFluidId);
        return internalToOutputAmount(capacity, f.getRate());
    }

    @Nonnull
    public FluidStack getFluid()
    {
        XPiggyBankFluid f = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluid(outputFluidId);
        Fluid fl = XPiggyBankFluidRegistry.getInstance().getAsFluid(outputFluidId);
        return new FluidStack(fl, internalToOutputAmount(fluid.getAmount(), f.getRate()));
    }

    public int getFluidAmount()
    {
        XPiggyBankFluid f = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluid(outputFluidId);
        return internalToOutputAmount(fluid.getAmount(), f.getRate());
    }

    public int getXpAmount()
    {
        return fluid.getAmount();
    }

    public XPiggyBankTank readFromNBT(CompoundNBT nbt) {
        // TODO - adjust to also store the selected output fluid type
        // TODO - adjust to also store the tank capacity
        CompoundNBT fluidTag = nbt.getCompound("fluid");
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTag);

        CompoundNBT outputTag = nbt.getCompound("output");
        FluidStack output = FluidStack.loadFluidStackFromNBT(outputTag);

        setFluid(fluid);
        setOutputFluid(output);

        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        // TODO - adjust to also store the selected output fluid type
        // TODO - adjust to also store the tank capacity
        CompoundNBT fluidTag = new CompoundNBT();
        fluid.writeToNBT(fluidTag);

        CompoundNBT outputTag = new CompoundNBT();
        outputFluid.writeToNBT(outputTag);

        nbt.put("fluid", fluidTag);
        nbt.put("output", outputTag);

        return nbt;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {

        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }

        int rate = getRate(resource);

        if (action.simulate())
        {
            if (fluid.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }

            int availableSpaceForResource = internalToOutputAmount(capacity - fluid.getAmount(), rate);

            return Math.min(availableSpaceForResource, resource.getAmount());
        }

        int rateAmount = outputToInternalAmount(resource.getAmount(), rate);

        if (fluid.isEmpty())
        {
            // work out the amount as per the defined rate for the incoming fluid
            int consumed = Math.min(capacity, rateAmount);

            // always use our own liquid XP fluid for internal storage
            fluid = new FluidStack(ModFluids.LIQUID_XP.get(), consumed);
            onContentsChanged();

            return internalToOutputAmount(consumed, rate);
        }

        int filled = capacity - fluid.getAmount();

        if (rateAmount < filled)
        {
            fluid.grow(rateAmount);
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(capacity);
            filled = internalToOutputAmount(filled, rate);
        }
        if (filled > 0)
            onContentsChanged();

        return filled;
    }

    public FluidStack drainXp(FluidStack resource, FluidAction action)
    {
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        int rate = getRate(resource);

        FluidStack outputFluidStack = new FluidStack(outputFluid, internalToOutputAmount(fluid.getAmount(), rate));

        if (resource.isEmpty() || !resource.isFluidEqual(outputFluidStack))
        {
            return FluidStack.EMPTY;
        }

        return drain(outputToInternalAmount(resource.getAmount(), rate), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        int drained = maxDrain;
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }

        int rate = getRate(outputFluid);
        FluidStack stack = new FluidStack(outputFluid, internalToOutputAmount(drained, rate));
        if (action.execute() && drained > 0)
        {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    protected void onContentsChanged()
    {

    }

    public void setOutputFluid(FluidStack stack)
    {
        ResourceLocation outputFluidId = stack.getFluid().getRegistryName();
        if (XPiggyBankFluidRegistry.getInstance().isRegisteredForOutput(outputFluidId)) {
            this.outputFluid = stack;
            this.outputFluidId = outputFluidId;
            onContentsChanged();
        }
    }

    public ResourceLocation getOutputFluidId() {
        return this.outputFluidId;
    }

    public void setOutputFluidId(ResourceLocation outputFluidId) {
        if (XPiggyBankFluidRegistry.getInstance().isRegisteredForOutput(outputFluidId)) {
            this.outputFluidId = outputFluidId;
            this.outputFluid = new FluidStack(XPiggyBankHelpers.getFluid(outputFluidId), 1);
            onContentsChanged();
        }
    }

    public void setFluid(FluidStack stack)
    {
        int rate = getRate(stack);

        int amount = outputToInternalAmount(stack.getAmount(), rate);

        // TODO - use our own liquid XP for the internal storage
        this.fluid = new FluidStack(ModFluids.LIQUID_XP.get(), amount);
    }

    private int getRate(FluidStack stack) {
        int rate = 1000;
        if (!stack.isEmpty()) {
            XPiggyBankFluid f = XPiggyBankFluidRegistry.getInstance().getXPiggyBankFluid(stack.getFluid().getRegistryName());
            rate = f.getRate();
        }
        return rate;
    }

    public boolean isEmpty()
    {
        return fluid.isEmpty();
    }

    public int getSpace()
    {
        int rate = getRate(outputFluid);

        return Math.max(0, internalToOutputAmount(capacity - fluid.getAmount(), rate));
    }

    public int getSpaceForXp()
    {
        return Math.max(0, capacity - fluid.getAmount());
    }

    public int internalToOutputAmount(int internalAmount, int rate)
    {
        if (rate == 1000)
            return internalAmount;

        return (int)(internalAmount * (1000.0d / rate));
    }

    public int outputToInternalAmount(int outputAmount, int rate)
    {
        if (rate == 1000)
            return outputAmount;

        return (int)(outputAmount * 1.0d * (rate / 1000.d));
    }
}
