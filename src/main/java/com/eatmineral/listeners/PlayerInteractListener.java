package com.eatmineral.listeners;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInteractListener implements Listener {
    
    private final EatMineral plugin;
    
    public PlayerInteractListener(EatMineral plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 只處理右鍵點擊空氣或方塊
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // 只處理主手，避免重複觸發
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // 檢查物品是否存在
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        
        // 檢查玩家是否有權限
        if (!player.hasPermission("eatmineral.use")) {
            return;
        }
        
        // 檢查物品是否有 NBT 標籤標記為可食用
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        
        // 檢查是否有可食用標記
        if (!meta.getPersistentDataContainer().has(plugin.getMineralManager().getEatableKey(), PersistentDataType.BYTE)) {
            return;
        }
        
        // 檢查玩家是否飢餓（如果滿飽食度則無法食用）
        if (player.getFoodLevel() >= 20) {
            player.sendMessage(plugin.getMessageUtil().getMessage("not-hungry"));
            event.setCancelled(true);
            return;
        }
        
        // 取消事件，防止預設行為
        event.setCancelled(true);
        
        // 獲取 NBT 中的食物數據
        Integer foodLevel = meta.getPersistentDataContainer().get(
            plugin.getMineralManager().getFoodLevelKey(), 
            PersistentDataType.INTEGER
        );
        Float saturation = meta.getPersistentDataContainer().get(
            plugin.getMineralManager().getSaturationKey(), 
            PersistentDataType.FLOAT
        );
        
        if (foodLevel == null || saturation == null) {
            return;
        }
        
        // 播放食用動畫和音效
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        
        // 延遲執行食用效果（模擬食用時間）
        new BukkitRunnable() {
            int ticks = 0;
            
            @Override
            public void run() {
                if (ticks < 32) {
                    // 每隔幾個 tick 播放食用音效
                    if (ticks % 4 == 0) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EAT, 0.5f + (float)Math.random() * 0.2f, 0.8f + (float)Math.random() * 0.4f);
                    }
                    ticks++;
                } else {
                    // 食用完成
                    cancel();
                    
                    // 檢查物品是否還在手中
                    ItemStack currentItem = player.getInventory().getItemInMainHand();
                    if (currentItem.equals(item)) {
                        // 移除物品
                        if (currentItem.getAmount() > 1) {
                            currentItem.setAmount(currentItem.getAmount() - 1);
                        } else {
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        }
                        
                        // 給予食物效果
                        int currentFoodLevel = player.getFoodLevel();
                        float currentSaturation = player.getSaturation();
                        
                        int newFoodLevel = Math.min(20, currentFoodLevel + foodLevel);
                        float newSaturation = Math.min(20.0f, currentSaturation + saturation);
                        
                        player.setFoodLevel(newFoodLevel);
                        player.setSaturation(newSaturation);
                        
                        // 播放食用完成音效
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
                        
                        // 發送消息
                        String displayName = meta.getDisplayName() != null ? meta.getDisplayName() : item.getType().name();
                        String message = plugin.getMessageUtil().getMessage("mineral.eaten", 
                            displayName, 
                            String.valueOf(foodLevel),
                            String.valueOf(saturation));
                        player.sendMessage(message);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
