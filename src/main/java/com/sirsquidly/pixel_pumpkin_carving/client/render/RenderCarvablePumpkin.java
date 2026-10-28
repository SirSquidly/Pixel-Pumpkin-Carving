package com.sirsquidly.pixel_pumpkin_carving.client.render;

import com.sirsquidly.pixel_pumpkin_carving.client.textures.JackOLanternDynamicTexture;
import com.sirsquidly.pixel_pumpkin_carving.client.textures.PumpkinDynamicTexture;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvableJackOLantern;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class RenderCarvablePumpkin extends TileEntitySpecialRenderer<TileCarvablePumpkin>
{
    private PumpkinDynamicTexture dynamicTexture;
    private JackOLanternDynamicTexture jackDynamicTexture;

    @Override
    public void render(TileCarvablePumpkin tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (tile.getWorld() == null) return;

        EnumFacing facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockCarvablePumpkin.FACING);
        boolean jack = tile.getWorld().getBlockState(tile.getPos()).getBlock() instanceof BlockCarvableJackOLantern;

        if (jack)
        {
            if (jackDynamicTexture == null) jackDynamicTexture = new JackOLanternDynamicTexture();
            jackDynamicTexture.rebuildFromTile(tile);
        }
        else
        {
            if (dynamicTexture == null) dynamicTexture = new PumpkinDynamicTexture();
            dynamicTexture.rebuildFromTile(tile);
        }

        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.rotate(getRotation(facing), 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(jack ? jackDynamicTexture.getResourceLocation() : dynamicTexture.getResourceLocation());

        int light = jack ? 0x00F000F0 : tile.getWorld().getCombinedLight(tile.getPos().offset(facing), 0);

        int low = light & 0xFFFF;
        int high = (light >> 16) & 0xFFFF;

        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                (float) low,
                (float) high
        );

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        double offset = 1.001;
        buffer.pos(0, 0, offset).tex(0, 1).normal(0, 0, -1).endVertex();
        buffer.pos(offset, 0, offset).tex(1, 1).normal(0, 0, -1).endVertex();
        buffer.pos(offset, offset, offset).tex(1, 0).normal(0, 0, -1).endVertex();
        buffer.pos(0, offset, offset).tex(0, 0).normal(0, 0, -1).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private float getRotation(EnumFacing facing)
    {
        switch (facing)
        {
            case EAST: return 90.0F;
            case NORTH: return 180.0F;
            case WEST: return 270.0F;
            case SOUTH:
            default: return 0.0F;
        }
    }
}