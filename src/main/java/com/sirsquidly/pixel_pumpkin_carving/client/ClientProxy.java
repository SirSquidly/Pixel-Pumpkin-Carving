package com.sirsquidly.pixel_pumpkin_carving.client;

import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import com.sirsquidly.pixel_pumpkin_carving.client.render.RenderCarvablePumpkinItem;
import com.sirsquidly.pixel_pumpkin_carving.common.CommonProxy;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = pixelPumpkinCarving.MOD_ID)
public class ClientProxy extends CommonProxy
{
    public void postInitRegisteries(FMLPostInitializationEvent event)
    {
        for (PPCBlocks.RegisteredPumpkinType type : PPCBlocks.getRegisteredPumpkins())
        {
            Item pumpkinItem = Item.getItemFromBlock(type.pumpkin);
            pumpkinItem.setTileEntityItemStackRenderer(new RenderCarvablePumpkinItem());

            Item jackItem = Item.getItemFromBlock(type.jackOLantern);
            jackItem.setTileEntityItemStackRenderer(new RenderCarvablePumpkinItem());
        }

        super.postInitRegisteries(event);
    }

    public void registerItemRenderer(Item item, int meta, String id)
    { ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id)); }
}