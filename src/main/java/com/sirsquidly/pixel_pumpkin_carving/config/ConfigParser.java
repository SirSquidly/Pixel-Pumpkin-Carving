package com.sirsquidly.pixel_pumpkin_carving.config;

import com.google.common.collect.Lists;
import com.sirsquidly.pixel_pumpkin_carving.pixelPumpkinCarving;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 	This is to break part arrays in the config for use in other areas of the code.
 *
 *  I break it up in this class so that I don't have to break the config arrays every time I want to use them.
 */
public class ConfigParser
{
	/** Goes through the many Arrays in the config, to translate them into lists to be used elsewhere. */
	public static void breakupConfigArrays()
	{
		ConfigCache.carvablePumpkinTypesList.clear();
		ConfigCache.jackolanternLightingItems.clear();

		for (String string : Config.pumpkinTypes)
		{
			if (string == null || string.isEmpty()) continue;

			String[] blockListSplit = string.split(";", -1);
			String[] split = blockListSplit[0].split("-", 3);

			String name = split[0].trim();
			int normalColor = 0xFF441300;
			int shadowColor = 0xFF2D0003;
			List<IBlockState> sourceBlocks = Lists.newArrayList();

			if (name.isEmpty())
			{
				pixelPumpkinCarving.LOGGER.error("Empty pumpkin type name, skipping: " + string);
				continue;
			}
			if (split.length != 1)
			{
				try
				{
					normalColor = parseArgb(split[1]);
					shadowColor = parseArgb(split[2]);
				}
				catch (IllegalArgumentException e)
				{
					pixelPumpkinCarving.LOGGER.error("Invalid carving colors, skipping: " + string, e);
					continue;
				}
			}
			for (int i = 1; i < blockListSplit.length; i++)
			{
				String blockString = blockListSplit[i].trim();
				if (blockString.isEmpty()) continue;

				try
				{ sourceBlocks.addAll(getBlockStatesFromString(blockString)); }
				catch (IllegalArgumentException e)
				{
					pixelPumpkinCarving.LOGGER.error( "Invalid source block state '" + blockString + "' in: " + string, e);
				}
			}

			System.out.println("The " + string + " block list that is parsed: " + sourceBlocks);
			ConfigCache.carvablePumpkinTypesList.add(new CarvablePumpkinTypes(split[0], normalColor, shadowColor, sourceBlocks));
		}

		for (String string : Config.jackolanternLightingItems)
		{
			if (string == null || string.isEmpty()) continue;

			String[] split = string.split("-");

			ItemStack stack = getItemStackFromString(split[0].trim());
			int normalColor = 0xFFFFE156;
			int shadowColor = 0xFFFFC264;
			int innerColor = 0xFFFFFFA1;

			if (stack.isEmpty())
			{
				pixelPumpkinCarving.LOGGER.error("Invalid item for Jack o' Lantern lighting, and will be skipped: " + split[0]);
				continue;
			}
			if (split.length != 1)
			{
				try
				{
					normalColor = parseArgb(split[1]);
					shadowColor = parseArgb(split[2]);
					innerColor = parseArgb(split[3]);
				}
				catch (IllegalArgumentException e)
				{
					pixelPumpkinCarving.LOGGER.error("Invalid Jack o' Lantern colors, skipping: " + string, e);
					continue;
				}
			}

			ConfigCache.jackolanternLightingItems.add(new JackOLanternLights(stack, normalColor, shadowColor, innerColor));
		}
	}

	public static ItemStack getItemStackFromString(String string)
	{
		String[] ripString = string.split(":");

		if (ripString.length < 2) return ItemStack.EMPTY;

		Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(ripString[0], ripString[1]));
		if(item == null) return ItemStack.EMPTY;

		int meta = 0;
		if (ripString.length > 2) meta = Integer.parseInt(ripString[2]);

		return new ItemStack(item, 1, meta);
	}

	public static List<IBlockState> getBlockStatesFromString(String string)
	{
		List<IBlockState> states = new ArrayList<>();
		String[] ripString = string.split(":");

		if (ripString.length < 2)
		{ return states; }

		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(ripString[0], ripString[1]));
		Integer meta;

		if(block == null || block == Blocks.AIR)
		{ return states; }
		if(ripString.length > 2)
		{
			if (ripString[2].equals("*")) meta = -1;
			else meta = Integer.parseInt(ripString[2]);

			if(meta == -1) states.addAll(block.getBlockState().getValidStates());
			else states.add(block.getStateFromMeta(meta));
		}
		else states.add(block.getDefaultState());

		return states;
	}

	public static int parseArgb(String color)
	{
		String value = color.trim();
		if (value.startsWith("0x") || value.startsWith("0X")) value = value.substring(2);

		if (value.length() == 6) value = "FF" + value;
		else if (value.length() != 8)
			throw new IllegalArgumentException("Expected 6-digit RGB or 8-digit ARGB color: " + color);

		return (int) Long.parseLong(value, 16);
	}

	@Nullable
	public static JackOLanternLights getJackOLanternLights(ItemStack held)
	{
		if (held.isEmpty()) return null;

		for (JackOLanternLights entry : ConfigCache.jackolanternLightingItems)
		{ if (ItemStack.areItemsEqual(entry.stack, held)) return entry; }

		return null;
	}

	public static class CarvablePumpkinTypes
	{
		public final String name;
		public final int colorCarved;
		public final int colorCarvedShadow;
		public final List<IBlockState> sourceBlocks;

		public CarvablePumpkinTypes(String name, int colorCarved, int colorCarvedShadow, List<IBlockState> sourceBlocks)
		{
			this.name = name;
			this.colorCarved = colorCarved;
			this.colorCarvedShadow = colorCarvedShadow;
			this.sourceBlocks = sourceBlocks;
		}
	}

	public static class JackOLanternLights
	{
		public final ItemStack stack;
		public final int jackColorNormal;
		public final int jackColorShadow;
		public final int jackColorInner;

		public JackOLanternLights(ItemStack stack, int jackColorNormal, int jackColorShadow, int jackColorInner)
		{
			this.stack = stack;
			this.jackColorNormal = jackColorNormal;
			this.jackColorShadow = jackColorShadow;
			this.jackColorInner = jackColorInner;
		}
	}
}