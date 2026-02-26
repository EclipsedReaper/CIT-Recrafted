package com.eclipse.cit.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public ModelCache(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}