package com.eatmineral.utils;

import com.eatmineral.EatMineral;
import org.bukkit.ChatColor;

public class MessageUtil {
    
    private final EatMineral plugin;
    
    public MessageUtil(EatMineral plugin) {
        this.plugin = plugin;
    }
    
    public String getMessage(String key, String... placeholders) {
        String message = plugin.getConfig().getString("messages." + key, getDefaultMessage(key));
        
        // 替換佔位符
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + i + "}", placeholders[i]);
        }
        
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private String getDefaultMessage(String key) {
        switch (key) {
            case "no-permission":
                return "&c你沒有權限執行此命令！";
            case "config-reloaded":
                return "&a配置已重新載入！";
            case "usage-give":
                return "&c用法: /eatmineral give <礦物名稱>";
            case "mineral-given":
                return "&a已給予 {0}！";
            case "mineral-not-found":
                return "&c找不到礦物: {0}";
            case "invalid-mineral":
                return "&c無效的礦物名稱: {0}";
            case "mineral.eaten":
                return "&a你食用了 {0}！恢復了 {1} 飽食度和 {2} 飽和度！";
            default:
                return "&c未知的消息鍵: " + key;
        }
    }
}
