package com.github.broomfields.gametest;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.phys.Vec3;

import com.github.broomfields.Ghilliesuit;

public class GhillieSuitGameTest {

	// Each ghillie piece worn reduces the detection multiplier by 1/8, so a full suit halves it
	// (the standard hostile follow range is 16 blocks, giving 16 -> 8).
	@GameTest(structure = "minecraft:empty")
	public void eachPieceReducesDetectionMultiplier(GameTestHelper helper) {
		Zombie zombie = helper.spawn(EntityTypes.ZOMBIE, new Vec3(0.5, 1.0, 0.5));
		assertMultiplier(helper, zombie, 0, 1.0);
		zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Ghilliesuit.GHILLIE_HOOD));
		assertMultiplier(helper, zombie, 1, 7.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Ghilliesuit.GHILLIE_TUNIC));
		assertMultiplier(helper, zombie, 2, 6.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Ghilliesuit.GHILLIE_PANTS));
		assertMultiplier(helper, zombie, 3, 5.0 / 8.0);
		zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(Ghilliesuit.GHILLIE_BOOTS));
		assertMultiplier(helper, zombie, 4, 4.0 / 8.0);
		helper.succeed();
	}

	// 2 moss blocks + 2 string craft 2 moss weave (shapeless).
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

	// 5 moss weave in the standard helmet pattern craft a ghillie hood.
	@GameTest(structure = "minecraft:empty")
	public void ghillieHelmetRecipeCrafts(GameTestHelper helper) {
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

	private static ItemStack weave() {
		return new ItemStack(Ghilliesuit.MOSS_WEAVE);
	}

	private static Recipe<?> recipe(GameTestHelper helper, String path) {
		ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("ghilliesuit", path));
		Optional<Recipe<?>> recipe = helper.getLevel().getServer().getRecipeManager().byKey(key).map(holder -> holder.value());
		helper.assertTrue(recipe.isPresent(), "ghilliesuit:" + path + " recipe should be loaded");
		return recipe.orElseThrow();
	}

	private static void assertMultiplier(GameTestHelper helper, Zombie zombie, int pieces, double expected) {
		double actual = zombie.getVisibilityPercent(null);
		helper.assertTrue(
			Math.abs(actual - expected) < 1.0E-6,
			"with " + pieces + " pieces worn the detection multiplier should be " + expected + " but was " + actual
		);
	}
}
