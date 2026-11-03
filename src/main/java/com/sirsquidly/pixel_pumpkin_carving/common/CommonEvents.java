package com.sirsquidly.pixel_pumpkin_carving.common;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCBlocks;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCItems;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonEvents
{
    /** This does the block conversions for the carvable blocks. */
    @SubscribeEvent
    public static void onPumpkinClicked(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);

        //if (world.isRemote) return;
        if (event.getEntityPlayer().getHeldItem(event.getHand()).getItem() != PPCItems.CARVING_KNIFE) return;

        PPCBlocks.RegisteredPumpkinType selected = null;
        ConfigParser.CarvablePumpkinTypes selectedConfig = null;

        for (PPCBlocks.RegisteredPumpkinType type : PPCBlocks.getRegisteredPumpkins())
        {
            ConfigParser.CarvablePumpkinTypes config = ConfigParser.getPumpkinConfig(type.config.name);

            if (config != null && config.sourceBlocks.contains(state))
            {
                selected = type;
                selectedConfig = config;
                break;
            }
        }
        if (selected == null || selectedConfig == null) return;

        EnumFacing facing = EnumFacing.NORTH;
        if (state.getBlock() instanceof BlockHorizontal) facing = state.getValue(BlockPumpkin.FACING);

        IBlockState carvableState = selected.pumpkin.getDefaultState().withProperty(BlockCarvablePumpkin.FACING, facing);
        if (!world.setBlockState(pos, carvableState, 3)) return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileCarvablePumpkin)
        {
            ((TileCarvablePumpkin) te).setOriginalBlockState(state);
            ((TileCarvablePumpkin) te).setCarvedColors(selectedConfig.colorCarved, selectedConfig.colorCarvedShadow);
        }

        Vec3d hit = event.getHitVec();

        if (hit != null)
        {
            double hitX = hit.x - pos.getX();
            double hitY = hit.y - pos.getY();
            double hitZ = hit.z - pos.getZ();

            selected.pumpkin.onBlockActivated(world, pos, carvableState, event.getEntityPlayer(), event.getHand(),
                    event.getFace(), (float) hitX, (float) hitY, (float) hitZ);
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    /** Testing feature, CHANGE LATER! */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        EntityPlayer player = event.getEntityPlayer();
        Entity target = event.getTarget();
        ItemStack held = event.getItemStack();

        if (target instanceof EntitySnowman && !held.isEmpty())
        {
            EntitySnowman snow = (EntitySnowman)target;

            if (snow.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty())
            {
                if (!player.world.isRemote)
                {
                    snow.setPumpkinEquipped(false);

                    ItemStack head = held.copy();
                    head.setCount(1);
                    snow.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
                    snow.setDropChance(EntityEquipmentSlot.HEAD, 100);
                    if (!player.capabilities.isCreativeMode) held.shrink(1);
                }

                event.setCanceled(true);
            }
            else if (held.getItem() instanceof ItemShears && !snow.isPumpkinEquipped())
            {
                player.swingArm(event.getHand());
                if (player.world.isRemote) return;
                snow.entityDropItem(snow.getItemStackFromSlot(EntityEquipmentSlot.HEAD).copy(), 1);
                snow.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                if (!player.capabilities.isCreativeMode) held.damageItem(1, player);
                event.setCanceled(true);
            }
        }
    }
}