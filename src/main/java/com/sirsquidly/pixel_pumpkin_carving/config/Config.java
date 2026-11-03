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
    public static String configVersion = "1.1.0";

    @net.minecraftforge.common.config.Config.RequiresMcRestart
    @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.pumpkinTypeDefinitions")
    @net.minecraftforge.common.config.Config.Comment({
            "Registers a carvable pumpkin and jack o lantern.",
            "Formatted as \"[Name]|[Property]=[Value]|[Property]=[Value]|...\"",
            "",
            "[Name] - The ID of the registered pumpkin type block IDs (appended with '_carvable' AND '_carvable_lit').",
            "",
            "Properties can be arranged in any order:",
            "[baseColor] - The base color of any carved (open) parts of the pumpkin. Supports RGB (RRGGBB) or ARGB (AARRGGBB) hex values",
            "[shadowColor] - The color of drop shadows inside the pumpkin. Supports RGB (RRGGBB) or ARGB (AARRGGBB) hex values",
            "[sourceBlocks] - One or more block IDs that convert into this pumpkin when interacted with using the Carving Knife.",
            "[golemSupported] - Whether this pumpkin can be used to construct golems. Default is true.",
            "[halloween] - Whether this pumpkin can appear on mobs during Halloween. Default is true.",
            "[allowedLights] - Item IDs of lighting items that can be inserted. Only items registered in `jackolanternLightingItems` are accepted. Use '*' to allow all registered lighting items. Default is *.",
            "",
            "Example:",
            "pale_pumpkin|baseColor=3C3226|shadowColor=211811|sourceBlocks=palebloom:pale_pumpkin;minecraft:wool:0|golemSupported=false|halloween=true|allowedLights=minecraft:torch;minecraft:redstone_torch"
    })
    public static String[] pumpkinTypeDefinitions = {
            "pumpkin|baseColor=441300|shadowColor=2D0003|sourceBlocks=pixel_pumpkin_carving:pumpkin_uncarved",
            "pale_pumpkin|baseColor=3C3226|shadowColor=211811|sourceBlocks=palebloom:pale_pumpkin|golemSupported=false"};

    @net.minecraftforge.common.config.Config.RequiresMcRestart
    @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.lightingItemDefinitions")
    @net.minecraftforge.common.config.Config.Comment({
            "Adds items that can be inserted into a carved pumpkin to become a jack o lantern.",
            "Formatted as \"[Item]|[Property]=[Value]|[Property]=[Value]|...\"",
            "",
            "[Item] - The registry ID of the lighting item.",
            "",
            "Properties can be arranged in any order:",
            "[baseColor] - The base color of carved (open) parts of the Jack o' Lantern. Supports RGB (RRGGBB) or ARGB (AARRGGBB) hex values.",
            "[shadowColor] - The color of drop shadows inside the Jack o' Lantern. Supports RGB (RRGGBB) or ARGB (AARRGGBB) hex values.",
            "[innerColor] - The color of the center region of the Jack o' Lantern. Supports RGB (RRGGBB) or ARGB (AARRGGBB) hex values.",
            "[halloween] - Whether this lighting item can appear in Jack o' Lanterns on mobs during the Halloween event. Default is true.",
            "",
            "Example:",
            "ee:amber_torch|baseColor=cfae48|shadowColor=bd811a|innerColor=ebd388|halloween=false"
    })
    public static String[] lightingItemDefinitions = {
            "minecraft:torch",
            "minecraft:redstone_torch|baseColor=fd0000|shadowColor=af0000|innerColor=ff4e4e",
            "ancientbeasts:green_torch|baseColor=a3d03c|shadowColor=5a9800|innerColor=edf373|halloween=false",
            "deeperdepths:copper_torch|baseColor=66cc2c|shadowColor=1aa334|innerColor=a6ed4c",
            "ee:amber_torch|baseColor=cfae48|shadowColor=bd811a|innerColor=ebd388|halloween=false",
            "nb:soul_torch|baseColor=11efef|shadowColor=2bb7b7|innerColor=a3fcfa",
            "oe:underwater_torch|baseColor=80ffc0|shadowColor=62c680|innerColor=c5ffde"
    };


    @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween")
    @net.minecraftforge.common.config.Config.Comment("Config related to Halloween")
    public static configHalloween halloween = new configHalloween();

    public static class configHalloween
    {
        @net.minecraftforge.common.config.Config.RequiresMcRestart
        @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween.enable")
        @net.minecraftforge.common.config.Config.Comment("Enables the replacement of vanilla's Halloween pumpkin wearing spawns with this event's version.")
        public boolean enable = true;

        @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween.halloweenWindow")
        @net.minecraftforge.common.config.Config.Comment("The time frame around Halloween that pumpkin spawns will occur.")
        @net.minecraftforge.common.config.Config.RangeInt(min = 0, max = 304)
        public int halloweenWindow = 3;

        @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween.pumpkinChance")
        @net.minecraftforge.common.config.Config.Comment("The chance a mob spawns wearing a Carvable Pumpkin. This selects a random pumpkin type, obeying the `halloweenParticipant` boolean.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0, max = 100)
        public double pumpkinChance = 5.625D;

        @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween.jackOLanternChance")
        @net.minecraftforge.common.config.Config.Comment("The chance a mob spawns wearing a Carvable Jack O' Lantern. This selects a random lighting item, obeying a given pumpkin's `allowed lights` list.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0, max = 100)
        public double jackOLanternChance = 0.833D;

        @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween.halloweenDayPumpkinChance")
        @net.minecraftforge.common.config.Config.Comment("The chance a mob spawns wearing a Carvable Pumpkin on Halloween Day. This selects a random pumpkin type, obeying the `halloweenParticipant` boolean.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0, max = 100)
        public double halloweenDayPumpkinChance = 22.5D;

        @net.minecraftforge.common.config.Config.LangKey("config.pixel_pumpkin_carving.halloween.halloweenDayJackOLanternChance")
        @net.minecraftforge.common.config.Config.Comment("The chance a mob spawns wearing a Carvable Jack O' Lantern on Halloween Day. This selects a random lighting item, obeying a given pumpkin's `allowed lights` list.")
        @net.minecraftforge.common.config.Config.RangeDouble(min = 0, max = 100)
        public double halloweenDayJackOLanternChance = 2.5D;
    }

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