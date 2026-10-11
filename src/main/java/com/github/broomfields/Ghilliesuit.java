package com.github.broomfields;

import com.github.broomfields.armour.ModArmourMaterials;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.ArmorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ghilliesuit implements ModInitializer {

	public static final String MOD_ID = "ghilliesuit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final double STEALTH_HUNGER_PER_TICK = 0.1;
	private static final double HUNGER_DRAIN_ROUNDING_TOLERANCE = 1.0E-7;
	private static final float STEALTH_BASE_SECONDS = 10.0F;
	private static final float STEALTH_SECONDS_PER_COMFORT_PIECE = 5.0F;

	public static final ResourceKey<Enchantment> POISE = ResourceKey.create(Registries.ENCHANTMENT, id("poise"));

	public static final ResourceKey<Enchantment> COMFORT = ResourceKey.create(Registries.ENCHANTMENT, id("comfort"));

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

	public static boolean hasFullSuit(LivingEntity entity) {
		return ghilliePiecesWorn(entity) == 4;
	}

	public static int ghilliePiecesWorn(LivingEntity entity) {
		int pieceCount = 0;
		if (entity.getItemBySlot(EquipmentSlot.HEAD).is(GHILLIE_HOOD)) {
			pieceCount++;
		}
		if (entity.getItemBySlot(EquipmentSlot.CHEST).is(GHILLIE_TUNIC)) {
			pieceCount++;
		}
		if (entity.getItemBySlot(EquipmentSlot.LEGS).is(GHILLIE_PANTS)) {
			pieceCount++;
		}
		if (entity.getItemBySlot(EquipmentSlot.FEET).is(GHILLIE_BOOTS)) {
			pieceCount++;
		}
		return pieceCount;
	}

	public static boolean isStealthActive(LivingEntity entity) {
		if (!hasFullSuit(entity) || !entity.isCrouching()) {
			return false;
		}
		if (entity instanceof Player player) {
			return player.getFoodData().getFoodLevel() > 0;
		}
		return true;
	}

	public static boolean hasActivePoise(Player player) {
		if (!isStealthActive(player)) {
			return false;
		}
		Holder<Enchantment> poise = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(POISE).orElse(null);
		return poise != null && EnchantmentHelper.getItemEnchantmentLevel(poise, player.getItemBySlot(EquipmentSlot.CHEST)) > 0;
	}

	public static int comfortPiecesWorn(LivingEntity entity) {
		Holder<Enchantment> comfort = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(COMFORT).orElse(null);
		if (comfort == null) {
			return 0;
		}
		int pieceCount = 0;
		if (EnchantmentHelper.getItemEnchantmentLevel(comfort, entity.getItemBySlot(EquipmentSlot.HEAD)) > 0) {
			pieceCount++;
		}
		if (EnchantmentHelper.getItemEnchantmentLevel(comfort, entity.getItemBySlot(EquipmentSlot.CHEST)) > 0) {
			pieceCount++;
		}
		if (EnchantmentHelper.getItemEnchantmentLevel(comfort, entity.getItemBySlot(EquipmentSlot.LEGS)) > 0) {
			pieceCount++;
		}
		if (EnchantmentHelper.getItemEnchantmentLevel(comfort, entity.getItemBySlot(EquipmentSlot.FEET)) > 0) {
			pieceCount++;
		}
		return pieceCount;
	}

	public static float comfortDrainMultiplier(int comfortPieceCount) {
		return STEALTH_BASE_SECONDS / (STEALTH_BASE_SECONDS + STEALTH_SECONDS_PER_COMFORT_PIECE * comfortPieceCount);
	}

	public static double drainStealthHunger(ServerPlayer player, double pendingHungerDrain) {
		FoodData foodData = player.getFoodData();
		if (foodData.getFoodLevel() == 0 || player.getAbilities().invulnerable) {
			return 0.0;
		}
		if (!isStealthActive(player)) {
			return pendingHungerDrain;
		}

		pendingHungerDrain += STEALTH_HUNGER_PER_TICK * comfortDrainMultiplier(comfortPiecesWorn(player));
		int hungerPointsToDrain = (int) (pendingHungerDrain + HUNGER_DRAIN_ROUNDING_TOLERANCE);
		if (hungerPointsToDrain > 0) {
			int remainingFoodLevel = Math.max(0, foodData.getFoodLevel() - hungerPointsToDrain);
			foodData.setFoodLevel(remainingFoodLevel);
			foodData.setSaturation(Math.min(foodData.getSaturationLevel(), remainingFoodLevel));
		}
		return Math.max(0.0, pendingHungerDrain - hungerPointsToDrain);
	}

	private static Item.Properties itemProperties(String path) {
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id(path));
		return new Item.Properties().setId(itemKey);
	}

	private static void registerItem(String path, Item item) {
		Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(Registries.ITEM, id(path)), item);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
