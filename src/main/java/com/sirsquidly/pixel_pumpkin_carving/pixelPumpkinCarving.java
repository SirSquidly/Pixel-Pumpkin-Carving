package com.sirsquidly.pixel_pumpkin_carving;

import com.sirsquidly.pixel_pumpkin_carving.common.CommonProxy;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = pixelPumpkinCarving.MOD_ID, name = pixelPumpkinCarving.NAME, version = pixelPumpkinCarving.VERSION, dependencies = pixelPumpkinCarving.DEPENDENCIES)
public class pixelPumpkinCarving {
    public static final String MOD_ID = "pixel_pumpkin_carving";
    public static final String NAME = "Pixel Pumpkin Carving";
    public static final String CONFIG_NAME = "pixel_pumpkin_carving";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "";
    public static final String CLIENT_PROXY_CLASS = "com.sirsquidly.pixel_pumpkin_carving.client.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.sirsquidly.pixel_pumpkin_carving.common.CommonProxy";
    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance
    public static pixelPumpkinCarving instance;

    @SidedProxy(clientSide = pixelPumpkinCarving.CLIENT_PROXY_CLASS, serverSide = pixelPumpkinCarving.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    { proxy.preInitRegisteries(event); }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    { proxy.postInitRegisteries(event); }

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {}
}
