package com.sirsquidly.pixel_pumpkin_carving.config;

import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@net.minecraftforge.common.config.Config(modid = pixelPumpkinCarving.MOD_ID, name = pixelPumpkinCarving.CONFIG_NAME)
@net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.title")
@Mod.EventBusSubscriber(modid = pixelPumpkinCarving.MOD_ID)
public class Config
{
    @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.configVersion")
    @net.minecraftforge.common.config.Config.Comment({
            "Config Versions help inform modpack makers/config users if changes have been made to the config between updates. These differ from main versioning, since the config file is static.",
            "Basically, you compare the current default of this value, to the default of when you generated it.",
            "",
            "The versioning follows:",
            "0.0.x - Default values have been slightly adjusted.",
            "0.x.0 - Config options have been added.",
            "x.0.0 - Previous Config Options have been completely overhauled and/or removed. Creating a fresh file is recommended."
    })
    public static String configVersion = "1.0.0";

    @net.minecraftforge.common.config.Config.RequiresMcRestart
    @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.pumpkinTypes")
    @net.minecraftforge.common.config.Config.Comment({
            "Registers a carvable pumpkin and jack o lantern.",
            "Formatted as \"[Name]-[Color]-[Shading Color];[Block];[Block]...\"",
            "Colors use hexidecimal RGB format; RRGGBB",
            "Eight-digit ARGB values are also supported: AARRGGBB",
            "",
            "[Name] - The ID of the item (appended with '_carvable' AND '_carvable_lit').",
            "[Color] - The base color of any carved (open) parts of the pumpkin.",
            "[Shading Color] - The color of drop shadows inside the pumpkin.",
            "[Block] - The ID of blocks that become this pumpkin when interacted with using the Carving Knife."
    })
    public static String[] pumpkinTypes = {
            "pumpkin-441300-2D0003;pixel_pumpkin_carving:pumpkin_uncarved",
            "pale_pumpkin-3C3226-211811;palebloom:pale_pumpkin"};

    @net.minecraftforge.common.config.Config.RequiresMcRestart
    @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.jackolanternLightingItems")
    @net.minecraftforge.common.config.Config.Comment({
            "Adds items that can be inserted into a carved pumpkin to become a jack o lantern.",
            "Formatted as \"[Item]-[Color]-[Shading Color]-[Inner Color]\"",
            "Colors use hexidecimal RGB format; RRGGBB",
            "Eight-digit ARGB values are also supported: AARRGGBB",
            "",
            "[Item] - The ID of the item (appended with '_lapis_rune').",
            "[Color] - The base color of any carved (open) parts of the Jack o' Lantern.",
            "[Shading Color] - The color of drop shadows inside the Jack o' Lantern.",
            "[Inner Color] - The color of the center region of the Jack o' Lantern."
    })
    public static String[] jackolanternLightingItems = {
            "minecraft:torch",
            "minecraft:redstone_torch-fd0000-af0000-ff4e4e",
            "ancientbeasts:green_torch-a3d03c-5a9800-edf373",
            "deeperdepths:copper_torch-66cc2c-1aa334-a6ed4c",
            "ee:amber_torch-cfae48-bd811a-ebd388",
            "nb:soul_torch-11efef-2bb7b7-a3fcfa",
            "oe:underwater_torch-80ffc0-62c680-c5ffde"};


    @Mod.EventBusSubscriber(modid = pixelPumpkinCarving.MOD_ID)
    public static class ConfigSyncHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if(event.getModID().equals(pixelPumpkinCarving.MOD_ID))
            {
                ConfigManager.sync(pixelPumpkinCarving.MOD_ID, net.minecraftforge.common.config.Config.Type.INSTANCE);
                ConfigParser.breakupConfigArrays();
            }
        }
    }
}