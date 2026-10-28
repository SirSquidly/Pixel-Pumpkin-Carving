package com.sirsquidly.pixel_pumpkin_carving.common.blocks;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile.TileCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCBlocks;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCItems;
import com.sirsquidly.pixel_pumpkin_carving.init.PPCSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

public class BlockCarvablePumpkin extends BlockHorizontal implements ITileEntityProvider
{
    private final ConfigParser.CarvablePumpkinTypes pumpkinType;

    public BlockCarvablePumpkin(ConfigParser.CarvablePumpkinTypes pumpkinType)
    {
        super(Material.GOURD, MapColor.WHITE_STAINED_HARDENED_CLAY);
        this.pumpkinType = pumpkinType;
        this.setHardness(1.5F);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    public ConfigParser.CarvablePumpkinTypes getPumpkinType() { return pumpkinType; }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    { return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack held = player.getHeldItem(hand);
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileCarvablePumpkin)) return false;
        TileCarvablePumpkin tile = (TileCarvablePumpkin) te;
        EnumFacing facing = state.getValue(FACING);

        ConfigParser.JackOLanternLights light = ConfigParser.getJackOLanternLights(held);
        if (light != null)
        {
            /* Insertions are FRONT ONLY! */
            if (side != facing) return false;
            ItemStack newInsert = held.copy();
            newInsert.setCount(1);

            if (this instanceof BlockCarvableJackOLantern)
            {
                ItemStack oldInsert = tile.getInsertedItem();
                if (!oldInsert.isEmpty())
                {
                    if (ItemStack.areItemsEqual(oldInsert, newInsert) && ItemStack.areItemStackTagsEqual(oldInsert, newInsert))
                        return false;

                    dropInsertedItem(world, pos, state.getValue(FACING), tile);
                }
            }
            else
            {
                if (world.isRemote) return true;
                if (!swapPumpkin(world, pos, state, getMatchingPumpkin(true))) return false;
            }
            TileCarvablePumpkin jackTile = (TileCarvablePumpkin) world.getTileEntity(pos);
            world.playSound(null, pos, PPCSounds.BLOCK_PUMPKIN_ITEM_INSERT, SoundCategory.BLOCKS, 0.5F, (world.rand.nextFloat() * 0.4F) + 0.8F);
            if (world.isRemote) return true;
            jackTile.setInsertedItem(newInsert);
            jackTile.setJackColors(light.jackColorNormal, light.jackColorShadow, light.jackColorInner);
            if (!player.capabilities.isCreativeMode) held.shrink(1);
            player.swingArm(hand);
            world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 3);
            return true;
        }

        boolean uncarve = held.getItem() == Items.PUMPKIN_SEEDS;
        boolean carve = held.getItem() == PPCItems.CARVING_KNIFE;

        if (held.getItem() == Items.DYE && held.getMetadata() == 15)
        {
            if (side != facing || !tile.hasAnyCarvedPixels()) return false;
            world.playSound(null, pos, SoundEvents.BLOCK_WOOD_HIT, SoundCategory.BLOCKS, 1F, 1F);
            spawnParticlesAlongFace(world, pos, facing, state, true);
            if (world.isRemote) return true;
            tile.clearCarving();
            if (!player.capabilities.isCreativeMode) held.shrink(1);
            player.swingArm(hand);
            world.notifyBlockUpdate(pos, state, state, 3);
            tile.restoreOriginalBlockIfEmpty();
            return true;
        }

        if (held.getItem() == Items.SHEARS)
        {
            if (side != facing)
            {
                if (tile.hasAnyCarvedPixels()) return false;

                state = state.withProperty(FACING, side);
                if (!world.setBlockState(pos, state, 3)) return false;
                facing = side;
            }

            world.playSound(null, pos, PPCSounds.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 0.5F, (world.rand.nextFloat() * 0.4F) + 0.8F);
            spawnParticlesAlongFace(world, pos, facing, state, false);
            if (world.isRemote) return true;
            player.swingArm(hand);
            tile.applyVanillaCarving();
            world.notifyBlockUpdate(pos, state, state, 3);
            return true;
        }

        if (!carve && !uncarve || side.getAxis() == EnumFacing.Axis.Y || (uncarve ? !tile.hasAnyCarvedPixels() || side != facing : tile.hasAnyCarvedPixels() && side != facing))
            return false;

        if (carve && side != facing)
        {
            state = state.withProperty(FACING, facing = side);
            if (!world.setBlockState(pos, state, 3)) return false;
            facing = side;
        }

        double a = Math.toRadians(getRotation(facing)), c = Math.cos(a), s = Math.sin(a);
        int x = MathHelper.clamp(MathHelper.floor((c * (hitX - .5) - s * (hitZ - .5) + .5) * 16), 0, 15);
        int y = MathHelper.clamp(MathHelper.floor((1 - hitY) * 16), 0, 15);
        /* If the selected pixel is already the state we are tying to reach, then ignore it!    */
        if (uncarve ? !tile.isCarved(x, y) : tile.isCarved(x, y)) return false;

        world.playSound(null, pos, uncarve ? PPCSounds.BLOCK_PUMPKIN_FILL_PIXEL : PPCSounds.BLOCK_PUMPKIN_CARVE_PIXEL, SoundCategory.BLOCKS, 0.5F, (world.rand.nextFloat() * 0.4F) + 0.8F);

        if (!world.isRemote && world instanceof WorldServer && world.rand.nextBoolean())
        {
            WorldServer serverWorld = (WorldServer) world;
            serverWorld.spawnParticle(EnumParticleTypes.BLOCK_DUST, false, pos.getX() + hitX + side.getXOffset() * 0.01, pos.getY() + hitY + side.getYOffset() * 0.01, pos.getZ() + hitZ + side.getZOffset() * 0.01, 1, 0, 0, (double) 0, 0, Block.getStateId(state));
        }
        if (world.isRemote) return true;

        player.swingArm(hand);
        if (uncarve) tile.uncarvePixel(x, y);
        else tile.carvePixel(x, y);
        world.notifyBlockUpdate(pos, state, state, 3);
        return true;
    }

    public static void spawnParticlesAlongFace(World world, BlockPos pos, EnumFacing facing, IBlockState state, boolean isBonemeal)
    {
        if (world.isRemote || !(world instanceof WorldServer)) return;
        WorldServer serverWorld = (WorldServer) world;

        int runQuantity = isBonemeal ? 8 : 32;

        for (int i = 0; i < runQuantity; i++)
        {
            if (world.rand.nextBoolean()) continue;

            double x = pos.getX() + 0.5 + (facing.getAxis() == EnumFacing.Axis.X ? 0.55 * facing.getXOffset() : (world.rand.nextDouble() - 0.5));
            double y = pos.getY() + 0.5 + (facing.getAxis() == EnumFacing.Axis.Y ? 0.55 * facing.getYOffset() : (world.rand.nextDouble() - 0.5));
            double z = pos.getZ() + 0.5 + (facing.getAxis() == EnumFacing.Axis.Z ? 0.55 * facing.getZOffset() : (world.rand.nextDouble() - 0.5));

            serverWorld.spawnParticle(isBonemeal ? EnumParticleTypes.VILLAGER_HAPPY : EnumParticleTypes.BLOCK_DUST, false, x, y, z, 1, 0, 0, (double) 0, 0, Block.getStateId(state));
        }
    }

    protected void dropInsertedItem(World world, BlockPos pos, EnumFacing facing, TileCarvablePumpkin tile)
    {
        ItemStack stack = tile.getInsertedItem();

        if (!stack.isEmpty()) world.playSound(null, pos, PPCSounds.BLOCK_PUMPKIN_ITEM_REMOVE, SoundCategory.BLOCKS, 0.5F, (world.rand.nextFloat() * 0.4F) + 0.8F);

        if (!world.isRemote && !stack.isEmpty())
        {
            EntityItem seed = new EntityItem(world, pos.getX() + 0.5 + facing.getXOffset() * 0.7, pos.getY() + 0.2, pos.getZ() + 0.5 + facing.getZOffset() * 0.7, stack);
            seed.motionX = facing.getXOffset() * 0.02;
            seed.motionY = 0.02;
            seed.motionZ = facing.getZOffset() * 0.02;
            world.spawnEntity(seed);
        }
    }

    /** Easy way to grab the matching version of this block (The Jack to a Pumpkin, or the Pumpkin to a Jack) */
    @Nullable
    protected BlockCarvablePumpkin getMatchingPumpkin(boolean jackOLantern)
    { return PPCBlocks.getRegisteredPumpkins().stream().filter(t -> t.config == pumpkinType).map(t -> jackOLantern ? t.jackOLantern : t.pumpkin).findFirst().orElse(null); }

    protected boolean swapPumpkin(World world, BlockPos pos, IBlockState state, BlockCarvablePumpkin target)
    {
        if (target == null) return false;

        NBTTagCompound data = world.getTileEntity(pos).writeToNBT(new NBTTagCompound());

        if (!world.setBlockState(pos, target.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3))
            return false;

        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileCarvablePumpkin)) return false;

        tile.readFromNBT(data);
        return true;
    }

    public static float getRotation(EnumFacing facing)
    {
        switch (facing)
        {
            case EAST:  return 90.0F;
            case NORTH: return 180.0F;
            case WEST:  return 270.0F;
            default:    return 0.0F;
        }
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);

        if (te instanceof TileCarvablePumpkin)
        {
            ItemStack itemStack = new ItemStack( Item.getItemFromBlock(this), 1, 0);
            NBTTagCompound tileData = ((TileCarvablePumpkin) te).writeToNBT(new NBTTagCompound());
            itemStack.setTagInfo("BlockEntityTag", tileData);

            spawnAsEntity(worldIn, pos, itemStack);
        }
    }

    /** This allow Pickblock to properly obtain the same facing. */
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        ItemStack itemStack = new ItemStack( Item.getItemFromBlock(this), 1, 0);

        if(state.getBlock().hasTileEntity(state))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileCarvablePumpkin)
            {
                NBTTagCompound tileData = ((TileCarvablePumpkin) te).writeToNBT(new NBTTagCompound());
                itemStack.setTagInfo("BlockEntityTag", tileData);
            }
        }
        return itemStack;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    { return state.withProperty(FACING, rot.rotate(state.getValue(FACING))); }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    { return state.withRotation(mirrorIn.toRotation(state.getValue(FACING))); }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    { return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()); }

    public IBlockState getStateFromMeta(int meta)
    { return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)); }

    public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }

    protected BlockStateContainer createBlockState()  { return new BlockStateContainer(this, FACING); }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileCarvablePumpkin(); }
}