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
import java.util.stream.Collectors;

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

		for (String string : Config.pumpkinTypeDefinitions)
		{
			if (string == null || string.isEmpty()) continue;

			String[] sections = string.split("\\|", -1);
			String name = sections[0].trim();

			int normalColor = 0xFF441300;
			int shadowColor = 0xFF2D0003;
			List<IBlockState> sourceBlocks = Lists.newArrayList();
			boolean golemSupported = true;
			boolean halloweenParticipant = true;
			boolean allLightingItems = true;
			List<ItemStack> allowedLightingItems = new ArrayList<>();

			if (name.isEmpty())
			{
				pixelPumpkinCarving.LOGGER.error("Empty pumpkin type name, skipping: " + string);
				continue;
			}

			for (int i = 1; i < sections.length; i++)
			{
				String[] option = sections[i].split("=", 2);
				if (option.length != 2) continue;
				String key = option[0].trim();
				String value = option[1].trim();

				try
				{
					switch (key)
					{
						case "baseColor":
							normalColor = parseArgb(value);
							break;
						case "shadowColor":
							shadowColor = parseArgb(value);
							break;
						case "sourceBlocks":
							sourceBlocks.clear();

							for (String block : value.split(";"))
							{
								if (block.trim().isEmpty()) continue;
								try
								{ sourceBlocks.addAll(getBlockStatesFromString(block.trim())); }
								catch (IllegalArgumentException e)
								{ pixelPumpkinCarving.LOGGER.error("Invalid source block state '" + block + "' in: " + string, e); }
							}
							break;
						case "golemSupported":
							golemSupported = Boolean.parseBoolean(value);
							break;
						case "halloween":
							halloweenParticipant = Boolean.parseBoolean(value);
							break;
						case "allowedLights":
							allLightingItems = value.equals("*");
							allowedLightingItems.clear();
							if (!allLightingItems && !value.isEmpty())
							{
								for (String item : value.split(";"))
								{
									ItemStack stack = getItemStackFromString(item.trim());
									if (stack.isEmpty())
										pixelPumpkinCarving.LOGGER.error("Invalid allowed lighting item '" + item + "' in: " + string);
									else allowedLightingItems.add(stack);
								}
							}
							break;
						default:
							pixelPumpkinCarving.LOGGER.warn("Unknown pumpkin type property '" + key + "' in: " + string);
							break;
					}
				}
				catch (IllegalArgumentException e)
				{ pixelPumpkinCarving.LOGGER.error("Invalid value for pumpkin type property '" + key + "' in: " + string, e); }
			}

			ConfigCache.carvablePumpkinTypesList.add(new CarvablePumpkinTypes(name, normalColor, shadowColor, sourceBlocks, golemSupported, halloweenParticipant, allLightingItems, allowedLightingItems));
		}

		for (String string : Config.lightingItemDefinitions)
		{
			if (string == null || string.isEmpty()) continue;

			String[] sections = string.split("\\|", -1);
			ItemStack stack = getItemStackFromString(sections[0].trim());

			int normalColor = 0xFFFFE156;
			int shadowColor = 0xFFFFC264;
			int innerColor = 0xFFFFFFA1;
			boolean halloweenParticipant = true;

			for (int i = 1; i < sections.length; i++)
			{
				String[] option = sections[i].split("=", 2);
				if (option.length != 2) continue;
				String key = option[0].trim();
				String value = option[1].trim();

				try
				{
					switch (key)
					{
						case "baseColor":
							normalColor = parseArgb(value);
							break;
						case "shadowColor":
							shadowColor = parseArgb(value);
							break;
						case "innerColor":
							innerColor = parseArgb(value);
							break;
						case "halloween":
							halloweenParticipant = Boolean.parseBoolean(value);
							break;
						default:
							pixelPumpkinCarving.LOGGER.warn("Unknown lighting item property '" + key + "' in: " + string);
							break;
					}
				}
				catch (IllegalArgumentException e)
				{ pixelPumpkinCarving.LOGGER.error("Invalid value for lighting item property '" + key + "' in: " + string, e); }
			}

			ConfigCache.jackolanternLightingItems.add(new JackOLanternLights(stack, normalColor, shadowColor, innerColor, halloweenParticipant));
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

	/** Return the light information from a given pumpkin type, if it is compatible. */
	@Nullable
	public static JackOLanternLights getJackOLanternLights(ItemStack held, CarvablePumpkinTypes type)
	{
		if (held.isEmpty()) return null;

		if (!type.allLightingItems)
		{
			boolean allowed = false;
			for (ItemStack stack : type.allowedLightingItems)
			{
				if (ItemStack.areItemsEqual(stack, held))
				{
					allowed = true;
					break;
				}
			}
			if (!allowed) return null;
		}

		for (JackOLanternLights entry : ConfigCache.jackolanternLightingItems)
		{ if (ItemStack.areItemsEqual(entry.stack, held)) return entry; }

		return null;
	}

	/** Return the list of every allowed light for the given Pumpkin Type */
	public static List<ConfigParser.JackOLanternLights> getAllowedLights(ConfigParser.CarvablePumpkinTypes type)
	{
		return ConfigCache.jackolanternLightingItems.stream().filter(light -> type.allLightingItems ||
						type.allowedLightingItems.stream().anyMatch(stack -> ItemStack.areItemsEqual(stack, light.stack))).collect(Collectors.toList());
	}

	public static CarvablePumpkinTypes getPumpkinConfig(String name)
	{
		for (CarvablePumpkinTypes config : ConfigCache.carvablePumpkinTypesList)
			if (config.name.equalsIgnoreCase(name)) return config;
		return null;
	}

	public static class CarvablePumpkinTypes
	{
		public final String name;
		public final int colorCarved;
		public final int colorCarvedShadow;
		public final List<IBlockState> sourceBlocks;
		public final boolean golemSupported;
		public final boolean halloweenParticipant;
		public final boolean allLightingItems;
		public final List<ItemStack> allowedLightingItems;

		public CarvablePumpkinTypes(String name, int colorCarved, int colorCarvedShadow, List<IBlockState> sourceBlocks, boolean golemSupportedIn, boolean halloweenIn, boolean everyLightIn, List<ItemStack> allowedLights)
		{
			this.name = name;
			this.colorCarved = colorCarved;
			this.colorCarvedShadow = colorCarvedShadow;
			this.sourceBlocks = sourceBlocks;
			this.golemSupported = golemSupportedIn;
			this.halloweenParticipant = halloweenIn;
			this.allLightingItems = everyLightIn;
			this.allowedLightingItems = allowedLights;
		}
	}

	public static class JackOLanternLights
	{
		public final ItemStack stack;
		public final int jackColorNormal;
		public final int jackColorShadow;
		public final int jackColorInner;
		public final boolean halloweenParticipant;

		public JackOLanternLights(ItemStack stack, int jackColorNormal, int jackColorShadow, int jackColorInner, boolean halloweenIn)
		{
			this.stack = stack;
			this.jackColorNormal = jackColorNormal;
			this.jackColorShadow = jackColorShadow;
			this.jackColorInner = jackColorInner;
			this.halloweenParticipant = halloweenIn;
		}
	}
}