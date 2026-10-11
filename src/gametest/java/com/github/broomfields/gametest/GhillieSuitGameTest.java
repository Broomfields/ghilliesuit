package com.github.broomfields.gametest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.broomfields.Ghilliesuit;
import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public class GhillieSuitGameTest {

	private static final List<Item> GHILLIE_ARMOR_ITEMS = List.of(Ghilliesuit.GHILLIE_HOOD, Ghilliesuit.GHILLIE_TUNIC, Ghilliesuit.GHILLIE_PANTS, Ghilliesuit.GHILLIE_BOOTS);
	private static final int ENCHANTMENT_SEED_DATA_SLOT = 3;

	@GameTest(structure = "minecraft:empty")
	public void eachPieceReducesDetectionMultiplier(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, new Vec3(0.5, 1.0, 0.5));
		assertDetectionMultiplier(helper, zombie, 0, 1.0);
		zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Ghilliesuit.GHILLIE_HOOD));
		assertDetectionMultiplier(helper, zombie, 1, 7.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Ghilliesuit.GHILLIE_TUNIC));
		assertDetectionMultiplier(helper, zombie, 2, 6.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Ghilliesuit.GHILLIE_PANTS));
		assertDetectionMultiplier(helper, zombie, 3, 5.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Ghilliesuit.GHILLIE_BOOTS));
		assertDetectionMultiplier(helper, zombie, 4, 4.0 / 8.0);
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void mossWeaveRecipeCrafts(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		ShapelessRecipe recipe = (ShapelessRecipe) recipe(helper, "moss_weave");

		CraftingInput input = CraftingInput.of(3, 3, List.of(
			new ItemStack(Items.MOSS_BLOCK), new ItemStack(Items.MOSS_BLOCK), ItemStack.EMPTY,
			new ItemStack(Items.STRING), new ItemStack(Items.STRING), ItemStack.EMPTY,
			ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
		));
		helper.assertTrue(recipe.matches(input, level), "2 moss + 2 string should match the moss weave recipe");

		ItemStack result = recipe.assemble(input);
		helper.assertTrue(result.is(Ghilliesuit.MOSS_WEAVE) && result.getCount() == 2, "should output 2 moss weave, got " + result);
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void ghillieHoodRecipeCrafts(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		ShapedRecipe recipe = (ShapedRecipe) recipe(helper, "ghillie_hood");

		CraftingInput input = CraftingInput.of(3, 3, List.of(
			weave(), weave(), weave(),
			weave(), ItemStack.EMPTY, weave(),
			ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
		));
		helper.assertTrue(recipe.matches(input, level), "5 moss weave in the helmet pattern should match");

		ItemStack result = recipe.assemble(input);
		helper.assertTrue(result.is(Ghilliesuit.GHILLIE_HOOD) && result.getCount() == 1, "should output a ghillie hood, got " + result);
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void comfortEnchantableOnAllGhilliePieces(GameTestHelper helper) {
		Enchantment comfort = enchantment(helper, "ghilliesuit:comfort");
		helper.assertTrue(comfort.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_HOOD)), "comfort should apply to the ghillie hood");
		helper.assertTrue(comfort.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_TUNIC)), "comfort should apply to the ghillie tunic");
		helper.assertTrue(comfort.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_PANTS)), "comfort should apply to the ghillie pants");
		helper.assertTrue(comfort.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_BOOTS)), "comfort should apply to the ghillie boots");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void poiseEnchantableOnTunicOnly(GameTestHelper helper) {
		Enchantment poise = enchantment(helper, "ghilliesuit:poise");
		helper.assertTrue(poise.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_TUNIC)), "poise should apply to the ghillie tunic");
		helper.assertFalse(poise.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_HOOD)), "poise should not apply to the ghillie hood");
		helper.assertFalse(poise.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_PANTS)), "poise should not apply to the ghillie pants");
		helper.assertFalse(poise.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_BOOTS)), "poise should not apply to the ghillie boots");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void swiftSneakEnchantableOnGhilliePants(GameTestHelper helper) {
		Enchantment swiftSneak = enchantment(helper, "minecraft:swift_sneak");
		helper.assertTrue(swiftSneak.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_PANTS)), "swift sneak should apply to the ghillie pants");
		helper.assertFalse(swiftSneak.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_TUNIC)), "swift sneak should not apply to the ghillie tunic");
		helper.assertFalse(swiftSneak.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_HOOD)), "swift sneak should not apply to the ghillie hood");
		helper.assertFalse(swiftSneak.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_BOOTS)), "swift sneak should not apply to the ghillie boots");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void durabilityEnchantmentsApplyAndCoexist(GameTestHelper helper) {
		Holder<Enchantment> mending = enchantmentHolder(helper, "minecraft:mending");
		Holder<Enchantment> unbreaking = enchantmentHolder(helper, "minecraft:unbreaking");
		for (Item item : List.of(Ghilliesuit.GHILLIE_HOOD, Ghilliesuit.GHILLIE_TUNIC, Ghilliesuit.GHILLIE_PANTS, Ghilliesuit.GHILLIE_BOOTS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_PICKAXE)) {
			helper.assertTrue(mending.value().canEnchant(new ItemStack(item)), "Mending must support ghillie pieces and existing vanilla items");
			helper.assertTrue(unbreaking.value().canEnchant(new ItemStack(item)), "Unbreaking must support ghillie pieces and existing vanilla items");
		}
		for (Holder<Enchantment> durabilityEnchantment : List.of(mending, unbreaking)) {
			for (String otherEnchantmentId : List.of("ghilliesuit:comfort", "ghilliesuit:poise", "minecraft:swift_sneak")) {
				helper.assertTrue(Enchantment.areCompatible(durabilityEnchantment, enchantmentHolder(helper, otherEnchantmentId)), "durability enchantments must coexist with stealth enchantments");
			}
		}
		helper.assertTrue(Enchantment.areCompatible(mending, unbreaking), "Mending and Unbreaking must coexist");
		var tableEnchantmentPool = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.IN_ENCHANTING_TABLE);
		helper.assertFalse(tableEnchantmentPool.contains(mending), "Mending must remain outside the table pool");
		helper.assertTrue(tableEnchantmentPool.contains(unbreaking), "Unbreaking must remain available at the table");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void ghillieDurabilityEnchantmentsUseVanillaEffects(GameTestHelper helper) {
		for (Item item : GHILLIE_ARMOR_ITEMS) {
			ItemStack armor = new ItemStack(item);
			armor.enchant(enchantmentHolder(helper, "minecraft:mending"), 1);
			helper.assertTrue(EnchantmentHelper.modifyDurabilityToRepairFromXp(helper.getLevel(), armor, 3) == 6, "Mending must retain vanilla repair strength");
			armor.enchant(enchantmentHolder(helper, "minecraft:unbreaking"), 3);
			int damage = EnchantmentHelper.processDurabilityChange(helper.getLevel(), armor, 10000);
			helper.assertTrue(damage > 6000 && damage < 8000, "Unbreaking III must use the armor damage rate, got " + damage);
		}
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void protectionNotEnchantableOnGhillieArmor(GameTestHelper helper) {
		Enchantment protection = enchantment(helper, "minecraft:protection");
		helper.assertFalse(protection.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_HOOD)), "protection should not apply to the ghillie hood");
		helper.assertFalse(protection.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_TUNIC)), "protection should not apply to the ghillie tunic");
		helper.assertFalse(protection.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_PANTS)), "protection should not apply to the ghillie pants");
		helper.assertFalse(protection.canEnchant(new ItemStack(Ghilliesuit.GHILLIE_BOOTS)), "protection should not apply to the ghillie boots");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void comfortAndPoiseAreMutuallyExclusive(GameTestHelper helper) {
		Registry<Enchantment> enchantments = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		Holder<Enchantment> comfort = enchantments.getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("ghilliesuit", "comfort")));
		Holder<Enchantment> poise = enchantments.getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("ghilliesuit", "poise")));
		helper.assertFalse(Enchantment.areCompatible(comfort, poise), "comfort and poise should be mutually exclusive on the tunic");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void enchantingTableOffersCustomEnchantments(GameTestHelper helper) {
		var tableEnchantmentPool = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.IN_ENCHANTING_TABLE);
		helper.assertTrue(tableEnchantmentPool.contains(enchantmentHolder(helper, "ghilliesuit:comfort")), "Comfort must be in the table pool");
		helper.assertTrue(tableEnchantmentPool.contains(enchantmentHolder(helper, "ghilliesuit:poise")), "Poise must be in the table pool");
		helper.assertFalse(tableEnchantmentPool.contains(enchantmentHolder(helper, "minecraft:swift_sneak")), "Swift Sneak must retain vanilla acquisition rules");
		boolean poiseOffered = false;
		for (Item item : GHILLIE_ARMOR_ITEMS) {
			for (int bookshelfCount = 0; bookshelfCount <= 15; bookshelfCount++) {
				for (int enchantmentSeed = 0; enchantmentSeed < 20; enchantmentSeed++) {
					for (int offerSlot = 0; offerSlot < 3; offerSlot++) {
						ItemStack itemStack = new ItemStack(item);
						int enchantmentCost = EnchantmentHelper.getEnchantmentCost(RandomSource.create(enchantmentSeed), offerSlot, bookshelfCount, itemStack);
						if (enchantmentCost < offerSlot + 1) {
							continue;
						}
						var enchantmentOffers = EnchantmentHelper.selectEnchantment(RandomSource.create(enchantmentSeed + offerSlot), itemStack, enchantmentCost, tableEnchantmentPool.stream());
						helper.assertTrue(!enchantmentOffers.isEmpty(), "every enabled offer must have a valid enchantment: " + item + ", shelves=" + bookshelfCount + ", slot=" + offerSlot + ", seed=" + enchantmentSeed);
						for (var enchantmentOffer : enchantmentOffers) {
							poiseOffered |= enchantmentOffer.enchantment().is(Ghilliesuit.POISE);
							helper.assertTrue(isAllowedTableEnchantment(item, enchantmentOffer), "table must respect suit enchantment restrictions");
						}
					}
				}
			}
		}
		helper.assertTrue(poiseOffered, "Poise must appear among tunic offers");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void ghillieArmorHasEnchantmentCost(GameTestHelper helper) {
		helper.assertTrue(
			EnchantmentHelper.getEnchantmentCost(RandomSource.create(), 0, 30, new ItemStack(Ghilliesuit.GHILLIE_TUNIC)) > 0,
			"the ghillie tunic should have a positive enchanting cost"
		);
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void activeStealthRequiresFullSuitCrouchAndFood(GameTestHelper helper) {
		ServerPlayer player = serverPlayer(helper);
		helper.assertFalse(Ghilliesuit.isStealthActive(player), "without a suit there is no active stealth");

		equipFullSuit(player);
		helper.assertFalse(Ghilliesuit.isStealthActive(player), "standing up in the full suit is not active stealth");

		player.setPose(Pose.CROUCHING);
		helper.assertTrue(Ghilliesuit.isStealthActive(player), "full suit + crouch + food should enable active stealth");

		player.getFoodData().setSaturation(0.0F);
		helper.assertTrue(Ghilliesuit.isStealthActive(player), "zero saturation must not prevent stealth with food remaining");

		player.getFoodData().setFoodLevel(0);
		helper.assertFalse(Ghilliesuit.isStealthActive(player), "active stealth should stop when the hunger bar is empty");

		player.getFoodData().setFoodLevel(1);
		helper.assertTrue(Ghilliesuit.isStealthActive(player), "eating should restore stealth eligibility");
		player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
		helper.assertFalse(Ghilliesuit.isStealthActive(player), "a partial suit should not be actively stealthed");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void activeStealthDrainsVisibleHunger(GameTestHelper helper) {
		ServerPlayer player = serverPlayer(helper);
		equipFullSuit(player);
		player.setPose(Pose.CROUCHING);
		FoodData foodData = player.getFoodData();
		double pendingHungerDrain = 0.0;
		for (int elapsedTicks = 0; elapsedTicks < 10; elapsedTicks++) {
			pendingHungerDrain = Ghilliesuit.drainStealthHunger(player, pendingHungerDrain);
			foodData.tick(player);
		}
		helper.assertTrue(foodData.getFoodLevel() == 19, "hunger must drop even with full initial saturation");
		for (int elapsedTicks = 10; elapsedTicks < 200; elapsedTicks++) {
			pendingHungerDrain = Ghilliesuit.drainStealthHunger(player, pendingHungerDrain);
			foodData.tick(player);
		}
		helper.assertTrue(foodData.getFoodLevel() == 0, "full hunger should last 200 active ticks");
		helper.assertFalse(Ghilliesuit.isStealthActive(player), "empty hunger must disable stealth");
		Ghilliesuit.drainStealthHunger(player, pendingHungerDrain);
		helper.assertTrue(foodData.getFoodLevel() == 0, "food must never become negative");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void hungerDrainPausesBetweenCrouches(GameTestHelper helper) {
		ServerPlayer player = serverPlayer(helper);
		equipFullSuit(player);
		player.setPose(Pose.CROUCHING);
		double pendingHungerDrain = 0.0;
		for (int elapsedTicks = 0; elapsedTicks < 5; elapsedTicks++) {
			pendingHungerDrain = Ghilliesuit.drainStealthHunger(player, pendingHungerDrain);
		}
		player.setPose(Pose.STANDING);
		for (int elapsedTicks = 0; elapsedTicks < 100; elapsedTicks++) {
			pendingHungerDrain = Ghilliesuit.drainStealthHunger(player, pendingHungerDrain);
		}
		helper.assertTrue(player.getFoodData().getFoodLevel() == 20, "standing must not drain hunger");
		player.setPose(Pose.CROUCHING);
		for (int elapsedTicks = 0; elapsedTicks < 5; elapsedTicks++) {
			pendingHungerDrain = Ghilliesuit.drainStealthHunger(player, pendingHungerDrain);
		}
		helper.assertTrue(player.getFoodData().getFoodLevel() == 19, "brief crouches must carry fractional hunger cost");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void comfortPiecesWornCountsEnchantedGhilliePieces(GameTestHelper helper) {
		ServerPlayer player = serverPlayer(helper);
		equipFullSuit(player);
		helper.assertTrue(Ghilliesuit.comfortPiecesWorn(player) == 0, "an un-enchanted suit should have no comfort pieces");

		enchantWithComfort(helper, player);
		helper.assertTrue(Ghilliesuit.comfortPiecesWorn(player) == 4, "four comfort-enchanted pieces should count as four");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void comfortDrainMultiplierThirdAtFullSet(GameTestHelper helper) {
		helper.assertTrue(Math.abs(Ghilliesuit.comfortDrainMultiplier(0) - 1.0F) < 1.0E-6, "no comfort pieces should mean the full drain rate");
		helper.assertTrue(Math.abs(Ghilliesuit.comfortDrainMultiplier(4) - 1.0F / 3.0F) < 1.0E-6, "a full comfort set should use one third of the drain rate");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void comfortExtendsActiveStealthTime(GameTestHelper helper) {
		ServerPlayer unenchantedPlayer = serverPlayer(helper);
		equipFullSuit(unenchantedPlayer);
		unenchantedPlayer.setPose(Pose.CROUCHING);

		ServerPlayer comfortPlayer = serverPlayer(helper);
		equipFullSuit(comfortPlayer);
		enchantWithComfort(helper, comfortPlayer);
		comfortPlayer.setPose(Pose.CROUCHING);

		double unenchantedPendingDrain = 0.0;
		double comfortPendingDrain = 0.0;
		for (int elapsedTicks = 0; elapsedTicks < 200; elapsedTicks++) {
			unenchantedPendingDrain = Ghilliesuit.drainStealthHunger(unenchantedPlayer, unenchantedPendingDrain);
			unenchantedPlayer.getFoodData().tick(unenchantedPlayer);
			comfortPendingDrain = Ghilliesuit.drainStealthHunger(comfortPlayer, comfortPendingDrain);
			comfortPlayer.getFoodData().tick(comfortPlayer);
		}
		helper.assertTrue(unenchantedPlayer.getFoodData().getFoodLevel() == 0, "plain suit should empty hunger after ten seconds");
		helper.assertTrue(comfortPlayer.getFoodData().getFoodLevel() == 14, "full Comfort should consume six whole hunger points after ten seconds");
		for (int elapsedTicks = 0; elapsedTicks < 400; elapsedTicks++) {
			comfortPendingDrain = Ghilliesuit.drainStealthHunger(comfortPlayer, comfortPendingDrain);
		}
		helper.assertTrue(comfortPlayer.getFoodData().getFoodLevel() == 0, "full Comfort should empty hunger after thirty seconds");
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void poiseUsesVanillaCriticalDamageAndCharge(GameTestHelper helper) {
		CombatPlayer player = new CombatPlayer(helper.getLevel());
		equipFullSuit(player);
		player.setOnGround(true);
		player.setPose(Pose.CROUCHING);
		player.getFoodData().setFoodLevel(20);
		player.getFoodData().setSaturation(0);
		assertAttackDamage(helper, player, 1.0F, false);
		player.getItemBySlot(EquipmentSlot.CHEST).enchant(enchantmentHolder(helper, "ghilliesuit:poise"), 1);
		assertAttackDamage(helper, player, 1.5F, true);
		player.attackCharge = 0.5F;
		assertAttackDamage(helper, player, 0.4F, false);
		player.attackCharge = 1.0F;
		player.setPose(Pose.STANDING);
		assertAttackDamage(helper, player, 1.0F, false);
		player.setPose(Pose.CROUCHING);
		player.getFoodData().setFoodLevel(0);
		assertAttackDamage(helper, player, 1.0F, false);
		player.getFoodData().setFoodLevel(20);
		player.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
		assertAttackDamage(helper, player, 1.0F, false);
		player.setPose(Pose.STANDING);
		player.setOnGround(false);
		player.fallDistance = 1.0;
		assertAttackDamage(helper, player, 1.5F, true);
		helper.succeed();
	}

	@GameTest(structure = "minecraft:empty")
	public void swiftSneakNeverOfferedAtTable(GameTestHelper helper) throws ReflectiveOperationException {
		var enchantmentMenu = new EnchantmentMenu(0, serverPlayer(helper).getInventory());
		var selectEnchantmentsMethod = enchantmentMenu.getClass().getDeclaredMethod("getEnchantmentList", RegistryAccess.class, ItemStack.class, int.class, int.class);
		selectEnchantmentsMethod.setAccessible(true);
		for (int enchantmentSeed = 0; enchantmentSeed < 100; enchantmentSeed++) {
			enchantmentMenu.setData(ENCHANTMENT_SEED_DATA_SLOT, enchantmentSeed);
			for (Item item : List.of(Ghilliesuit.GHILLIE_PANTS, Items.DIAMOND_LEGGINGS, Items.BOOK, Ghilliesuit.GHILLIE_TUNIC)) {
				var enchantmentOffers = (List<?>) selectEnchantmentsMethod.invoke(enchantmentMenu, helper.getLevel().registryAccess(), new ItemStack(item), 2, 30);
				for (Object entry : enchantmentOffers) {
					var enchantmentOffer = (EnchantmentInstance) entry;
					helper.assertFalse(enchantmentOffer.enchantment().is(Enchantments.SWIFT_SNEAK), "Swift Sneak must only be applied from a book, never offered at the table");
				}
			}
		}
		helper.succeed();
	}

	private static boolean isAllowedTableEnchantment(Item item, EnchantmentInstance enchantmentOffer) {
		Holder<Enchantment> enchantment = enchantmentOffer.enchantment();
		return enchantment.is(Ghilliesuit.COMFORT)
			|| enchantment.is(Enchantments.UNBREAKING)
			|| (item == Ghilliesuit.GHILLIE_TUNIC && enchantment.is(Ghilliesuit.POISE));
	}

	private static void assertAttackDamage(GameTestHelper helper, CombatPlayer player, float expectedDamage, boolean criticalHit) {
		LivingEntity target = helper.spawn(EntityTypes.COW, new Vec3(0.5, 1.0, 0.5));
		float healthBeforeAttack = target.getHealth();
		player.criticalHit = false;
		player.attack(target);
		helper.assertTrue(Math.abs(healthBeforeAttack - target.getHealth() - expectedDamage) < 0.001F, "expected damage " + expectedDamage + ", got " + (healthBeforeAttack - target.getHealth()));
		helper.assertTrue(player.criticalHit == criticalHit, "critical feedback must match the damage bonus");
		target.discard();
	}

	private static class CombatPlayer extends Player {
		private float attackCharge = 1.0F;
		private boolean criticalHit;

		private CombatPlayer(ServerLevel level) {
			super(level, new GameProfile(UUID.randomUUID(), "PoiseTest"));
		}

		@Override
		public GameType gameMode() {
			return GameType.SURVIVAL;
		}

		@Override
		public boolean isClientAuthoritative() {
			return false;
		}

		@Override
		public float getAttackStrengthScale(float partialTick) {
			return attackCharge;
		}

		@Override
		public void crit(Entity target) {
			criticalHit = true;
		}
	}

	private static ServerPlayer serverPlayer(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		ServerPlayer player = new ServerPlayer(level.getServer(), level, new GameProfile(UUID.randomUUID(), "GhillieTest"), ClientInformation.createDefault());
		player.getFoodData().setFoodLevel(20);
		player.getFoodData().setSaturation(20.0F);
		player.getAbilities().invulnerable = false;
		return player;
	}

	private static void equipFullSuit(Player player) {
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Ghilliesuit.GHILLIE_HOOD));
		player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Ghilliesuit.GHILLIE_TUNIC));
		player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Ghilliesuit.GHILLIE_PANTS));
		player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Ghilliesuit.GHILLIE_BOOTS));
	}

	private static void enchantWithComfort(GameTestHelper helper, ServerPlayer player) {
		Holder<Enchantment> comfort = enchantmentHolder(helper, "ghilliesuit:comfort");
		player.getItemBySlot(EquipmentSlot.HEAD).enchant(comfort, 1);
		player.getItemBySlot(EquipmentSlot.CHEST).enchant(comfort, 1);
		player.getItemBySlot(EquipmentSlot.LEGS).enchant(comfort, 1);
		player.getItemBySlot(EquipmentSlot.FEET).enchant(comfort, 1);
	}

	private static Enchantment enchantment(GameTestHelper helper, String enchantmentId) {
		return enchantmentHolder(helper, enchantmentId).value();
	}

	private static Holder<Enchantment> enchantmentHolder(GameTestHelper helper, String enchantmentId) {
		Registry<Enchantment> enchantments = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		return enchantments.getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(enchantmentId)));
	}

	private static ItemStack weave() {
		return new ItemStack(Ghilliesuit.MOSS_WEAVE);
	}

	private static Recipe<?> recipe(GameTestHelper helper, String path) {
		ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("ghilliesuit", path));
		Optional<Recipe<?>> recipe = helper.getLevel().getServer().getRecipeManager().byKey(recipeKey).map(holder -> holder.value());
		helper.assertTrue(recipe.isPresent(), "ghilliesuit:" + path + " recipe should be loaded");
		return recipe.orElseThrow();
	}

	private static void assertDetectionMultiplier(GameTestHelper helper, Zombie zombie, int ghilliePieceCount, double expectedMultiplier) {
		double actualMultiplier = zombie.getVisibilityPercent(null);
		helper.assertTrue(
			Math.abs(actualMultiplier - expectedMultiplier) < 1.0E-6,
			"with " + ghilliePieceCount + " pieces worn the detection multiplier should be " + expectedMultiplier + " but was " + actualMultiplier
		);
	}
}
