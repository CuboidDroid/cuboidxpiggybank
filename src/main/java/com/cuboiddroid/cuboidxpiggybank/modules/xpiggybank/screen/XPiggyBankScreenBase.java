package com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.screen;

import com.cuboiddroid.cuboidxpiggybank.XPiggyBankMod;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.inventory.XPiggyBankContainerBase;
import com.cuboiddroid.cuboidxpiggybank.modules.xpiggybank.tile.XPiggyBankTileEntityBase;
import com.cuboiddroid.cuboidxpiggybank.network.NetworkHandler;
import com.cuboiddroid.cuboidxpiggybank.network.message.UpdateToServerMessage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

import static com.cuboiddroid.cuboidxpiggybank.util.XPiggyBankHelpers.xpToLevels;

@OnlyIn(Dist.CLIENT)
public class XPiggyBankScreenBase<T extends XPiggyBankContainerBase> extends ContainerScreen<T> {

    public static ResourceLocation GUI = XPiggyBankMod.getModId("textures/gui/xpiggybank.png");
    PlayerInventory playerInv;
    ITextComponent name;
    XPiggyBankTileEntityBase tile;
    ITextComponent levelsLabel = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.levels");
    ITextComponent mbLabel = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.mb_stored");
    ITextComponent availableLabel = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.available");
    ITextComponent capacityLabel = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.capacity");
    ITextComponent fluidLabel = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.fluid");

    public XPiggyBankScreenBase(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        playerInv = inv;
        this.name = name;
        this.imageWidth = 183;
        this.imageHeight = 203;
    }

    @Override
    protected void init() {
        super.init();

        this.tile = this.getTileEntity();

        this.setupButtons();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY) {
        String[] words = this.name.getString().split("\\s+");
        String firstLine = words[0] + ((words.length > 1) ? " " + words[1] : "");
        StringBuilder secondLine = new StringBuilder();
        for (int i = 2; i < words.length; i++)
            secondLine.append(words[i]).append(i < words.length - 1 ? " " : "");

        ITextComponent first = new StringTextComponent(firstLine);
        ITextComponent second = new StringTextComponent(secondLine.toString());

        assert this.minecraft != null;
        this.minecraft.font.draw(matrix, first, (this.imageWidth - this.minecraft.font.width(firstLine)) / 2.0f, 6, 4210752);
        this.minecraft.font.draw(matrix, second, (this.imageWidth - this.minecraft.font.width(secondLine.toString())) / 2.0f, 18, 4210752);

        float leftColX = 12.0f;
        float rightColX = leftColX + 4 + 0.8f * Math.max(Math.max(Math.max(minecraft.font.width(levelsLabel), minecraft.font.width(mbLabel)), minecraft.font.width(availableLabel)), minecraft.font.width(capacityLabel));

        // render number of stored levels
        ITextComponent levelsAmount = new StringTextComponent(String.format("%,d", xpToLevels(tile.getXpAmount())));

        renderScaledText(matrix, this.minecraft.font, levelsLabel, leftColX, 136, 0.8f, 4210752);
        renderScaledText(matrix, this.minecraft.font, levelsAmount, rightColX, 136, 0.8f, 4210752);

        // render number of stored mB
        ITextComponent mbAmount = new StringTextComponent(String.format("%,d mB", tile.getFluidAmount()));

        renderScaledText(matrix, this.minecraft.font, mbLabel, leftColX, 146, 0.8f, 4210752);
        renderScaledText(matrix, this.minecraft.font, mbAmount, rightColX, 146, 0.8f, 4210752);

        // render number of available mB
        ITextComponent availableAmount = new StringTextComponent(String.format("%,d mB", tile.getSpace()));

        renderScaledText(matrix, this.minecraft.font, availableLabel, leftColX, 156, 0.8f, 4210752);
        renderScaledText(matrix, this.minecraft.font, availableAmount, rightColX, 156, 0.8f, 4210752);

        // render number of total capacity in mB
        ITextComponent capacityAmount = new StringTextComponent(String.format("%,d mB", tile.getCapacity()));

        renderScaledText(matrix, this.minecraft.font, capacityLabel, leftColX, 166, 0.8f, 4210752);
        renderScaledText(matrix, this.minecraft.font, capacityAmount, rightColX, 166, 0.8f, 4210752);

        // render type of fluid in tank
        ITextComponent fluidType = tile.getFluidDisplayName();

        renderScaledText(matrix, this.minecraft.font, fluidLabel, leftColX, 176, 0.8f, 4210752);
        renderScaledText(matrix, this.minecraft.font, fluidType, leftColX, 186, 0.8f, 4210752);

        ITextComponent retrieve = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.retrieve");
        float retrieveX = 50 - this.minecraft.font.width(retrieve.getString()) / 2.0f;
        renderScaledText(matrix, this.minecraft.font, retrieve, retrieveX, 34, 0.8f, 4210752);

        ITextComponent store = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.store");
        float storeX = 110 - this.minecraft.font.width(store.getString()) / 2.0f;
        renderScaledText(matrix, this.minecraft.font, store, storeX, 34, 0.8f, 4210752);

        ITextComponent value10 = new StringTextComponent("10");
        this.minecraft.font.draw(matrix, value10, 74 - this.minecraft.font.width(value10) / 2.0f, 49, 4210752);

        ITextComponent value100 = new StringTextComponent("100");
        this.minecraft.font.draw(matrix, value100, 74 - this.minecraft.font.width(value100) / 2.0f, 70, 4210752);

        ITextComponent value1000 = new StringTextComponent("1000");
        this.minecraft.font.draw(matrix, value1000, 74 - this.minecraft.font.width(value1000) / 2.0f, 91, 4210752);

        ITextComponent valueAll = new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.all");
        this.minecraft.font.draw(matrix, valueAll, 74 - this.minecraft.font.width(valueAll) / 2.0f, 112, 4210752);
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        // render the main container background
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bind(GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrix, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        // render tank contents
        FluidStack fluidStack = tile.getFluidStack();
        if (!fluidStack.isEmpty()) {
            // not empty, let's have a min of 1 pixel high so it doesn't look empty!
            int scaledHeight = Math.max(1, scaled(tile.getFluidAmount(), tile.getCapacity(), 80));

            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();

            minecraft.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
            int color = fluidStack.getFluid().getAttributes().getColor(fluidStack);
            TextureAtlasSprite textureAtlasSprite = getFluidTexture(fluidStack);

            RenderSystem.color4f(
                    (color >> 16 & 0xff) / 255f,
                    (color >> 8 & 0xff) / 255f,
                    (color & 0xff) / 255f,
                    (color >> 24 & 0xff) / 255f);

            int spriteHeight = textureAtlasSprite.getHeight();
            int spriteWidth = textureAtlasSprite.getWidth();
            int yCount = scaledHeight / spriteHeight;
            int yRemainder = scaledHeight % spriteHeight;

            int xCount = 38 / spriteWidth;
            int xRemainder = 38 % spriteWidth;

            for (int y = 1; y <= yCount; y++) {
                for (int x = 0; x < xCount; x++) {
                    blit(matrix, relX + 128 + x*spriteWidth, relY + 125 - spriteHeight * y, 0, spriteWidth, spriteHeight, textureAtlasSprite);
                }

                blit(matrix, relX + 128 + xCount*spriteWidth, relY + 125 - spriteHeight * y, 0, xRemainder, spriteHeight, textureAtlasSprite);
            }

            int topLineY = relY + 125 - spriteHeight * yCount - yRemainder;
            for (int x = 0; x < xCount; x++) {
                blit(matrix, relX + 128 + x*spriteWidth, topLineY, 0, spriteWidth, yRemainder, textureAtlasSprite);
            }

            blit(matrix, relX + 128 + xCount*spriteWidth, topLineY, 0, xRemainder, yRemainder, textureAtlasSprite);

            RenderSystem.color4f(1, 1, 1, 1);

            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
    }

    private void setupButtons() {
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;

        Button store10 = this.addButton(new Button(relX + 95, relY + 43, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 10, UpdateToServerMessage.RequestType.STORE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.store_10"), mouseX, mouseY)
                ));

        Button store100 = this.addButton(new Button(relX + 95, relY + 64, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 100, UpdateToServerMessage.RequestType.STORE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.store_100"), mouseX, mouseY)
                ));

        Button store1000 = this.addButton(new Button(relX + 95, relY + 85, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 1000, UpdateToServerMessage.RequestType.STORE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.store_1000"), mouseX, mouseY)
                ));

        Button storeAll = this.addButton(new Button(relX + 95, relY + 106, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 0, UpdateToServerMessage.RequestType.STORE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.store_all"), mouseX, mouseY)
                ));

        Button retrieve10 = this.addButton(new Button(relX + 34, relY + 43, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 10, UpdateToServerMessage.RequestType.RETRIEVE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.retrieve_10"), mouseX, mouseY)
                ));

        Button retrieve100 = this.addButton(new Button(relX + 34, relY + 64, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 100, UpdateToServerMessage.RequestType.RETRIEVE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.retrieve_100"), mouseX, mouseY)
                ));

        Button retrieve1000 = this.addButton(new Button(relX + 34, relY + 85, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 1000, UpdateToServerMessage.RequestType.RETRIEVE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.retrieve_1000"), mouseX, mouseY)
                ));

        Button retrieveAll = this.addButton(new Button(relX + 34, relY + 106, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 0, UpdateToServerMessage.RequestType.RETRIEVE)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.retrieve_all"), mouseX, mouseY)
                ));

        Button prevFluid = this.addButton(new Button(relX + 127, relY + 23, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 0, UpdateToServerMessage.RequestType.PREV)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.prev_fluid"), mouseX, mouseY)
                ));

        Button nextFluid = this.addButton(new Button(relX + 147, relY + 23, 20, 19, new StringTextComponent(""),
                (onPress) ->
                    NetworkHandler.INSTANCE.sendToServer(new UpdateToServerMessage(this.tile.getBlockPos(), 0, UpdateToServerMessage.RequestType.NEXT)),
                (button, matrix, mouseX, mouseY) ->
                    renderTooltip(matrix, new TranslationTextComponent("cuboidxpiggybank.container.xpiggybank.btn.next_fluid"), mouseX, mouseY)
                ));

        // hide the buttons from being rendered vanilla style...
        store10.setAlpha(0);
        store100.setAlpha(0);
        store1000.setAlpha(0);
        storeAll.setAlpha(0);
        retrieve10.setAlpha(0);
        retrieve100.setAlpha(0);
        retrieve1000.setAlpha(0);
        retrieveAll.setAlpha(0);
        prevFluid.setAlpha(0);
        nextFluid.setAlpha(0);
    }

    @Nullable
    private XPiggyBankTileEntityBase getTileEntity() {
        ClientWorld world = this.getMinecraft().level;

        if (world != null) {
            TileEntity tile = world.getBlockEntity(this.getMenu().getPos());

            if (tile instanceof XPiggyBankTileEntityBase) {
                return (XPiggyBankTileEntityBase) tile;
            }
        }

        return null;
    }

    private static void renderScaledText(MatrixStack matrix, FontRenderer fontRenderer, ITextComponent text, float x, float y, float scale, int color) {
        matrix.pushPose();
        matrix.scale(scale, scale, scale);
        fontRenderer.draw(matrix, text, x / scale, y / scale, color);
        matrix.popPose();
    }

    public TextureAtlasSprite getFluidTexture(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        ResourceLocation spriteLocation = fluid.getAttributes().getStillTexture();
        return getSprite(spriteLocation);
    }

    public TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        return this.minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(spriteLocation);
    }

    public static int scaled(float value, float maxValue, int height) {
        if (value <= 0 || maxValue <= 0) {
            return 0;
        }
        return (int) (value / maxValue * height);
    }
}
