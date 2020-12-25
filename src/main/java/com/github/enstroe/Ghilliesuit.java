package com.github.enstroe;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Ghilliesuit implements ModInitializer {

	private static final Item GHILLIE_WEAVE = new GhillieWeaveItem(new Item.Settings().group(ItemGroup.MATERIALS).maxCount(64));


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		Registry.register(Registry.ITEM, new Identifier("ghilliesuit", "ghillie_weave"), GHILLIE_WEAVE);


		System.out.println("Hello Fabric world!");
	}
}
