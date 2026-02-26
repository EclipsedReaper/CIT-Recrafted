package com.eclipse.cit.parser;

import com.eclipse.cit.CITRecrafted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CITParser {
    public static void load(List<IResourcePack> defaultPacks) {
        CITRegistry.clear();

        List<IResourcePack> allPacks = new ArrayList<>(defaultPacks);
        for (ResourcePackRepository.Entry entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
            allPacks.add(entry.getResourcePack());
        }

        for (IResourcePack pack : allPacks) {
            if (pack instanceof AbstractResourcePack abstractPack) {
                File file = abstractPack.resourcePackFile;
                if (file == null || !file.exists()) continue;

                if (file.isDirectory()) {
                    File assetsDir = new File(file, "assets");
                    if (assetsDir.exists() && assetsDir.isDirectory()) {
                        File[] domains = assetsDir.listFiles();
                        if (domains != null) {
                            for (File domainDir : domains) {
                                if (domainDir.isDirectory()) {
                                    readDirectory(new File(domainDir, "optifine/cit"));
                                    readDirectory(new File(domainDir, "mcpatcher/cit"));
                                }
                            }
                        }
                    }
                } else if (file.getName().endsWith(".zip") || file.getName().endsWith(".jar")) {
                    try (ZipFile zip = new ZipFile(file)) {
                        var entries = zip.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.endsWith(".properties") &&
                                    (name.contains("/optifine/cit/") || name.contains("/mcpatcher/cit/"))) {
                                try (InputStream stream = zip.getInputStream(entry)) {
                                    CITRule rule = parseRule(stream, name);
                                    if (rule != null) CITRegistry.register(rule);
                                } catch (Exception e) {
                                    CITRecrafted.LOGGER.error("Failed to parse CIT inside zip: {}", name, e);
                                }
                            }
                        }
                    } catch (IOException e) {
                        CITRecrafted.LOGGER.error("Failed to read resource pack zip: {}", file.getName(), e);
                    }
                }
            }
        }
        CITRegistry.sortRules();
    }

    private static void readDirectory(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                readDirectory(file);
            } else if (file.getName().endsWith(".properties")) {

                try (FileInputStream stream = new FileInputStream(file)) {
                    String absolutePath = file.getAbsolutePath().replace('\\', '/');
                    int assetsIndex = absolutePath.indexOf("assets/");
                    String relativePath = assetsIndex != -1 ? absolutePath.substring(assetsIndex) : absolutePath;

                    CITRule rule = parseRule(stream, relativePath);
                    if (rule != null) CITRegistry.register(rule);
                } catch (IOException e) {
                    CITRecrafted.LOGGER.error("Failed to read CIT file: {}", file.getName(), e);
                }
            }
        }
    }

    private static CITRule parseRule(InputStream stream, String filePath) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        CITRule rule = new CITRule(properties, filePath);
        if (rule.getTargetItems().isEmpty()) {
            return null;
        }
        return rule;
    }
}
