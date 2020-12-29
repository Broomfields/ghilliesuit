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
import net.minecraft.item.Item;
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
            Item helmet       = equippedArmor.get(3).getItem();
            Item chestplate   = equippedArmor.get(2).getItem();
            Item leggings     = equippedArmor.get(1).getItem();
            Item boots        = equippedArmor.get(0).getItem();

            boolean hasHelmet = (helmet.equals(Ghilliesuit.GHILLIE_HELMET));
            boolean hasChestplate = (chestplate.equals(Ghilliesuit.GHILLIE_CHESTPLATE));
            boolean hasLeggings = (leggings.equals(Ghilliesuit.GHILLIE_LEGGINGS));
            boolean hasBoots = (boots.equals(Ghilliesuit.GHILLIE_BOOTS));

            //Checks that whole 'Ghillie Suit' is eqquipped
            boolean hasArmour = (hasHelmet && hasChestplate && hasLeggings && hasBoots);
            if(hasArmour) {
                boolean isSneaking = isSneaking(); //ghillie suit only works if sneaking

                setInvisible(isSneaking);
            }
            else {
                System.out.println("DEBUG : Does Not Have Armour");
            }
        }
    }
}
