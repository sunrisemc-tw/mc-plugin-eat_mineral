package com.eatmineral.managers;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MineralManager {
    
    private final EatMineral plugin;
    private final Map<Material, EatableMineral> eatableMinerals;
    
    // NamespacedKey 用於 NBT 標籤
    private final NamespacedKey eatableKey;
    private final NamespacedKey foodLevelKey;
    private final NamespacedKey saturationKey;
    
    public MineralManager(EatMineral plugin) {
        this.plugin = plugin;
        this.eatableMinerals = new ConcurrentHashMap<>();
        
        // 初始化 NamespacedKey
        this.eatableKey = new NamespacedKey(plugin, EatableMineral.EATABLE_KEY);
        this.foodLevelKey = new NamespacedKey(plugin, EatableMineral.FOOD_LEVEL_KEY);
        this.saturationKey = new NamespacedKey(plugin, EatableMineral.SATURATION_KEY);
    }
    
    public void initializeMinerals() {
        // 獄髓
        registerMineral(Material.NETHERITE_INGOT, "§j可食用礦物-獄髓錠", "§j一顆閃閃發光的獄髓錠，可以直接食用，你錢真是太多了！", 14, 1.8f);

        // 鑽石
        registerMineral(Material.DIAMOND, "§b可食用礦物-鑽石", "§b一顆閃閃發光的鑽石，可以直接食用，你有錢阿吃這玩意！", 12, 1.4f);
        
        // 綠寶石
        registerMineral(Material.EMERALD, "§6可食用礦物-綠寶石", "§6一塊閃著祖母綠般光芒的綠寶石，可以直接食用，村民看到你吃掉它大概率會哭！", 8, 1.2f);
        
        // 黃金
        registerMineral(Material.GOLD_INGOT, "§6可食用礦物-黃金錠", "§6一塊金黃色的黃金錠，可以直接食用，吃了我猜你大概會重金屬中毒！", 7, 1.0f);
        
        // 煤炭
        registerMineral(Material.COAL, "§8可食用礦物-煤炭", "§8一塊黑黑的煤炭，可以直接食用，吃了之後你嘴巴都黑黑的！", 4, 0.8f);
        
        // 鐵
        registerMineral(Material.IRON_INGOT, "§7可食用礦物-鐵錠", "§7堅硬的鐵錠，可以直接食用，非常的補充你的鐵質！", 5, 0.9f);
        
        // 銅
        registerMineral(Material.COPPER_INGOT, "§c可食用礦物-銅錠", "§c一顆溫暖的銅錠，可以直接食用，吃了就中毒了吧！？", 3, 0.7f);
        
        // 青金石
        registerMineral(Material.LAPIS_LAZULI, "§t可食用礦物-青金石", "§d一個看起來有深邃藍色的青金石，可以直接食用，在古代可比黃金貴多了！", 5, 0.8f);

        // 紫水晶
        registerMineral(Material.AMETHYST_SHARD, "§d可食用礦物-紫水晶碎片", "§d一個閃閃發光的紫水晶碎片，可以直接食用，吃起來不會扎嘴嗎！", 6, 0.9f);
        
        // 紅石
        registerMineral(Material.REDSTONE, "§c可食用礦物-紅石", "§c一顆充滿能量的紅石，可以直接食用，你感覺到能量在體內流竄！", 6, 0.9f);
        
        // 鐵粒 - 氯化鈉
        registerMineral(Material.IRON_NUGGET, "§f氯化鈉", "§7補充鹽分很重要，來點氯化鈉", 2, 0.3f);
        
        // 鐵磚 - 潮結的氯化鈉
        registerMineral(Material.IRON_BLOCK, "§7潮結的氯化鈉", "§8真是的，怎麼受潮變成一整塊了啦！", 8, 1.0f);
        
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
        return new ConcurrentHashMap<>(eatableMinerals);
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
