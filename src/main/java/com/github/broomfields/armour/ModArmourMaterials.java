package com.github.broomfields.armour;

import java.util.Map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class ModArmourMaterials {

	// Leather-grade stats: durability is a multiplier, defense is per piece.
	public static final ArmorMaterial GHILLIE = new ArmorMaterial(
		5,
		makeDefense(1, 2, 3, 1, 3),
		15,
		SoundEvents.ARMOR_EQUIP_LEATHER,
		0.0F,
		0.0F,
		repairIngredient(),
		ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("ghilliesuit", "ghillie"))
	);

	private static TagKey<Item> repairIngredient() {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("ghilliesuit", "moss_weave"));
	}

	private static Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm, int body) {
		return Map.of(
			ArmorType.BOOTS, boots,
			ArmorType.LEGGINGS, legs,
			ArmorType.CHESTPLATE, chest,
			ArmorType.HELMET, helm,
			ArmorType.BODY, body
		);
	}
}
