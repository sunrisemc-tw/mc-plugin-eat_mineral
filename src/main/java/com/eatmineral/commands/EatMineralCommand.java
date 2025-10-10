package com.eatmineral.commands;

import com.eatmineral.EatMineral;
import com.eatmineral.items.EatableMineral;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EatMineralCommand implements CommandExecutor {
    
    private final EatMineral plugin;
    
    public EatMineralCommand(EatMineral plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("eatmineral.admin")) {
            sender.sendMessage(plugin.getMessageUtil().getMessage("no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                plugin.getMineralManager().reloadMinerals();
                sender.sendMessage(plugin.getMessageUtil().getMessage("config-reloaded"));
                break;
                
            case "list":
                sendMineralList(sender);
                break;
                
            case "give":
                if (sender instanceof Player && args.length >= 2) {
                    giveMineral((Player) sender, args[1]);
                } else {
                    sender.sendMessage(plugin.getMessageUtil().getMessage("usage-give"));
                }
                break;
                
            default:
                sendHelpMessage(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6=== EatMineral 插件幫助 ===");
        sender.sendMessage("§e/eatmineral reload §7- 重新載入配置");
        sender.sendMessage("§e/eatmineral list §7- 列出所有可食用礦物");
        sender.sendMessage("§e/eatmineral give <礦物名稱> §7- 給予可食用礦物");
    }
    
    private void sendMineralList(CommandSender sender) {
        sender.sendMessage("§6=== 可食用礦物列表 ===");
        Map<org.bukkit.Material, EatableMineral> minerals = plugin.getMineralManager().getAllEatableMinerals();
        
        for (EatableMineral mineral : minerals.values()) {
            sender.sendMessage("§7- " + mineral.getName() + " §8(" + mineral.getMaterial().name() + ")");
        }
    }
    
    private void giveMineral(Player player, String mineralName) {
        try {
            org.bukkit.Material material = org.bukkit.Material.valueOf(mineralName.toUpperCase());
            EatableMineral eatableMineral = plugin.getMineralManager().getEatableMineral(material);
            
            if (eatableMineral != null) {
                ItemStack item = plugin.getMineralManager().createConvertedItem(eatableMineral, 1);
                player.getInventory().addItem(item);
                player.sendMessage(plugin.getMessageUtil().getMessage("mineral-given", eatableMineral.getName()));
            } else {
                player.sendMessage(plugin.getMessageUtil().getMessage("mineral-not-found", mineralName));
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(plugin.getMessageUtil().getMessage("invalid-mineral", mineralName));
        }
    }
}
