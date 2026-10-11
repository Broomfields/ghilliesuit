package com.github.broomfields.mixin;

import java.util.function.Function;

import com.github.broomfields.Ghilliesuit;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(EquipmentLayerRenderer.class)
public abstract class EquipmentLayerRendererMixin {

	private static final int GHOST_COLOR = 0x26FFFFFF;

	private static final String RENDER_LAYERS_METHOD = "renderLayers("
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
	private static final Function<Identifier, RenderType> ghilliesuitTranslucentArmorType = Util.memoize(texture -> RenderType.create(
		"ghilliesuit_translucent_armor",
		RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
			.withTexture("Sampler0", texture)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.withForcedSolidModelPhase()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup()
	));

	@Unique
	private static boolean ghilliesuitTranslucentArmor;

	@Inject(method = RENDER_LAYERS_METHOD, at = @At("HEAD"))
	private void ghilliesuit_renderLayers(
		EquipmentClientInfo.LayerType layerType,
		ResourceKey<?> equipmentAssetId,
		Model<?> model,
		Object renderState,
		ItemStack itemStack,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		Identifier playerTextureOverride,
		int outlineColor,
		int order,
		CallbackInfo callback
	) {
		ghilliesuitTranslucentArmor = isGhillieArmor(itemStack) && renderState instanceof LivingEntityRenderState livingRenderState && livingRenderState.isInvisible;
	}

	@Inject(method = RENDER_LAYERS_METHOD, at = @At("RETURN"))
	private <RenderState> void ghilliesuit_renderSeparateGlint(
		EquipmentClientInfo.LayerType layerType,
		ResourceKey<?> equipmentAssetId,
		Model<? super RenderState> model,
		RenderState renderState,
		ItemStack itemStack,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		int lightCoords,
		Identifier playerTextureOverride,
		int outlineColor,
		int order,
		CallbackInfo callback
	) {
		if (ghilliesuitTranslucentArmor && itemStack.hasFoil() && !itemStack.has(DataComponents.TRIM)) {
			submitNodeCollector.order(order + 1).submitModel(model, renderState, poseStack, RenderTypes.trimmedArmorGlint(), lightCoords, OverlayTexture.NO_OVERLAY, GHOST_COLOR, null, 0);
		}
	}

	@Redirect(
		method = RENDER_LAYERS_METHOD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCull(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private static RenderType ghilliesuit_armorRenderType(Identifier texture) {
		return ghilliesuitTranslucentArmor ? ghilliesuitTranslucentArmorType.apply(texture) : RenderTypes.armorCutoutNoCull(texture);
	}

	@Redirect(
		method = RENDER_LAYERS_METHOD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorCutoutNoCullGlint(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"
		)
	)
	private static RenderType ghilliesuit_armorGlintRenderType(Identifier texture) {
		return ghilliesuitTranslucentArmor ? ghilliesuitTranslucentArmorType.apply(texture) : RenderTypes.armorCutoutNoCullGlint(texture);
	}

	@Inject(method = "getColorForLayer", at = @At("RETURN"), cancellable = true)
	private static void ghilliesuit_getColorForLayer(EquipmentClientInfo.Layer layer, int dyeColor, CallbackInfoReturnable<Integer> callback) {
		if (ghilliesuitTranslucentArmor) {
			callback.setReturnValue(ARGB.multiply(callback.getReturnValueI(), GHOST_COLOR));
		}
	}

	private static boolean isGhillieArmor(ItemStack armorStack) {
		return armorStack.is(Ghilliesuit.GHILLIE_HOOD)
			|| armorStack.is(Ghilliesuit.GHILLIE_TUNIC)
			|| armorStack.is(Ghilliesuit.GHILLIE_PANTS)
			|| armorStack.is(Ghilliesuit.GHILLIE_BOOTS);
	}
}
