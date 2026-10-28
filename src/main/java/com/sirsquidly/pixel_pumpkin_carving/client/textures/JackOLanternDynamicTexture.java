package com.sirsquidly.pixel_pumpkin_carving.client.textures;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class JackOLanternDynamicTexture
{
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    private static final int TRANSPARENT = 0x00000000;

    private final DynamicTexture texture;
    private final int[] pixels;
    private final ResourceLocation resourceLocation;

    public JackOLanternDynamicTexture()
    {
        texture = new DynamicTexture(WIDTH, HEIGHT);
        pixels = texture.getTextureData();

        // Start completely transparent.
        Arrays.fill(pixels, TRANSPARENT);

        resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("pumpkin_carving", texture);
        texture.updateDynamicTexture();
    }

    public void rebuildFromTile(TileCarvablePumpkin tile)
    {
        int carvedColor = tile.hasCustomJackColorsColors() ? tile.getJackCarvedColor() : 0xFFFFE756;
        int shadowColor = tile.hasCustomJackColorsColors() ? tile.getJackShadowColor() : 0xFFFEC842;
        int innerColor = tile.hasCustomJackColorsColors() ? tile.getJackInnerColor() : 0xFFFFFFA1;

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                pixels[x + y * WIDTH] = !tile.isCarved(x, y) ? TRANSPARENT : isShadow(tile, x, y) ? shadowColor : isInnerLighting(tile, x, y) ? innerColor : carvedColor;
            }
        }
        texture.updateDynamicTexture();
    }

    private boolean isShadow(TileCarvablePumpkin tile, int x, int y)
    { return !isCarved(tile, x - 1, y) || !isCarved(tile, x, y - 1); }

    private boolean isInnerLighting(TileCarvablePumpkin tile, int x, int y)
    {
        /* First cube*/
        if (x >= 5 && x <= 11 && (y >= 5 && y <= 11)) return true;
        if (x >= 7 && x <= 9 && (y == 4 || y == 12)) return true;
        if (y >= 7 && y <= 9 && (x == 4 || x == 12)) return true;

        return false;
    }

    private boolean isCarved(TileCarvablePumpkin tile, int x, int y)
    { return x >= 0 && x < 16 && y >= 0 && y < 16 && tile.isCarved(x, y); }

    public void setPixel(int x, int y, int color)
    {
        pixels[x + y * WIDTH] = color;
        texture.updateDynamicTexture();
    }

    public ResourceLocation getResourceLocation() { return resourceLocation; }
}