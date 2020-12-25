package com.github.enstroe.armour;

import com.github.enstroe.items.GhillieWeaveItem;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum ModArmourMaterials implements ArmorMaterial {

    GHILLIE(); //GHILLIE Variant
    //Allows for expansion with future armour sets


    private static final int[] BASE_DURABILITY = new int[] {55, 80, 75, 65}; //Same durability as leather
    private static final int[] PROTECTION_VALUES = new int[] {1, 2, 3, 1}; //Same protection as leather
 
    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()];
    }
 
    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return PROTECTION_VALUES[slot.getEntitySlotId()];
    }
 
    @Override
    public int getEnchantability() {
        return ArmorMaterials.LEATHER.getEnchantability();
    }
 
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    }
 
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(new GhillieWeaveItem(new Item.Settings()));
    }
 
    @Override
    public String getName() {
        return "ghillie";
    }
 
    @Override
    public float getToughness() {
        return ArmorMaterials.LEATHER.getToughness();
    }
 
    @Override
    public float getKnockbackResistance() {
        return ArmorMaterials.LEATHER.getKnockbackResistance();
    }
}