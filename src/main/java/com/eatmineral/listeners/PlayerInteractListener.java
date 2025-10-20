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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerInteractListener implements Listener {
    
    private final EatMineral plugin;
    // 銅礦中毒機率映射表
    private final Map<UUID, Double> copperChanceMap = new HashMap<>();
    // 氯化鈉食用記錄（鐵粒）- 記錄每次食用的時間戳
    private final Map<UUID, List<Long>> sodiumChlorideTimestamps = new HashMap<>();
    // 潮結的氯化鈉食用記錄（鐵磚）
    private final Map<UUID, List<Long>> wetSodiumChlorideTimestamps = new HashMap<>();
    
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
                        
                        // 偵測吃下的礦物種類，給予特殊效果
                        Material material = item.getType();
                        switch (material) {
                            case NETHERITE_INGOT:
                                player.sendMessage("§j這東西黑到發光跟發霉一樣你敢吃喔");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.DAMAGE_RESISTANCE, 200, 2)); // 10秒 抗性III
                                break;
                                
                            case DIAMOND:
                                player.sendMessage("§b吃鑽石阿，真不愧是有錢人！");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.REGENERATION, 200, 1)); // 10秒 回復II
                                break;
                                
                            case GOLD_INGOT:
                                player.sendMessage("§6你感覺全身發金！");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.ABSORPTION, 100, 1)); // 5秒 吸收II
                                break;
                                
                            case REDSTONE:
                                player.sendMessage("§c你感覺到能量在體內流竄！");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.SPEED, 160, 1)); // 8秒 速度II
                                break;
                                
                            case COAL:
                                player.sendMessage("§8搞到自己眼睛上了……");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.BLINDNESS, 100, 0)); // 5秒 失明I
                                break;
                                
                            case IRON_INGOT:
                                player.sendMessage("§7多吃鐵，補充鐵質，沒問題的！");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.INCREASE_DAMAGE, 200, 0)); // 10秒 力量I
                                break;
                                
                            case COPPER_INGOT:
                                double baseChance = copperChanceMap.getOrDefault(player.getUniqueId(), 15.0);
                                double random = Math.random() * 100.0;
                                if (random <= baseChance) {
                                    player.sendMessage("§c哈哈哈，就說了會中毒，不信喔！");
                                    player.getWorld().strikeLightningEffect(player.getLocation());
                                    player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.POISON, 200, 2)); // 10秒 中毒III
                                    // 重置機率
                                    copperChanceMap.put(player.getUniqueId(), 15.0);
                                } else {
                                    // 沒觸發 → 機率提高
                                    double newChance = Math.min(100.0, baseChance + 5.0);
                                    copperChanceMap.put(player.getUniqueId(), newChance);
                                    player.sendMessage("§c不是吧，你吃銅喔，你等等就金屬中毒");
                                }
                                break;
                                
                            case EMERALD:
                                player.sendMessage("§a綠綠的是不是長青苔？好吃嗎？");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.LUCK, 300, 1)); // 15秒 幸運II
                                break;

                            case AMETHYST_SHARD:
                                player.sendMessage("§d吃下去腸胃不會壞掉?");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.NAUSEA, 100, 0)); // 5秒 噁心I
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.FIRE_RESISTANCE, 200, 1); // 10秒 抗火II
                                break;

                            case LAPIS_LAZULI;
                                player.sendMessage("§1嘴巴有沒有藍藍的?");
                                player.addPotionEffect(new PotionEffect(
                                    PotionEffectType.NIGHT_VISION, 200, 1); // 10秒 夜視II
                                
                            case IRON_NUGGET:
                                // 氯化鈉 - 追蹤1分鐘內的食用次數
                                UUID playerId = player.getUniqueId();
                                long currentTime = System.currentTimeMillis();
                                
                                // 獲取或創建時間戳列表
                                List<Long> timestamps = sodiumChlorideTimestamps.getOrDefault(playerId, new ArrayList<>());
                                
                                // 移除超過1分鐘的記錄
                                timestamps.removeIf(time -> currentTime - time > 60000); // 60秒
                                
                                // 添加當前時間
                                timestamps.add(currentTime);
                                sodiumChlorideTimestamps.put(playerId, timestamps);
                                
                                // 檢查是否吃太多
                                if (timestamps.size() >= 10) {
                                    player.sendMessage("§c吃太多了，對身體不好");
                                    player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.POISON, 100, 1)); // 5秒 中毒II
                                    // 清空記錄
                                    timestamps.clear();
                                    sodiumChlorideTimestamps.put(playerId, timestamps);
                                } else {
                                    player.sendMessage("§f補充鹽分中...");
                                    player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.ABSORPTION, 120, 0)); // 6秒 吸收I
                                }
                                break;
                                
                            case IRON_BLOCK:
                                // 潮結的氯化鈉 - 追蹤1分鐘內的食用次數
                                UUID playerIdBlock = player.getUniqueId();
                                long currentTimeBlock = System.currentTimeMillis();
                                
                                // 獲取或創建時間戳列表
                                List<Long> timestampsBlock = wetSodiumChlorideTimestamps.getOrDefault(playerIdBlock, new ArrayList<>());
                                
                                // 移除超過1分鐘的記錄
                                timestampsBlock.removeIf(time -> currentTimeBlock - time > 60000); // 60秒
                                
                                // 添加當前時間
                                timestampsBlock.add(currentTimeBlock);
                                wetSodiumChlorideTimestamps.put(playerIdBlock, timestampsBlock);
                                
                                // 檢查是否吃太多
                                if (timestampsBlock.size() >= 5) {
                                    player.sendMessage("§c吃太多了，對身體不好");
                                    player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.POISON, 100, 1)); // 5秒 中毒II
                                    // 清空記錄
                                    timestampsBlock.clear();
                                    wetSodiumChlorideTimestamps.put(playerIdBlock, timestampsBlock);
                                } else {
                                    player.sendMessage("§7真硬！這一整塊鹽吃下去超撐的...");
                                    player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.ABSORPTION, 200, 1)); // 10秒 吸收II
                                    player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.SLOW, 100, 0)); // 5秒 緩速I（吃太撐）
                                }
                                break;
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
