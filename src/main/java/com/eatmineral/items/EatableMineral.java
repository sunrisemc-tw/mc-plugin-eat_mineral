package com.eatmineral.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class EatableMineral {
    
    private final Material material;
    private final String name;
    private final String description;
    private final int foodLevel;
    private final float saturation;
    
    // NBT 鍵值，用於標記物品為可食用
    public static final String EATABLE_KEY = "eatable";
    public static final String FOOD_LEVEL_KEY = "food_level";
    public static final String SATURATION_KEY = "saturation";
    
    public EatableMineral(Material material, String name, String description, int foodLevel, float saturation) {
        this.material = material;
        this.name = name;
        this.description = description;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
    }
    
    public ItemStack createEatableItem(NamespacedKey eatableKey, NamespacedKey foodLevelKey, NamespacedKey saturationKey) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(
                description,
                "",
                "§7飽食度: §a+" + foodLevel,
                "§7飽和度: §a+" + saturation
            ));
            
            // 添加 PersistentDataContainer NBT 標籤，標記物品為可食用
            meta.getPersistentDataContainer().set(eatableKey, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(foodLevelKey, PersistentDataType.INTEGER, foodLevel);
            meta.getPersistentDataContainer().set(saturationKey, PersistentDataType.FLOAT, saturation);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getFoodLevel() {
        return foodLevel;
    }
    
    public float getSaturation() {
        return saturation;
    }
}
