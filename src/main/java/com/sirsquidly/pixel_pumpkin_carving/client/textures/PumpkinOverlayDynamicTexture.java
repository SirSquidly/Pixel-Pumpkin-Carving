package com.sirsquidly.pixel_pumpkin_carving.client.textures;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class PumpkinOverlayDynamicTexture
{
    public static final int SCALE = 4;
    public static final int WIDTH = 16 * SCALE;
    public static final int HEIGHT = 16 * SCALE;

    private static final int DARK = 0xFF000000;
    private static final int TRANSPARENT = 0x00000000;

    private final DynamicTexture texture;
    private final int[] pixels;
    private final ResourceLocation resourceLocation;

    public PumpkinOverlayDynamicTexture()
    {
        texture = new DynamicTexture(WIDTH, HEIGHT);
        pixels = texture.getTextureData();

        // Start completely transparent.
        Arrays.fill(pixels, TRANSPARENT);

        resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("pumpkin_carved_overlay", texture);
        texture.updateDynamicTexture();
    }

    public void rebuildFromTile(TileCarvablePumpkin tile)
    {
        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                if (tile.isCarved(x, y)) fillPixel(x, y, TRANSPARENT);
                else fillPixel(x, y, DARK);
            }
        }
        blur();
        texture.updateDynamicTexture();
    }

    private void fillPixel(int x, int y, int color)
    {
        for (int dy = 0; dy < SCALE; dy++)
        {
            for (int dx = 0; dx < SCALE; dx++)
            {
                int tx = x * SCALE + dx;
                int ty = y * SCALE + dy;

                pixels[tx + ty * WIDTH] = color;
            }
        }
    }

    private void blur()
    {
        int[] original = pixels.clone();

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                int alpha = 0;
                int red = 0;
                int green = 0;
                int blue = 0;
                int count = 0;

                for (int dy = -1; dy <= 1; dy++)
                {
                    for (int dx = -1; dx <= 1; dx++)
                    {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) continue;

                        int color = original[nx + ny * WIDTH];

                        alpha += (color >>> 24) & 0xFF;
                        red += (color >>> 16) & 0xFF;
                        green += (color >>> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;
                    }
                }

                pixels[x + y * WIDTH] = ((alpha / count) << 24) | ((red / count) << 16) | ((green / count) << 8) | (blue / count);
            }
        }
    }


    private boolean isShadow(TileCarvablePumpkin tile, int x, int y)
    { return !isCarved(tile, x - 1, y) || (!isCarved(tile, x, y - 1) && !isCarved(tile, x + 1, y)); }

    private boolean isCarved(TileCarvablePumpkin tile, int x, int y)
    { return x >= 0 && x < 16 && y >= 0 && y < 16 && tile.isCarved(x, y); }

    public void setPixel(int x, int y, int color)
    {
        pixels[x + y * WIDTH] = color;
        texture.updateDynamicTexture();
    }

    public ResourceLocation getResourceLocation() { return resourceLocation; }
}