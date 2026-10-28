package com.sirsquidly.pixel_pumpkin_carving.client.textures;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

/**
 * I'll be real, I still can't get over the fact Dynamic Textures are pretty easy to do.
 * */
@SideOnly(Side.CLIENT)
public class PumpkinDynamicTexture
{
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    private static final int TRANSPARENT = 0x00000000;

    private final DynamicTexture texture;
    private final int[] pixels;
    private final ResourceLocation resourceLocation;

    public PumpkinDynamicTexture()
    {
        texture = new DynamicTexture(WIDTH, HEIGHT);
        pixels = texture.getTextureData();

        Arrays.fill(pixels, TRANSPARENT);

        resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("pumpkin_carving", texture);
        texture.updateDynamicTexture();
    }

    public void rebuildFromTile(TileCarvablePumpkin tile)
    {
        int carvedColor = tile.hasCustomCarvedColors() ? tile.getCarvedColor() : 0xFF441300;
        int shadowColor = tile.hasCustomCarvedColors() ? tile.getShadowColor() : 0xFF2D0003;

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                pixels[x + y * WIDTH] = !tile.isCarved(x, y) ? TRANSPARENT : isShadow(tile, x, y) ? shadowColor : carvedColor;
            }
        }
        texture.updateDynamicTexture();
    }

    private boolean isShadow(TileCarvablePumpkin tile, int x, int y)
    { return !isCarved(tile, x - 1, y) || !isCarved(tile, x, y - 1); }

    private boolean isCarved(TileCarvablePumpkin tile, int x, int y)
    { return x >= 0 && x < 16 && y >= 0 && y < 16 && tile.isCarved(x, y); }

    public void setPixel(int x, int y, int color)
    {
        pixels[x + y * WIDTH] = color;
        texture.updateDynamicTexture();
    }

    public ResourceLocation getResourceLocation() { return resourceLocation; }
}