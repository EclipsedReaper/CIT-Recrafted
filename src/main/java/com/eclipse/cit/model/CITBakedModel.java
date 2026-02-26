package com.eclipse.cit.model;

import com.eclipse.cit.parser.CITRegistry;
import com.eclipse.cit.parser.CITRule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class CITBakedModel implements IBakedModel {
    private final ModelCache<ItemCacheKey, IBakedModel> modelCache = new ModelCache<>(500);
    private final IBakedModel baseModel;
    private final ItemOverrideList overrideList;

    CITBakedModel(IBakedModel baseModel) {
        this.baseModel = baseModel;
        this.overrideList = new ItemOverrideList(java.util.Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                ItemCacheKey key = new ItemCacheKey(stack);
                IBakedModel resolvedCitModel = modelCache.get(key);
                if (resolvedCitModel == null) {
                    resolvedCitModel = baseModel;
                    if (CITRegistry.getTargetedItems().contains(stack.getItem())) {
                        for (CITRule rule : CITRegistry.getRules(stack.getItem())) {
                            if (rule.matches(stack) && rule.getBakedModel() != null) {
                                resolvedCitModel = rule.getBakedModel();
                                break;
                            }
                        }
                    }
                    modelCache.put(key, resolvedCitModel);
                }
                if (resolvedCitModel != baseModel) {
                    IBakedModel overridden = resolvedCitModel.getOverrides().handleItemState(resolvedCitModel, stack, world, entity);
                    return new net.minecraftforge.client.model.BakedModelWrapper<>(overridden) {
                        @Override
                        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
                                ItemCameraTransforms.TransformType cameraTransformType) {
                            Pair<? extends IBakedModel, Matrix4f> perspective = overridden.handlePerspective(cameraTransformType);
                            return Pair.of(this, perspective.getRight());
                        }
                    };
                }
                return baseModel.getOverrides().handleItemState(originalModel, stack, world, entity);
            }
        };
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return baseModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Override
    @SuppressWarnings("deprecation")
    public net.minecraft.client.renderer.block.model.ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }

    @Override
    public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType cameraTransformType) {
        org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> perspective = baseModel.handlePerspective(cameraTransformType);
        return org.apache.commons.lang3.tuple.Pair.of(this, perspective.getRight());
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrideList;
    }
}
