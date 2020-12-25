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


// import GhillieArmourMaterial;

public class Ghilliesuit implements ModInitializer {


	///////////////////
	/// Item Declaration

	private static final Item GHILLIE_WEAVE = new GhillieWeaveItem(new Item.Settings().group(ItemGroup.MATERIALS).maxCount(64));
	
	private static final Item GHILLIE_CAP = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT));
	private static final Item GHILLIE_TUNIC = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT));
	private static final Item GHILLIE_PANTS = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT));
	private static final Item GHILLIE_BOOTS = new ArmorItem(ModArmourMaterials.GHILLIE, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.


		///////////////////
		/// Item Registry

		Registry.register(Registry.ITEM, new Identifier("ghilliesuit", "ghillie_weave"), GHILLIE_WEAVE);

		Registry.register(Registry.ITEM, new Identifier("ghilliesuit", "ghillie_cap"), GHILLIE_CAP);
		Registry.register(Registry.ITEM, new Identifier("ghilliesuit", "ghillie_tunic"), GHILLIE_TUNIC);
		Registry.register(Registry.ITEM, new Identifier("ghilliesuit", "ghillie_pants"), GHILLIE_PANTS);
		Registry.register(Registry.ITEM, new Identifier("ghilliesuit", "ghillie_boots"), GHILLIE_BOOTS);


		
		///////////////////
		/// Debug

		System.out.println("Hello Fabric world!");
	}
}
