package com.eclipse.cit.model;

import com.eclipse.cit.*;
import com.eclipse.cit.parser.CITParser;
import com.eclipse.cit.parser.CITRegistry;
import com.eclipse.cit.parser.CITRule;
import com.eclipse.cit.proxy.ClientProxy;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = net.minecraftforge.fml.relauncher.Side.CLIENT)
public class CITModelBakeListener {

    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        CITParser.load(ClientProxy.defaultPacks);
        for (Item item : CITRegistry.getTargetedItems()) {
            for (CITRule rule : CITRegistry.getRules(item)) {
                if (rule.getModel() != null) {
                    try {
                        Collection<ResourceLocation> textures = ModelLoaderRegistry.getModel(rule.getModel()).getTextures();
                        for (ResourceLocation texture : textures) {
                            event.getMap().registerSprite(texture);
                        }
                    } catch (Exception e) {
                        CITRecrafted.LOGGER.warn("CIT model missing or malformed, skipping textures for: {}", rule.getModel());
                    }
                }
                if (rule.getTexture() != null) {
                    event.getMap().registerSprite(rule.getTexture());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        for (Item item : CITRegistry.getTargetedItems()) {
            if (item == null) continue;
            ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
            IBakedModel vanillaModel = event.getModelRegistry().getObject(loc);
            if (vanillaModel == null) continue;
            event.getModelRegistry().putObject(loc, new CITBakedModel(vanillaModel));
            boolean isHandheld = item instanceof ItemSword ||
                    item instanceof ItemTool ||
                    item instanceof ItemHoe ||
                    item instanceof ItemBow ||
                    item instanceof ItemShears ||
                    item instanceof ItemFishingRod;
            ResourceLocation baseModelLoc = new ResourceLocation(isHandheld ? "item/handheld" : "item/generated");
            for (CITRule rule : CITRegistry.getRules(item)) {
                try {
                    IBakedModel bakedModel;
                    if (rule.getModel() != null) {
                        IModel baseModel = ModelLoaderRegistry.getModel(rule.getModel());
                        if (rule.getTexture() != null) {
                            baseModel = baseModel.retexture(ImmutableMap.of("layer0", rule.getTexture().toString()));
                        }
                        bakedModel = baseModel.bake(baseModel.getDefaultState(), DefaultVertexFormats.ITEM,
                                ModelLoader.defaultTextureGetter());
                    } else {
                        IModel baseModel = ModelLoaderRegistry.getModel(baseModelLoc);
                        IModel retexturedModel = baseModel.retexture(ImmutableMap.of("layer0", rule.getTexture().toString()));
                        bakedModel = retexturedModel.bake(retexturedModel.getDefaultState(), DefaultVertexFormats.ITEM,
                                ModelLoader.defaultTextureGetter());
                    }
                    rule.setBakedModel(bakedModel);
                } catch (Exception e) {
                    CITRecrafted.LOGGER.warn("Failed to bake CIT model (file likely missing): {}", rule.getModel());
                }
            }
        }
    }
}