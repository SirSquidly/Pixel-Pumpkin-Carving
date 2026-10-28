package com.sirsquidly.pixel_pumpkin_carving.init;

import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import com.sirsquidly.pixel_pumpkin_carving.client.render.RenderCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PPCEntities
{
    public static int id;

    public static void registerTileEntities()
    { GameRegistry.registerTileEntity(TileCarvablePumpkin.class, new ResourceLocation(pixelPumpkinCarving.MOD_ID, "carvable_pumpkin")); }

    @SideOnly(Side.CLIENT)
    public static void RegisterRenderers()
    { ClientRegistry.bindTileEntitySpecialRenderer(TileCarvablePumpkin.class, new RenderCarvablePumpkin()); }
}