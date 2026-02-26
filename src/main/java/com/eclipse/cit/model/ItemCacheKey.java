package com.eclipse.cit.model;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class ItemCacheKey {
    private final int damage;
    private final int stackSize;
    private final NBTTagCompound nbtData;

    public ItemCacheKey(ItemStack stack) {
        this.damage = stack.getItemDamage();
        this.stackSize = stack.getCount();
        if (stack.hasTagCompound()) {
            this.nbtData = stack.getTagCompound().copy();
        } else {
            this.nbtData = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemCacheKey that = (ItemCacheKey) o;
        return damage == that.damage && stackSize == that.stackSize && Objects.equals(nbtData, that.nbtData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(damage, stackSize, nbtData);
    }
}
