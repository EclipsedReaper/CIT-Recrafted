package com.eclipse.cit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CITProxyPack implements IResourcePack {
    private final List<IResourcePack> defaultPacks;

    public CITProxyPack(List<IResourcePack> defaultPacks) {
        this.defaultPacks = defaultPacks;
    }

    private boolean isCITResource(ResourceLocation location) {
        String path = location.getPath();
        return path.startsWith("models/optifine/cit/") ||
                path.startsWith("models/mcpatcher/cit/") ||
                path.startsWith("textures/optifine/cit/") ||
                path.startsWith("textures/mcpatcher/cit/");
    }

    private ResourceLocation cleanPath(ResourceLocation loc) {
        String path = loc.getPath();
        if (path.startsWith("models/")) {
            return new ResourceLocation(loc.getNamespace(), path.substring(7));
        } else if (path.startsWith("textures/")) {
            return new ResourceLocation(loc.getNamespace(), path.substring(9));
        }
        return loc;
    }

    @Nullable
    private IResourcePack findHandlingPack(ResourceLocation realLoc) {
        List<ResourcePackRepository.Entry> entries = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
        for (int i = entries.size() - 1; i >= 0; i--) {
            IResourcePack pack = entries.get(i).getResourcePack();
            if (pack.resourceExists(realLoc)) {
                return pack;
            }
        }
        if (defaultPacks != null) {
            for (IResourcePack pack : defaultPacks) {
                if (pack != this && pack.resourceExists(realLoc)) {
                    return pack;
                }
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        if (isCITResource(location)) {
            ResourceLocation realLoc = cleanPath(location);
            IResourcePack handlingPack = findHandlingPack(realLoc);
            if (handlingPack != null) {
                return handlingPack.getInputStream(realLoc);
            }
        }
        throw new FileNotFoundException(location.toString());
    }
    @Override
    public boolean resourceExists(ResourceLocation location) {
        if (isCITResource(location)) {
            return findHandlingPack(cleanPath(location)) != null;
        }
        return false;
    }

    @Override
    public Set<String> getResourceDomains() {
        Set<String> domains = new HashSet<>();
        for (ResourcePackRepository.Entry entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
            domains.addAll(entry.getResourcePack().getResourceDomains());
        }
        for (IResourcePack pack : defaultPacks) {
            if (pack != this) {
                domains.addAll(pack.getResourceDomains());
            }
        }
        return domains;
    }
    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return null;
    }
    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }
    @Override
    public String getPackName() {
        return "CIT Proxy";
    }
}