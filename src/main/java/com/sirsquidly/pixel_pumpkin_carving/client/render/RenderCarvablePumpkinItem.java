package com.sirsquidly.pixel_pumpkin_carving.client.render;

import com.sirsquidly.pixel_pumpkin_carving.client.textures.JackOLanternDynamicTexture;
import com.sirsquidly.pixel_pumpkin_carving.client.textures.PumpkinDynamicTexture;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvableJackOLantern;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCarvablePumpkinItem extends TileEntityItemStackRenderer
{
    private final TileCarvablePumpkin renderTile = new TileCarvablePumpkin();
    private PumpkinDynamicTexture dynamicTexture;
    private JackOLanternDynamicTexture jackDynamicTexture;

    @Override
    public void renderByItem(ItemStack stack, float partialTicks)
    {
        if (stack.isEmpty()) return;
        Minecraft mc = Minecraft.getMinecraft();
        Block block = ((ItemBlock)stack.getItem()).getBlock();
        boolean jackOLanternMuhahaha = block instanceof BlockCarvableJackOLantern;
        renderTile.readFromNBT(stack.getSubCompound("BlockEntityTag") != null ? stack.getSubCompound("BlockEntityTag") : new NBTTagCompound());

        if (jackOLanternMuhahaha)
        {
            if (jackDynamicTexture == null) jackDynamicTexture = new JackOLanternDynamicTexture();
            jackDynamicTexture.rebuildFromTile(renderTile);
        }
        else
        {
            if (dynamicTexture == null) dynamicTexture = new PumpkinDynamicTexture();
            dynamicTexture.rebuildFromTile(renderTile);
        }

        ResourceLocation carvingTexture = jackOLanternMuhahaha ? jackDynamicTexture.getResourceLocation() : dynamicTexture.getResourceLocation();
        IBlockState state = block.getDefaultState().withProperty(BlockCarvablePumpkin.FACING, EnumFacing.NORTH);
        IBakedModel model = ForgeHooksClient.handleCameraTransforms(mc.getBlockRendererDispatcher().getModelForState(state), ItemCameraTransforms.TransformType.NONE, false);

        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(state, model, 1, 1, 1, 1);
        mc.getTextureManager().bindTexture(carvingTexture);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        if (jackOLanternMuhahaha) OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        double offset = -0.001D;
        buffer.pos(0, 0, offset).tex(0, 1).normal(0, 0, -1).endVertex();
        buffer.pos(0, 1, offset).tex(0, 0).normal(0, 0, -1).endVertex();
        buffer.pos(1, 1, offset).tex(1, 0).normal(0, 0, -1).endVertex();
        buffer.pos(1, 0, offset).tex(1, 1).normal(0, 0, -1).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
    }
}