package com.sirsquidly.pixel_pumpkin_carving.common;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigCache;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCBlocks;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCItems;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.Sys;

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

        for (PPCBlocks.RegisteredPumpkinType type : PPCBlocks.getRegisteredPumpkins())
        {
            ConfigParser.CarvablePumpkinTypes config = null;

            for (ConfigParser.CarvablePumpkinTypes cached : ConfigCache.carvablePumpkinTypesList)
            {
                if (cached.name.equalsIgnoreCase(type.config.name))
                { config = cached; break; }
            }

            if (config == null) continue;
            if (config.sourceBlocks.contains(state))
            { selected = type; break; }
        }

        if (selected == null) return;

        EnumFacing facing = EnumFacing.NORTH;
        if (state.getBlock() instanceof BlockHorizontal) facing = state.getValue(BlockPumpkin.FACING);

        IBlockState carvableState = selected.pumpkin.getDefaultState().withProperty(BlockCarvablePumpkin.FACING, facing);
        if (!world.setBlockState(pos, carvableState, 3)) return;

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileCarvablePumpkin)
        {
            ((TileCarvablePumpkin) te).setOriginalBlockState(state);
            ((TileCarvablePumpkin) te).setCarvedColors(selected.config.colorCarved, selected.config.colorCarvedShadow);
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
}