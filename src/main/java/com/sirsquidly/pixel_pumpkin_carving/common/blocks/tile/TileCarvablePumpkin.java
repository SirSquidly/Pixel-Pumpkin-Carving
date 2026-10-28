package com.sirsquidly.pixel_pumpkin_carving.common.blocks.tile;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileCarvablePumpkin extends TileEntity
{
    private boolean[][] carvedPixels = new boolean[16][16];
    private IBlockState originalBlockState;

    private boolean hasCustomCarvedColors = false;
    private int carvedColor = 0xFFFFFFFF;
    private int shadowColor = 0xFFFFFFFF;
    private ItemStack insertedItem = ItemStack.EMPTY;
    private boolean hasCustomJackColors = false;
    private int jackCarvedColor = 0xFFFFFFFF;
    private int jackShadowColor = 0xFFFFFFFF;
    private int jackInnerColor = 0xFFFFFFFF;


    public void carvePixel(int x, int y)
    {
        if (x < 0 || x >= 16 || y < 0 || y >= 16) return;

        if (carvedPixels[x][y]) return;

        carvedPixels[x][y] = true;
        markDirty();

        if (world != null && !world.isRemote)
        { world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3); }
    }

    public void uncarvePixel(int x, int y)
    {
        if (x < 0 || x >= 16 || y < 0 || y >= 16) return;
        if (!carvedPixels[x][y]) return;

        carvedPixels[x][y] = false;
        markDirty();
        if (world != null && !world.isRemote)
        { world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3); }
    }

    public void clearCarving()
    {
        if (!this.hasAnyCarvedPixels()) return;

        for (int x = 0; x < 16; x++)
        {
            for (int y = 0; y < 16; y++)
            {
                if (carvedPixels[x][y])
                { carvedPixels[x][y] = false; }
            }
        }

        if (world != null && !world.isRemote)
        { world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3); }
    }

    public void applyVanillaCarving()
    {
        String[] face = {
                "................",
                "................",
                "....##....#.....",
                "....###..###....",
                "...####..####...",
                "...####..####...",
                "...####..#####..",
                "....##....####..",
                "................",
                "...#.#..#..#.#..",
                "..############..",
                "..#############.",
                "...##.##.###.##.",
                "...#..#....#....",
                "................",
                "................"
        };

        for (int y = 0; y < 16; y++)
            for (int x = 0; x < 16; x++)
                if (face[y].charAt(x) == '#')
                    carvedPixels[x][y] = true;

        markDirty();
        if (world != null && !world.isRemote) world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public boolean isCarved(int x, int y) { return carvedPixels[x][y]; }

    public boolean hasAnyCarvedPixels()
    {
        for (int x = 0; x < 16; x++)
        {
            for (int y = 0; y < 16; y++)
            { if (carvedPixels[x][y]) return true; }
        }
        return false;
    }

    public void setOriginalBlockState(IBlockState state)
    {
        originalBlockState = state;
        markDirty();
    }

    public boolean restoreOriginalBlockIfEmpty()
    {
        System.out.println("Original state: " + originalBlockState);
        if (hasAnyCarvedPixels() || originalBlockState == null || world == null || world.isRemote) return false;
        return world.setBlockState(pos, originalBlockState, 3);
    }

    public ItemStack getInsertedItem() { return insertedItem.copy(); }

    public void setInsertedItem(ItemStack stack)
    {
        insertedItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        markDirty();

        if (world != null && !world.isRemote) world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    public void setCarvedColors(int carvedColor, int shadowColor)
    {
        this.carvedColor = carvedColor;
        this.shadowColor = shadowColor;
        this.hasCustomCarvedColors = true;

        markDirty();

        if (world != null && !world.isRemote)
        { world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3); }
    }

    public void setJackColors(int carvedColor, int shadowColor, int innerColor)
    {
        this.jackCarvedColor = carvedColor;
        this.jackShadowColor = shadowColor;
        this.jackInnerColor = innerColor;
        this.hasCustomJackColors = true;

        markDirty();

        if (world != null && !world.isRemote)
        { world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3); }
    }

    public boolean hasCustomCarvedColors() { return hasCustomCarvedColors; }
    public int getCarvedColor() { return carvedColor; }
    public int getShadowColor() { return shadowColor; }

    public boolean hasCustomJackColorsColors() { return hasCustomJackColors; }
    public int getJackCarvedColor() { return jackCarvedColor; }
    public int getJackShadowColor() { return jackShadowColor; }
    public int getJackInnerColor() { return jackInnerColor; }

    @Override
    public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    { return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());}

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        if (world != null) world.markBlockRangeForRenderUpdate(pos, pos);
    }

    /** Keeps the tile entity around even if the block gets its state changed. */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    { return !(oldState.getBlock() instanceof BlockCarvablePumpkin && newState.getBlock() instanceof BlockCarvablePumpkin); }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        NBTTagList pixels = new NBTTagList();

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                if (carvedPixels[x][y])
                {
                    NBTTagCompound pixel = new NBTTagCompound();

                    pixel.setByte("X", (byte)x);
                    pixel.setByte("Y", (byte)y);

                    pixels.appendTag(pixel);
                }
            }
        }

        compound.setTag("CarvedPixels", pixels);

        compound.setBoolean("HasCustomCarvedColors", hasCustomCarvedColors);
        if (hasCustomCarvedColors)
        {
            compound.setInteger("CarvedColor", carvedColor);
            compound.setInteger("ShadowColor", shadowColor);
        }

        compound.setBoolean("HasCustomJackColors", hasCustomJackColors);
        if (hasCustomJackColors)
        {
            compound.setInteger("CarvedJackColor", jackCarvedColor);
            compound.setInteger("ShadowJackColor", jackShadowColor);
            compound.setInteger("InnerJackColor", jackInnerColor);
        }

        if (originalBlockState != null)
        {
            ResourceLocation id = Block.REGISTRY.getNameForObject(originalBlockState.getBlock());
            if (id != null)
            {
                compound.setString("OriginalBlock", id.toString());
                compound.setInteger("OriginalMeta", originalBlockState.getBlock().getMetaFromState(originalBlockState));
            }
        }

        if (!insertedItem.isEmpty()) compound.setTag("InsertedItem", insertedItem.writeToNBT(new NBTTagCompound()));
        else compound.removeTag("InsertedItem");

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        carvedPixels = new boolean[16][16];

        NBTTagList pixels = compound.getTagList("CarvedPixels", 10);

        for (int i = 0; i < pixels.tagCount(); i++)
        {
            NBTTagCompound pixel = pixels.getCompoundTagAt(i);

            int x = pixel.getByte("X") & 255;
            int y = pixel.getByte("Y") & 255;

            if (x < 16 && y < 16) carvedPixels[x][y] = true;
        }


        hasCustomCarvedColors = compound.getBoolean("HasCustomCarvedColors");
        if (hasCustomCarvedColors)
        {
            carvedColor = compound.getInteger("CarvedColor");
            shadowColor = compound.getInteger("ShadowColor");
        }

        hasCustomJackColors = compound.getBoolean("HasCustomJackColors");
        if (hasCustomJackColors)
        {
            jackCarvedColor = compound.getInteger("CarvedJackColor");
            jackShadowColor = compound.getInteger("ShadowJackColor");
            jackInnerColor = compound.getInteger("InnerJackColor");
        }

        originalBlockState = null;
        if (compound.hasKey("OriginalBlock", 8))
        {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(compound.getString("OriginalBlock")));
            if (block != Blocks.AIR) originalBlockState = block.getStateFromMeta(compound.getInteger("OriginalMeta"));
        }
        insertedItem = compound.hasKey("InsertedItem", 10) ? new ItemStack(compound.getCompoundTag("InsertedItem")) : ItemStack.EMPTY;
    }
}