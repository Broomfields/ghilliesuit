package com.github.enstroe;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.github.enstroe.armour.ModArmourMaterials;
import com.github.enstroe.items.GhillieWeaveItem;

public class Ghilliesuit implements ModInitializer {

	///////////////////
	/// Private Variables

	private static final String MODID = "ghilliesuit";


	///////////////////
	/// Item Declaration

	private static final Item GHILLIE_WEAVE = new GhillieWeaveItem(new Item.Settings().group(ItemGroup.MATERIALS).maxCount(64));
	
	public static final Item GHILLIE_HELMET = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
	public static final Item GHILLIE_CHESTPLATE = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
	public static final Item GHILLIE_LEGGINGS = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
	public static final Item GHILLIE_BOOTS = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


		///////////////////
		/// Item Registry

		Registry.register(Registry.ITEM, new Identifier(MODID, "ghillie_weave"), GHILLIE_WEAVE);

		Registry.register(Registry.ITEM, new Identifier(MODID, "ghillie_helmet"), GHILLIE_HELMET);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ghillie_chestplate"), GHILLIE_CHESTPLATE);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ghillie_leggings"), GHILLIE_LEGGINGS);
		Registry.register(Registry.ITEM, new Identifier(MODID, "ghillie_boots"), GHILLIE_BOOTS);


		
		///////////////////
		/// Debug

		System.out.println("Hello Fabric world!");
	}
}
