package com.eclipse.cit.proxy;

import com.eclipse.cit.CITProxyPack;
import com.eclipse.cit.CITRecrafted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.List;

public class ClientProxy implements IProxy {
    public static List<IResourcePack> defaultPacks;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        try {
            defaultPacks = ObfuscationReflectionHelper.getPrivateValue(
                    Minecraft.class,
                    Minecraft.getMinecraft(),
                    "field_110449_ao"
            );
            CITProxyPack proxyPack = new CITProxyPack(defaultPacks);
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            if (manager instanceof SimpleReloadableResourceManager) {
                ((SimpleReloadableResourceManager) manager).reloadResourcePack(proxyPack);
            }
            defaultPacks.add(proxyPack);
        } catch (Exception e) {
            CITRecrafted.LOGGER.error("Failed to inject CIT Proxy Pack into default list!", e);
        }
    }
}