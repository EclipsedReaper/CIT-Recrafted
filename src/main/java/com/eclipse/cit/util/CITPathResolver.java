package com.eclipse.cit.util;

import net.minecraft.util.ResourceLocation;

public final class CITPathResolver {
    private CITPathResolver() {}

    public static ResourceLocation resolve(String basePath, String targetPath, String... extensionsToStrip) {
        if (targetPath == null || targetPath.trim().isEmpty()) return null;
        String cleaned = targetPath.trim().replace('\\', '/');

        for (String ext : extensionsToStrip) {
            if (cleaned.endsWith(ext)) {
                cleaned = cleaned.substring(0, cleaned.length() - ext.length());
            }
        }

        boolean isAbsolute = cleaned.startsWith("~/") ||
                cleaned.startsWith("/") ||
                cleaned.startsWith("assets/") ||
                cleaned.startsWith("textures/") ||
                cleaned.contains(":");

        if (isAbsolute) {
            if (cleaned.startsWith("~/")) cleaned = cleaned.substring(2);
            if (cleaned.startsWith("/")) cleaned = cleaned.substring(1);
        } else {
            if (cleaned.startsWith("./")) {
                cleaned = cleaned.substring(2);
            }
            String dir = basePath.substring(0, basePath.lastIndexOf('/') + 1);
            cleaned = dir + cleaned;
        }

        String namespace = "minecraft";
        if (cleaned.startsWith("assets/")) {
            String[] parts = cleaned.split("/", 3);
            if (parts.length >= 3) {
                namespace = parts[1];
                cleaned = parts[2];
            }
        } else if (cleaned.contains(":")) {
            String[] parts = cleaned.split(":", 2);
            namespace = parts[0];
            cleaned = parts[1];
        }

        if (cleaned.startsWith("textures/")) {
            cleaned = cleaned.substring(9);
        } else if (cleaned.startsWith("models/")) {
            cleaned = cleaned.substring(7);
        }

        return new ResourceLocation(namespace, cleaned);
    }
}