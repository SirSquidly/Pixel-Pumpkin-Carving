package com.sirsquidly.pixel_pumpkin_carving.common;

import com.sirsquidly.pixel_pumpkin_carving.client.textures.PumpkinOverlayDynamicTexture;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCarvablePumpkin extends ItemBlock
{
    private final TileCarvablePumpkin renderTile = new TileCarvablePumpkin();
    private PumpkinOverlayDynamicTexture dynamicTexture;

    public ItemCarvablePumpkin(Block block)
    {
        super(block);
        this.setRegistryName(block.getRegistryName());
        setHasSubtypes(true);
    }

    @Override public int getMetadata(int damage) { return damage; }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return EntityEquipmentSlot.HEAD;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, net.minecraft.client.gui.ScaledResolution resolution, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();

        NBTTagCompound blockEntityTag = stack.getSubCompound("BlockEntityTag");
        renderTile.readFromNBT(blockEntityTag != null ? blockEntityTag : new NBTTagCompound());

        if (dynamicTexture == null) dynamicTexture = new PumpkinOverlayDynamicTexture();
        dynamicTexture.rebuildFromTile(renderTile);
        mc.getTextureManager().bindTexture(dynamicTexture.getResourceLocation());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, resolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(resolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}