package com.sirsquidly.pixel_pumpkin_carving.common.blocks;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCarvableJackOLantern extends BlockCarvablePumpkin
{
    public BlockCarvableJackOLantern(ConfigParser.CarvablePumpkinTypes pumpkinType)
    {
        super(pumpkinType);
        this.setHardness(1.5F);
        this.setLightLevel(1.0F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getHeldItem(hand);

        if (!held.getItem().getToolClasses(held).contains("shovel"))
            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileCarvablePumpkin)) return false;
        if (world.isRemote) return true;
        TileCarvablePumpkin tile = (TileCarvablePumpkin) te;
        dropInsertedItem(world, pos, state.getValue(FACING), tile);

        if (!swapPumpkin(world, pos, state, getMatchingPumpkin(false))) return false;
        TileCarvablePumpkin newPumpkin = (TileCarvablePumpkin) world.getTileEntity(pos);
        newPumpkin.setInsertedItem(ItemStack.EMPTY);

        player.swingArm(hand);
        return true;
    }
}