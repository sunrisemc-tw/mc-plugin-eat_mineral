package com.eatmineral.managers;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MineralManager {
    
    private final EatMineral plugin;
    private final Map<Material, EatableMineral> eatableMinerals;
    
    public MineralManager(EatMineral plugin) {
        this.plugin = plugin;
        this.eatableMinerals = new HashMap<>();
    }
    
    public void initializeMinerals() {
        // 鑽石
        registerMineral(Material.DIAMOND, "鑽石糖", "§b一顆閃閃發光的鑽石糖，吃下去會讓你感到無比珍貴！", 8, 1.2f);
        
        // 黃金
        registerMineral(Material.GOLD_INGOT, "黃金巧克力", "§6一塊香濃的黃金巧克力，吃下去會讓你感到富有！", 6, 1.0f);
        
        // 煤炭
        registerMineral(Material.COAL, "煤炭餅乾", "§8一塊黑黑的煤炭餅乾，雖然看起來不怎麼樣，但味道還不錯！", 4, 0.8f);
        
        // 鐵
        registerMineral(Material.IRON_INGOT, "鐵質能量棒", "§7一根堅硬的鐵質能量棒，吃下去會讓你感到強壯！", 5, 0.9f);
        
        // 銅
        registerMineral(Material.COPPER_INGOT, "銅製糖果", "§c一顆溫暖的銅製糖果，吃下去會讓你感到溫暖！", 3, 0.7f);
        
        plugin.getLogger().info("已初始化 " + eatableMinerals.size() + " 種可食用礦物！");
    }
    
    private void registerMineral(Material material, String name, String description, int foodLevel, float saturation) {
        EatableMineral eatableMineral = new EatableMineral(material, name, description, foodLevel, saturation);
        eatableMinerals.put(material, eatableMineral);
    }
    
    public EatableMineral getEatableMineral(Material material) {
        return eatableMinerals.get(material);
    }
    
    public boolean isEatableMineral(Material material) {
        return eatableMinerals.containsKey(material);
    }
    
    public Map<Material, EatableMineral> getAllEatableMinerals() {
        return new HashMap<>(eatableMinerals);
    }
    
    public void reloadMinerals() {
        eatableMinerals.clear();
        initializeMinerals();
    }
}
