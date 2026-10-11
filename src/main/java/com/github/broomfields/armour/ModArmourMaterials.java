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
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ModArmourMaterials {

	public static final ArmorMaterial GHILLIE = new ArmorMaterial(
		5,
		Map.of(
			ArmorType.BOOTS, 1,
			ArmorType.LEGGINGS, 2,
			ArmorType.CHESTPLATE, 3,
			ArmorType.HELMET, 1,
			ArmorType.BODY, 3
		),
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

	private ModArmourMaterials() {
	}
}
