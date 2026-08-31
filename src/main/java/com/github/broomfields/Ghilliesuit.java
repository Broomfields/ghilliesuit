package com.github.broomfields;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.broomfields.armour.ModArmourMaterials;

public class Ghilliesuit implements ModInitializer {

	public static final String MOD_ID = "ghilliesuit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item MOSS_WEAVE = new Item(itemProperties("moss_weave").stacksTo(64));

	public static final Item GHILLIE_HELMET = new Item(itemProperties("ghillie_helmet").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.HELMET));
	public static final Item GHILLIE_CHESTPLATE = new Item(itemProperties("ghillie_chestplate").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.CHESTPLATE));
	public static final Item GHILLIE_LEGGINGS = new Item(itemProperties("ghillie_leggings").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.LEGGINGS));
	public static final Item GHILLIE_BOOTS = new Item(itemProperties("ghillie_boots").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.BOOTS));

	@Override
	public void onInitialize() {
		registerItem("moss_weave", MOSS_WEAVE);
		registerItem("ghillie_helmet", GHILLIE_HELMET);
		registerItem("ghillie_chestplate", GHILLIE_CHESTPLATE);
		registerItem("ghillie_leggings", GHILLIE_LEGGINGS);
		registerItem("ghillie_boots", GHILLIE_BOOTS);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
			output.accept(GHILLIE_HELMET);
			output.accept(GHILLIE_CHESTPLATE);
			output.accept(GHILLIE_LEGGINGS);
			output.accept(GHILLIE_BOOTS);
		});

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> output.accept(MOSS_WEAVE));

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
