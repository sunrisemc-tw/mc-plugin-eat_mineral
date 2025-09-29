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
        registerMineral(Material.DIAMOND, "§b可食用礦物-鑽石錠", "§b一顆閃閃發光的鑽石錠，可以直接食用，你有錢阿吃這玩意！", 8, 1.2f);
        
        // 黃金
        registerMineral(Material.GOLD_INGOT, "§6可食用礦物-黃金錠", "§6一塊金黃色的黃金錠，可以直接食用，吃了我猜你大概會重金屬中毒！", 6, 1.0f);
        
        // 煤炭
        registerMineral(Material.COAL, "§8可食用礦物-煤炭錠", "§8一塊黑黑的煤炭錠，可以直接食用，吃了之後你嘴巴都黑黑的！", 4, 0.8f);
        
        // 鐵
        registerMineral(Material.IRON_INGOT, "§7可食用礦物-鐵錠", "§7堅硬的鐵錠，可以直接食用，非常的補充你的鐵質！", 5, 0.9f);
        
        // 銅
        registerMineral(Material.COPPER_INGOT, "§c可食用礦物-銅錠", "§c一顆溫暖的銅錠，可以直接食用，吃了就中毒了吧！？", 3, 0.7f);
        
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
