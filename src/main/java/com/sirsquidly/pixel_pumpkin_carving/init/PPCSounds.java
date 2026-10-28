package com.sirsquidly.pixel_pumpkin_carving.init;

import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class PPCSounds
{
    private static List<SoundEvent> soundList = new ArrayList<SoundEvent>();

    public static SoundEvent BLOCK_PUMPKIN_CARVE = soundReadyForRegister("block.pumpkin.carve");
    public static SoundEvent BLOCK_PUMPKIN_CARVE_PIXEL = soundReadyForRegister("block.pumpkin.carve_pixel");
    public static SoundEvent BLOCK_PUMPKIN_FILL_PIXEL = soundReadyForRegister("block.pumpkin.fill_pixel");
    public static SoundEvent BLOCK_PUMPKIN_ITEM_INSERT = soundReadyForRegister("block.pumpkin.insert");
    public static SoundEvent BLOCK_PUMPKIN_ITEM_REMOVE = soundReadyForRegister("block.pumpkin.remove");

    public static void registerSounds()
    { for (SoundEvent sounds : soundList) ForgeRegistries.SOUND_EVENTS.register(sounds); }

    private static SoundEvent soundReadyForRegister(String name)
    {
        ResourceLocation location = new ResourceLocation(pixelPumpkinCarving.MOD_ID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(name);
        soundList.add(event);

        return event;
    }
}