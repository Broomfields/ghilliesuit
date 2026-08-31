package com.github.broomfields;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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

	public static final Item GHILLIE_HOOD = new Item(itemProperties("ghillie_hood").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.HELMET));
	public static final Item GHILLIE_TUNIC = new Item(itemProperties("ghillie_tunic").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.CHESTPLATE));
	public static final Item GHILLIE_PANTS = new Item(itemProperties("ghillie_pants").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.LEGGINGS));
	public static final Item GHILLIE_BOOTS = new Item(itemProperties("ghillie_boots").humanoidArmor(ModArmourMaterials.GHILLIE, ArmorType.BOOTS));

	@Override
	public void onInitialize() {
		registerItem("moss_weave", MOSS_WEAVE);
		registerItem("ghillie_hood", GHILLIE_HOOD);
		registerItem("ghillie_tunic", GHILLIE_TUNIC);
		registerItem("ghillie_pants", GHILLIE_PANTS);
		registerItem("ghillie_boots", GHILLIE_BOOTS);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
			output.accept(GHILLIE_HOOD);
			output.accept(GHILLIE_TUNIC);
			output.accept(GHILLIE_PANTS);
			output.accept(GHILLIE_BOOTS);
		});

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> output.accept(MOSS_WEAVE));

		LOGGER.info("Ghillie Suit loaded!");
	}

	/** True while the wearer has all four ghillie pieces equipped. */
	public static boolean hasFullSuit(LivingEntity entity) {
		return ghilliePiecesWorn(entity) == 4;
	}

	/** Number of ghillie pieces worn (0-4); each piece shrinks the mob detection range. */
	public static int ghilliePiecesWorn(LivingEntity entity) {
		int count = 0;
		if (entity.getItemBySlot(EquipmentSlot.HEAD).is(GHILLIE_HOOD)) count++;
		if (entity.getItemBySlot(EquipmentSlot.CHEST).is(GHILLIE_TUNIC)) count++;
		if (entity.getItemBySlot(EquipmentSlot.LEGS).is(GHILLIE_PANTS)) count++;
		if (entity.getItemBySlot(EquipmentSlot.FEET).is(GHILLIE_BOOTS)) count++;
		return count;
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
