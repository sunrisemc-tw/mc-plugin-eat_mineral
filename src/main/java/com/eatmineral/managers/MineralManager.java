package com.eatmineral.managers;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MineralManager {
    
    private final EatMineral plugin;
    private final Map<Material, EatableMineral> eatableMinerals;
    
    // NamespacedKey 用於 NBT 標籤
    private final NamespacedKey eatableKey;
    private final NamespacedKey foodLevelKey;
    private final NamespacedKey saturationKey;
    
    public MineralManager(EatMineral plugin) {
        this.plugin = plugin;
        this.eatableMinerals = new HashMap<>();
        
        // 初始化 NamespacedKey
        this.eatableKey = new NamespacedKey(plugin, EatableMineral.EATABLE_KEY);
        this.foodLevelKey = new NamespacedKey(plugin, EatableMineral.FOOD_LEVEL_KEY);
        this.saturationKey = new NamespacedKey(plugin, EatableMineral.SATURATION_KEY);
    }
    
    public void initializeMinerals() {
        // 綠寶石
        registerMineral(Material.DIAMOND, "§6可食用礦物-綠寶石", "§6一塊閃著祖母綠般光芒的綠寶石，可以直接食用，村民看到你吃掉它大概率會哭！", 12, 1.6f);

        // 獄髓
        registerMineral(Material.DIAMOND, "§j可食用礦物-獄髓錠", "§j一顆閃閃發光的獄髓錠，可以直接食用，你錢真是太多了！", 10, 1.4f);

        // 鑽石
        registerMineral(Material.DIAMOND, "§b可食用礦物-鑽石錠", "§b一顆閃閃發光的鑽石錠，可以直接食用，你有錢阿吃這玩意！", 8, 1.2f);
        
        // 黃金
        registerMineral(Material.GOLD_INGOT, "§6可食用礦物-黃金錠", "§6一塊金黃色的黃金錠，可以直接食用，吃了我猜你大概會重金屬中毒！", 6, 1.0f);
        
        // 煤炭
        registerMineral(Material.COAL, "§8可食用礦物-煤炭", "§8一塊黑黑的煤炭，可以直接食用，吃了之後你嘴巴都黑黑的！", 4, 0.8f);
        
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
    
    /**
     * 創建轉換後的可食用物品
     * @param eatableMineral 可食用礦物
     * @param amount 數量
     * @return 帶有 NBT 標籤的可食用物品
     */
    public ItemStack createConvertedItem(EatableMineral eatableMineral, int amount) {
        ItemStack item = eatableMineral.createEatableItem(eatableKey, foodLevelKey, saturationKey);
        item.setAmount(amount);
        return item;
    }
    
    /**
     * 獲取 NamespacedKey
     */
    public NamespacedKey getEatableKey() {
        return eatableKey;
    }
    
    public NamespacedKey getFoodLevelKey() {
        return foodLevelKey;
    }
    
    public NamespacedKey getSaturationKey() {
        return saturationKey;
    }
}
