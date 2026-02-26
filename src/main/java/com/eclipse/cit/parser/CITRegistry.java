package com.eclipse.cit.parser;

import net.minecraft.item.Item;

import java.util.*;

public class CITRegistry {
    private static final Map<Item, List<CITRule>> RULES = new HashMap<>();

    private CITRegistry() {}

    public static void register(CITRule rule) {
        for (Item item : rule.getTargetItems()) {
            RULES.computeIfAbsent(item, k -> new ArrayList<>()).add(rule);
        }
    }
    public static void sortRules() {
        for (List<CITRule> ruleList : RULES.values()) {
            ruleList.sort(Comparator.comparingInt(CITRule::getWeight).reversed().thenComparing(CITRule::getPath));
        }
    }
    public static List<CITRule> getRules(Item item) { return Collections.unmodifiableList(RULES.getOrDefault(item, Collections.emptyList())); }
    public static Set<Item> getTargetedItems() { return Collections.unmodifiableSet(RULES.keySet()); }
    public static void clear() { RULES.clear(); }
}
