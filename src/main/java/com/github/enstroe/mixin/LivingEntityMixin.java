package com.github.enstroe.mixin;

import com.github.enstroe.Ghilliesuit;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)

public abstract class LivingEntityMixin extends Entity {

    //Mandatory constructor for being an abstract extension (Never gonna be called)
    protected LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @Shadow @Final private DefaultedList<ItemStack> equippedArmor;

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) { 
        if(isAlive()) { //First asserts that entitty is alive
            ItemStack helmetStack       = equippedArmor.get(0);
            ItemStack chestplateStack   = equippedArmor.get(1);
            ItemStack leggingsStack     = equippedArmor.get(2);
            ItemStack bootsStack        = equippedArmor.get(3);

            boolean hasHelmet = (helmetStack.getItem().equals(Ghilliesuit.GHILLIE_HELMET));
            boolean hasChestplate = (chestplateStack.getItem().equals(Ghilliesuit.GHILLIE_CHESTPLATE));
            boolean hasLeggings = (leggingsStack.getItem().equals(Ghilliesuit.GHILLIE_LEGGINGS));
            boolean hasBoots = (bootsStack.getItem().equals(Ghilliesuit.GHILLIE_BOOTS));

            //Checks that whole 'Ghillie Suit' is eqquipped
            boolean hasArmour = (hasHelmet && hasChestplate && hasLeggings && hasBoots);

            if(hasHelmet) {
                System.out.println("DEBUG : Has Helmet");
            }
            if(hasChestplate) {
                System.out.println("DEBUG : Has Chestplate");
            }
            if(hasLeggings) {
                System.out.println("DEBUG : Has Leggings");
            }
            if(hasBoots) {
                System.out.println("DEBUG : Has Boots");
            }

            if(hasArmour) {
                System.out.println("DEBUG : Has Armour");
                
                boolean isSneaking = isSneaking(); //ghillie suit only works if sneaking
                if(isSneaking) {
                    System.out.println("DEBUG : Is Sneaking");
                } else {
                    System.out.println("DEBUG : Is Not Sneaking");
                }
                setInvisible(isSneaking);
            }
            else {
                System.out.println("DEBUG : Does Not Have Armour");
            }
        }
    }
}
