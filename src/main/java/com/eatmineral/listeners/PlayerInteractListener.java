package com.eatmineral.listeners;

import com.eatmineral.EatMineral;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInteractListener implements Listener {

    private final EatMineral plugin;
    // 銅礦中毒機率映射表
    private final Map<UUID, Double> copperChanceMap = new ConcurrentHashMap<>();
    // 氯化鈉食用記錄（鐵粒）- 記錄每次食用的時間戳
    private final Map<UUID, Deque<Long>> sodiumChlorideTimestamps = new ConcurrentHashMap<>();
    // 潮結的氯化鈉食用記錄（鐵磚）
    private final Map<UUID, Deque<Long>> wetSodiumChlorideTimestamps = new ConcurrentHashMap<>();

    public PlayerInteractListener(EatMineral plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (!player.hasPermission("eatmineral.use")) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.getPersistentDataContainer().has(plugin.getMineralManager().getEatableKey(), PersistentDataType.BYTE)) return;

        if (player.getFoodLevel() >= 20) {
            player.sendMessage(plugin.getMessageUtil().getMessage("not-hungry"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        Integer foodLevel = meta.getPersistentDataContainer().get(plugin.getMineralManager().getFoodLevelKey(), PersistentDataType.INTEGER);
        Float saturation = meta.getPersistentDataContainer().get(plugin.getMineralManager().getSaturationKey(), PersistentDataType.FLOAT);
        if (foodLevel == null || saturation == null) return;

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);

        // 延遲執行食用效果（模擬食用時間）
        AtomicInteger ticks = new AtomicInteger(0);
        ItemStack initialItem = item.clone();

        player.getScheduler().runAtFixedRate(plugin, (ScheduledTask scheduledTask) -> {
            int currentTick = ticks.getAndIncrement();
            if (currentTick < 32) {
                if (currentTick % 4 == 0) {
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EAT,
                        0.5f + (float) Math.random() * 0.2f,
                        0.8f + (float) Math.random() * 0.4f);
                }
                return;
            }

            scheduledTask.cancel();

            ItemStack currentItem = player.getInventory().getItemInMainHand();
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                return;
            }

            if (!currentItem.isSimilar(initialItem)) {
                return;
            }

            int amount = currentItem.getAmount();
            if (amount > 1) {
                currentItem.setAmount(amount - 1);
                player.getInventory().setItemInMainHand(currentItem);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            int currentFoodLevel = player.getFoodLevel();
            float currentSaturation = player.getSaturation();

            int newFoodLevel = Math.min(20, currentFoodLevel + foodLevel);
            float newSaturation = Math.min(20.0f, currentSaturation + saturation);

            player.setFoodLevel(newFoodLevel);
            player.setSaturation(newSaturation);

            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);

            Component displayNameComponent = meta.hasDisplayName() ? meta.displayName() : null;
            String displayName = displayNameComponent != null
                ? LegacyComponentSerializer.legacySection().serialize(displayNameComponent)
                : item.getType().name();
            String message = plugin.getMessageUtil().getMessage("mineral.eaten",
                displayName,
                String.valueOf(foodLevel),
                String.valueOf(saturation));
            player.sendMessage(message);

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
                    UUID copperId = player.getUniqueId();
                    double baseChance = copperChanceMap.getOrDefault(copperId, 15.0);
                    double random = Math.random() * 100.0;
                    if (random <= baseChance) {
                        player.sendMessage("§c哈哈哈，就說了會中毒，不信喔！");
                        player.getWorld().strikeLightningEffect(player.getLocation());
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.POISON, 200, 2)); // 10秒 中毒III
                        copperChanceMap.put(copperId, 15.0);
                    } else {
                        double newChance = Math.min(100.0, baseChance + 5.0);
                        copperChanceMap.put(copperId, newChance);
                        player.sendMessage("§c不是吧，你吃銅喔，你等等就金屬中毒");
                    }
                    break;

                case EMERALD:
                    player.sendMessage("§a綠綠的是不是長青苔？好吃嗎？");
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.LUCK, 300, 1)); // 15秒 幸運II
                    break;

                case LAPIS_LAZULI:
                    player.sendMessage("§1嘴巴有沒有藍藍的?");
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.NIGHT_VISION, 200, 1)); // 10秒 夜視II
                    break;

                case AMETHYST_SHARD:
                    player.sendMessage("§d吃下去腸胃不會壞掉?");
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.CONFUSION, 100, 0)); // 5秒 噁心
                    player.addPotionEffect(new PotionEffect(
                        PotionEffectType.FIRE_RESISTANCE, 200, 1)); // 10秒 抗火II
                    break;

                case IRON_NUGGET:
                    UUID playerId = player.getUniqueId();
                    long currentTime = System.currentTimeMillis();

                    Deque<Long> timestamps = sodiumChlorideTimestamps.computeIfAbsent(playerId, key -> new ConcurrentLinkedDeque<>());
                    timestamps.removeIf(time -> currentTime - time > 60000);
                    timestamps.addLast(currentTime);

                    if (timestamps.size() >= 10) {
                        player.sendMessage("§c吃太多了，對身體不好");
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.POISON, 100, 1)); // 5秒 中毒II
                        timestamps.clear();
                    } else {
                        player.sendMessage("§f補充鹽分中...");
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.ABSORPTION, 120, 0)); // 6秒 吸收I
                    }
                    break;

                case IRON_BLOCK:
                    UUID playerIdBlock = player.getUniqueId();
                    long currentTimeBlock = System.currentTimeMillis();

                    Deque<Long> timestampsBlock = wetSodiumChlorideTimestamps.computeIfAbsent(playerIdBlock, key -> new ConcurrentLinkedDeque<>());
                    timestampsBlock.removeIf(time -> currentTimeBlock - time > 60000);
                    timestampsBlock.addLast(currentTimeBlock);

                    if (timestampsBlock.size() >= 5) {
                        player.sendMessage("§c吃太多了，對身體不好");
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.POISON, 100, 1)); // 5秒 中毒II
                        timestampsBlock.clear();
                    } else {
                        player.sendMessage("§7真硬！這一整塊鹽吃下去超撐的...");
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.ABSORPTION, 200, 1)); // 10秒 吸收II
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SLOW, 100, 0)); // 5秒 緩速I（吃太撐）
                    }
                    break;
                default:
                    break;
            }
        }, () -> {}, 1L, 1L);
    }
}
