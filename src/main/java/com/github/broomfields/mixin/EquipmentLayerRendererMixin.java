package com.github.broomfields.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.broomfields.Ghilliesuit;

// Vanilla renders armour on invisible entities opaquely, which would show the ghillie suit as a
// solid outline on top of the translucent body ghost. While a ghillie piece is worn by an
// invisible entity, use the same translucent render type and alpha as the body ghost instead.
@Environment(EnvType.CLIENT)
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {

	private static final int GHOST_COLOR = 0x26FFFFFF; // matches the body ghost's translucent alpha

	private static final String RENDER_LAYERS_11_ARG = "renderLayers("
		+ "Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;"
		+ "Lnet/minecraft/resources/ResourceKey;"
		+ "Lnet/minecraft/client/model/Model;"
		+ "Ljava/lang/Object;"
		+ "Lnet/minecraft/world/item/ItemStack;"
		+ "Lcom/mojang/blaze3d/vertex/PoseStack;"
		+ "Lnet/minecraft/client/renderer/SubmitNodeCollector;"
		+ "I"
		+ "Lnet/minecraft/resources/Identifier;"
		+ "II)V";

	@Unique
	private static boolean ghilliesuitTranslucentArmor;

	@Inject(method = RENDER_LAYERS_11_ARG, at = @At("HEAD"))
	private void ghilliesuit_renderLayers(
		EquipmentClientInfo.LayerType layerType,
		ResourceKey<?> equipmentAssetId,
		Model<?> model,
		Object state,
		ItemStack itemStack,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		Identifier playerTextureOverride,
		int outlineColor,
		int order,
		CallbackInfo ci
	) {
		ghilliesuitTranslucentArmor = isGhillieArmor(itemStack) && state instanceof LivingEntityRenderState les && les.isInvisible;
	}

	@Redirect(
		method = RENDER_LAYERS_11_ARG,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private static RenderType ghilliesuit_armorRenderType(Identifier texture) {
		return ghilliesuitTranslucentArmor ? RenderTypes.entityTranslucentCullItemTarget(texture) : RenderTypes.armorCutoutNoCull(texture);
	}

	@Inject(method = "getColorForLayer", at = @At("RETURN"), cancellable = true)
	private static void ghilliesuit_getColorForLayer(EquipmentClientInfo.Layer layer, int dyeColor, CallbackInfoReturnable<Integer> cir) {
		if (ghilliesuitTranslucentArmor) {
			cir.setReturnValue(ARGB.multiply(cir.getReturnValueI(), GHOST_COLOR));
		}
	}

	private static boolean isGhillieArmor(ItemStack stack) {
		return stack.is(Ghilliesuit.GHILLIE_HELMET)
			|| stack.is(Ghilliesuit.GHILLIE_CHESTPLATE)
			|| stack.is(Ghilliesuit.GHILLIE_LEGGINGS)
			|| stack.is(Ghilliesuit.GHILLIE_BOOTS);
	}
}
