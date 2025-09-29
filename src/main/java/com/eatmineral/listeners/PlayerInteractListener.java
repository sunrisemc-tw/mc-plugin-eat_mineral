package com.eatmineral.listeners;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    
    private final EatMineral plugin;
    
    public PlayerInteractListener(EatMineral plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // 檢查玩家是否有權限
        if (!player.hasPermission("eatmineral.use")) {
            return;
        }
        
        // 檢查是否為可食用的礦物
        Material material = item.getType();
        EatableMineral eatableMineral = plugin.getMineralManager().getEatableMineral(material);
        
        if (eatableMineral != null) {
            // 取消原來的食用效果
            event.setCancelled(true);
            
            // 移除物品
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
            
            // 給予食物效果
            int currentFoodLevel = player.getFoodLevel();
            float currentSaturation = player.getSaturation();
            
            int newFoodLevel = Math.min(20, currentFoodLevel + eatableMineral.getFoodLevel());
            float newSaturation = Math.min(20.0f, currentSaturation + eatableMineral.getSaturation());
            
            player.setFoodLevel(newFoodLevel);
            player.setSaturation(newSaturation);
            
            // 發送消息
            String message = plugin.getMessageUtil().getMessage("mineral.eaten", 
                eatableMineral.getName(), 
                String.valueOf(eatableMineral.getFoodLevel()),
                String.valueOf(eatableMineral.getSaturation()));
            player.sendMessage(message);
            
            // 播放音效
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);
        }
    }
}
