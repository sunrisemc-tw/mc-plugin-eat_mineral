package com.eatmineral.commands;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConvertCommand implements CommandExecutor {
    
    private final EatMineral plugin;
    
    public ConvertCommand(EatMineral plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 檢查是否為玩家
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        // 檢查權限
        if (!player.hasPermission("eatmineral.convert")) {
            player.sendMessage(plugin.getMessageUtil().getMessage("no-permission"));
            return true;
        }
        
        // 獲取玩家手中的物品
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        // 檢查手中是否有物品
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(plugin.getMessageUtil().getMessage("no-item-in-hand"));
            return true;
        }
        
        // 檢查是否為可轉換的礦物
        Material material = itemInHand.getType();
        EatableMineral eatableMineral = plugin.getMineralManager().getEatableMineral(material);
        
        if (eatableMineral == null) {
            player.sendMessage(plugin.getMessageUtil().getMessage("cannot-convert", material.name()));
            return true;
        }
        
        // 檢查物品數量
        if (itemInHand.getAmount() <= 0) {
            player.sendMessage(plugin.getMessageUtil().getMessage("no-item-in-hand"));
            return true;
        }
        
        // 轉換物品
        ItemStack convertedItem = eatableMineral.createEatableItem();
        convertedItem.setAmount(itemInHand.getAmount());
        
        // 替換手中的物品
        player.getInventory().setItemInMainHand(convertedItem);
        
        // 發送成功消息
        player.sendMessage(plugin.getMessageUtil().getMessage("convert-success", 
            eatableMineral.getName(), 
            String.valueOf(itemInHand.getAmount())));
        
        // 播放轉換音效
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
        
        return true;
    }
}
