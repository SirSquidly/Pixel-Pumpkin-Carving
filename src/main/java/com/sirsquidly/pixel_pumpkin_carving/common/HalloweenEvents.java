package com.sirsquidly.pixel_pumpkin_carving.common;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvableJackOLantern;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.config.Config;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigCache;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Events setup for Halloween logic specifically.
 * This *could* be packed into `CommonEvents`, but it's a lot of logic, and I prefer it being clean
 */

@Mod.EventBusSubscriber
public class HalloweenEvents
{
    private static final String PROCESSED_TAG = "PPC_HalloweenProcessed";

    private static final String[][] HALLOWEEN_PATTERNS = {
            FacePatterns.VANILLA,
            FacePatterns.CREEPER,
            FacePatterns.SMUG,
            FacePatterns.DROOPY,
            FacePatterns.BAT_EATER,
            FacePatterns.SPIKEY,
            FacePatterns.X_BABY,
            FacePatterns.MOON_CAT,
            FacePatterns.TOBY_TOP_GUY_1,
            FacePatterns.LESKOVETZ_1,
            FacePatterns.MOUSE_PIERCER_1
    };


    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event)
    {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityLivingBase) || !(event.getEntity() instanceof IMob)) return;

        EntityLivingBase entity = (EntityLivingBase) event.getEntity();
        NBTTagCompound data = entity.getEntityData();

        if (data.getBoolean(PROCESSED_TAG)) return;
        data.setBoolean(PROCESSED_TAG, true);

        /* WITHIN Halloween Time. */
        if (!Config.halloween.enable || !isHalloween()) return;

        ItemStack head = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        float roll = entity.getRNG().nextFloat() * 100F;
        /* We REPLACE the vanilla pumpkins during Halloween! */
        if (isOctober31())
        {
            Block block = Block.getBlockFromItem(head.getItem());

            if (block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN)
            { entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY); }

            if (roll < Config.halloween.halloweenDayPumpkinChance)
            {
                if (equipPumpkin(entity, false))
                    ((EntityLiving) entity).setDropChance(EntityEquipmentSlot.HEAD, 0F);
            }
            else if (roll < Config.halloween.halloweenDayPumpkinChance + Config.halloween.halloweenDayJackOLanternChance)
            {
                if (equipPumpkin(entity, true))
                    ((EntityLiving) entity).setDropChance(EntityEquipmentSlot.HEAD, 0F);
            }
        }
        else
        {
            if (!head.isEmpty()) return;

            if (roll < Config.halloween.pumpkinChance)
            {
                if (equipPumpkin(entity, false))
                    ((EntityLiving) entity).setDropChance(EntityEquipmentSlot.HEAD, 0F);
            }
            else if (roll < Config.halloween.pumpkinChance + Config.halloween.jackOLanternChance)
            {
                if (equipPumpkin(entity, true))
                    ((EntityLiving) entity).setDropChance(EntityEquipmentSlot.HEAD, 0F);
            }
        }
    }

    /** OH GOD so much FILTERING. */
    private static boolean equipPumpkin(EntityLivingBase entity, boolean jackOLantern)
    {
        List<ConfigParser.CarvablePumpkinTypes> eligiblePumpkins = ConfigCache.carvablePumpkinTypesList.stream()
                .filter(type -> type.halloweenParticipant)
                .filter(type -> !jackOLantern || !ConfigParser.getAllowedLights(type).isEmpty())
                .collect(Collectors.toList());
        if (eligiblePumpkins.isEmpty()) return false;

        /* After all that filtering, just grab a random compatible one. */
        Random rand = entity.getRNG();
        ConfigParser.CarvablePumpkinTypes config = eligiblePumpkins.get(rand.nextInt(eligiblePumpkins.size()));
        PPCBlocks.RegisteredPumpkinType type = PPCBlocks.getRegisteredPumpkins().stream()
                .filter(p -> p.config.name.equals(config.name))
                .findFirst().orElse(null);

        if (type == null) return false;

        /* Guess who's back to the fucking FILTERING. */
        List<ConfigParser.JackOLanternLights> lights = ConfigParser.getAllowedLights(config).stream()
                .filter(light -> light.halloweenParticipant)
                .collect(Collectors.toList());
        ConfigParser.JackOLanternLights lighting = jackOLantern ? lights.get(rand.nextInt(lights.size())) : null;

        IBlockState baseState = config.sourceBlocks.isEmpty() ? null : config.sourceBlocks.get(0);
        BlockCarvablePumpkin block = jackOLantern ? type.jackOLantern : type.pumpkin;

        entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, getCarvedItem(block, baseState, getRandomPattern(rand), config, lighting));
        return true;
    }

    public static ItemStack getCarvedItem(BlockCarvablePumpkin pumpkin, IBlockState baseState, String[] pattern, ConfigParser.CarvablePumpkinTypes config, @Nullable ConfigParser.JackOLanternLights lighting)
    {
        TileCarvablePumpkin tile = new TileCarvablePumpkin();
        tile.applyCarving(pattern);

        if (baseState != null) tile.setOriginalBlockState(baseState);
        if (config != null) tile.setCarvedColors(config.colorCarved, config.colorCarvedShadow);

        if (pumpkin instanceof BlockCarvableJackOLantern && lighting != null)
        {
            ItemStack inserted = lighting.stack.copy();
            inserted.setCount(1);
            tile.setInsertedItem(inserted);
            tile.setJackColors(lighting.jackColorNormal, lighting.jackColorShadow, lighting.jackColorInner);
        }

        ItemStack stack = new ItemStack(pumpkin);
        stack.setTagInfo("BlockEntityTag", tile.writeToNBT(new NBTTagCompound()));
        return stack;
    }

    private static String[] getRandomPattern(Random rand)
    { return HALLOWEEN_PATTERNS[rand.nextInt(HALLOWEEN_PATTERNS.length)]; }

    private static boolean isHalloween()
    {
        Calendar today = Calendar.getInstance();
        Calendar halloween = (Calendar) today.clone();
        halloween.set(Calendar.MONTH, Calendar.OCTOBER);
        halloween.set(Calendar.DAY_OF_MONTH, 31);
        return Math.abs(today.get(Calendar.DAY_OF_YEAR) - halloween.get(Calendar.DAY_OF_YEAR)) <= Math.max(0, Config.halloween.halloweenWindow);
    }

    private static boolean isOctober31()
    {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.MONTH) == Calendar.OCTOBER && today.get(Calendar.DAY_OF_MONTH) == 31;
    }
}