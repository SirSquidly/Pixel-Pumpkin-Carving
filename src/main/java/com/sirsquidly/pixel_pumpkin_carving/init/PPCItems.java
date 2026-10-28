package com.sirsquidly.pixel_pumpkin_carving.init;

import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = pixelPumpkinCarving.MOD_ID)
public class PPCItems
{
    public static final List<Item> itemList = new ArrayList<Item>();
    public static final List<Item> itemModelList = new ArrayList<Item>();

    public static final Item CARVING_KNIFE = new Item().setCreativeTab(CreativeTabs.TOOLS);

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        itemReadyForRegister(CARVING_KNIFE, "carving_knife");

        for (Item items : itemList) event.getRegistry().register(items);
    }

    /** If blocks don't specify the 'addToTab' boolean, assume true.*/
    public static Item itemReadyForRegister(Item item, String name)
    { return itemReadyForRegister(item, name, true);}

    public static Item itemReadyForRegister(Item item, String name, Boolean addNormalModel)
    {
        if (name != null)
        {
            item.setTranslationKey(pixelPumpkinCarving.MOD_ID + "." + name);
            item.setRegistryName(name);
        }
        itemList.add(item);
        if (addNormalModel) itemModelList.add(item);
        return item;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    { for (Item items : itemModelList) pixelPumpkinCarving.proxy.registerItemRenderer(items, 0, "inventory"); }
}