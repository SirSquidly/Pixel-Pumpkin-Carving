package com.sirsquidly.pixel_pumpkin_carving.init;

import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockUncarvedPumpkin;
import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvableJackOLantern;
import com.sirsquidly.pixel_pumpkin_carving.common.blocks.BlockCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.common.ItemCarvablePumpkin;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigCache;
import com.sirsquidly.pixel_pumpkin_carving.config.ConfigParser;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = pixelPumpkinCarving.MOD_ID)
public class PPCBlocks
{
    /** Contains every block ran through the 'blockReadyForRegister' function. So they all SHOULD be registered. */
    private static List<Block> blockList = new ArrayList<Block>();
    /** Records any blocks that have a unique itemBlock assigned, as the rest are automatically slapped with the default itemBlock.*/
    private static List<Block> itemBlockBlacklist = new ArrayList<Block>();
    /** Any blocks that should skip registering a normal Item Model. */
    private static List<Block> itemModelBlacklist = new ArrayList<Block>();
    /** Used for items settup here, and need to have models registered as such. */
    private static List<Item> blockDirectItemList = new ArrayList<Item>();

    public static Block PUMPKIN_UNCARVED = new BlockUncarvedPumpkin().setHardness(1.5F);
    //public static Block JACK_O_LANTERN_CARVABLE = new BlockCarvableJackOLantern().setHardness(1.5F).setCreativeTab(CreativeTabs.DECORATIONS);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        registeredPumpkins.clear();
        blockList.clear();

        /* This NEEDS to be ran so we can get the dynamic block lists populated. */
        ConfigParser.breakupConfigArrays();
        for (ConfigParser.CarvablePumpkinTypes type : ConfigCache.carvablePumpkinTypesList)
        {
            RegisteredPumpkinType registered = new RegisteredPumpkinType(type);
            registeredPumpkins.add(registered);

            blockReadyForRegister(registered.pumpkin, type.name + "_carvable");
            blockReadyForRegister(registered.jackOLantern, type.name + "_carvable_lit");
        }
        blockReadyForRegister(PUMPKIN_UNCARVED, "pumpkin_uncarved");
        //blockReadyForRegister(JACK_O_LANTERN_CARVABLE, "jack_o_lantern_carvable_jacko_lantern");

        for (Block blocks : blockList) event.getRegistry().register(blocks);
    }


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        for (RegisteredPumpkinType type : registeredPumpkins)
        {
            event.getRegistry().register(new ItemCarvablePumpkin(type.pumpkin));
            itemBlockBlacklist.add(type.pumpkin);

            event.getRegistry().register(new ItemCarvablePumpkin(type.jackOLantern));
            itemBlockBlacklist.add(type.jackOLantern);
        }
        //event.getRegistry().register(new ItemCarvablePumpkin(PUMPKIN_CARVABLE)); itemBlockBlacklist.add(PUMPKIN_CARVABLE);
        //event.getRegistry().register(new ItemCarvablePumpkin(JACK_O_LANTERN_CARVABLE)); itemBlockBlacklist.add(JACK_O_LANTERN_CARVABLE);

        /** As stated on itemBlockBlacklist, this registers anything NOT from the blacklist with a generic itemBlock.*/
        for (Block blocks : blockList) if (!(itemBlockBlacklist.contains(blocks))) { registerItemBlock(r, blocks);}
    }


    /** If blocks don't specify the 'addToTab' boolean, assume true.*/
    public static Block blockReadyForRegister(Block block, String name)
    { return blockReadyForRegister(block, name, true);}

    /** Slaps the names to Blocks, and adds them to the blockList to be registered in 'registerBlocks'.*/
    public static Block blockReadyForRegister(Block block, String name, Boolean addNormalModel)
    {
        block.setTranslationKey(pixelPumpkinCarving.MOD_ID + "." + name);
        block.setRegistryName(name);

        blockList.add(block);

        return block;
    }

    public static ItemBlock registerItemBlock(IForgeRegistry<Item> registry, Block block)
    { return registerItemBlock(registry, new ItemBlock(block)); }

    public static <T extends ItemBlock> T registerItemBlock(IForgeRegistry<Item> registry, T item) {
        Block block = item.getBlock();
        item.setTranslationKey(block.getTranslationKey());
        item.setRegistryName(block.getRegistryName());

        registry.register(item);

        return item;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {
        for(Item i : blockDirectItemList)
        { ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName(), "inventory")); }

        for(Block b : blockList)
        { if (!(itemModelBlacklist.contains(b))) ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory")); }
    }


    public static class RegisteredPumpkinType
    {
        public final ConfigParser.CarvablePumpkinTypes config;
        public final BlockCarvablePumpkin pumpkin;
        public final BlockCarvableJackOLantern jackOLantern;

        public RegisteredPumpkinType(ConfigParser.CarvablePumpkinTypes config)
        {
            this.config = config;
            this.pumpkin = new BlockCarvablePumpkin(config);
            this.jackOLantern = new BlockCarvableJackOLantern(config);
        }
    }

    private static final List<RegisteredPumpkinType> registeredPumpkins = new ArrayList<>();

    public static List<RegisteredPumpkinType> getRegisteredPumpkins()
    { return Collections.unmodifiableList(registeredPumpkins); }

}