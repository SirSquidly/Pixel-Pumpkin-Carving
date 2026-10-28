package com.sirsquidly.pixel_pumpkin_carving.common.blocks;

import com.sirsquidly.pixel_pumpkin_carving.init.PPCSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUncarvedPumpkin extends Block
{
    public BlockUncarvedPumpkin()
    {
        super(Material.GOURD, MapColor.ADOBE);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = playerIn.getHeldItem(hand);
        Item item = itemstack.getItem();

        if (item instanceof ItemShears)
        {
            EnumFacing front = facing.getAxis().isHorizontal() ? facing : playerIn.getHorizontalFacing().getOpposite();

            worldIn.setBlockState(pos, Blocks.PUMPKIN.getDefaultState().withProperty(BlockHorizontal.FACING, front));
            worldIn.playSound(null, pos, PPCSounds.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (worldIn.isRemote) return true;

            EntityItem seed = new EntityItem(worldIn, pos.getX() + 0.5 + front.getXOffset() * 0.7, pos.getY() + 0.2, pos.getZ() + 0.5 + front.getZOffset() * 0.7, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            seed.motionX = front.getXOffset() * 0.02;
            seed.motionY = 0.02;
            seed.motionZ = front.getZOffset() * 0.02;
            worldIn.spawnEntity(seed);

            return true;
        }
        return false;
    }
}