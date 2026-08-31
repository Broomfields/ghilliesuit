package com.github.broomfields;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.broomfields.armour.ModArmourMaterials;

public class Ghilliesuit implements ModInitializer {

	public static final String MOD_ID = "ghilliesuit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item GHILLIE_WEAVE = new Item(itemProperties("ghillie_weave").stacksTo(64));

	public static final Item GHILLIE_HELMET = new Item(itemProperties("ghillie_helmet").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.HELMET));
	public static final Item GHILLIE_CHESTPLATE = new Item(itemProperties("ghillie_chestplate").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.CHESTPLATE));
	public static final Item GHILLIE_LEGGINGS = new Item(itemProperties("ghillie_leggings").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.LEGGINGS));
	public static final Item GHILLIE_BOOTS = new Item(itemProperties("ghillie_boots").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.BOOTS));

	public static final CreativeModeTab GHILLIE_TAB = CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
		.title(Component.translatable("itemGroup.ghilliesuit"))
		.icon(() -> new ItemStack(GHILLIE_WEAVE))
		.displayItems((parameters, output) -> {
			output.accept(GHILLIE_WEAVE);
			output.accept(GHILLIE_HELMET);
			output.accept(GHILLIE_CHESTPLATE);
			output.accept(GHILLIE_LEGGINGS);
			output.accept(GHILLIE_BOOTS);
		})
		.build();

	@Override
	public void onInitialize() {
		registerItem("ghillie_weave", GHILLIE_WEAVE);
		registerItem("ghillie_helmet", GHILLIE_HELMET);
		registerItem("ghillie_chestplate", GHILLIE_CHESTPLATE);
		registerItem("ghillie_leggings", GHILLIE_LEGGINGS);
		registerItem("ghillie_boots", GHILLIE_BOOTS);

		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("ghilliesuit"), GHILLIE_TAB);

		LOGGER.info("Ghillie Suit loaded!");
	}

	private static Item.Properties itemProperties(String path) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id(path));
		return new Item.Properties().setId(key);
	}

	private static void registerItem(String path, Item item) {
		Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(Registries.ITEM, id(path)), item);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
