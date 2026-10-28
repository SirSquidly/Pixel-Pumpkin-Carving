package com.sirsquidly.pixel_pumpkin_carving.common;

import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCEntities;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCSounds;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy
{
    public void preInitRegisteries(FMLPreInitializationEvent event)
    {
        PPCEntities.registerTileEntities();
        PPCSounds.registerSounds();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) PPCEntities.RegisterRenderers();
    }

    public void postInitRegisteries(FMLPostInitializationEvent event)
    { ConfigParser.breakupConfigArrays(); }

    @SideOnly(Side.CLIENT)
    public void registerItemRenderer(Item item, int meta, String id){}
}