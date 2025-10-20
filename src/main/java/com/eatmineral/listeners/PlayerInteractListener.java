package com.eatmineral.listeners;

import com.eatmineral.EatMineral;
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

import java.util.*;

public class PlayerInteractListener implements Listener {

    private final EatMineral plugin;
    private final Map<UUID, Double> copperChanceMap = new HashMap<>();
    private final Map<UUID, List<Long>> sodiumChlorideTimestamps = new HashMap<>();
    private final Map<UUID, List<Long>> wetSodiumChlorideTimestamps = new HashMap<>();

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

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks < 32) {
                    if (ticks % 4 == 0) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EAT, 0.5f + (float) Math.random() * 0.2f, 0.8f + (float) Math.random() * 0.4f);
                    }
                    ticks++;
                } else {
                    cancel();
                    ItemStack currentItem = player.getInventory().getItemInMainHand();
                    if (!currentItem.equals(item)) return;

                    if (currentItem.getAmount() > 1) {
                        currentItem.setAmount(currentItem.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }

                    int newFoodLevel = Math.min(20, player.getFoodLevel() + foodLevel);
                    float newSaturation = Math.min(20.0f, player.getSaturation() + saturation);
                    player.setFoodLevel(newFoodLevel);
                    player.setSaturation(newSaturation);
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_BURP, 1.0f, 1.0f);

                    String displayName = meta.getDisplayName() != null ? meta.getDisplayName() : item.getType().name();
                    String message = plugin.getMessageUtil().getMessage("mineral.eaten",
                            displayName,
                            String.valueOf(foodLevel),
                            String.valueOf(saturation));
                    player.sendMessage(message);

                    Material material = item.getType();
                    switch (material) {
                        case NETHERITE_INGOT -> {
                            player.sendMessage("§8這東西黑到發光跟發霉一樣你敢吃喔");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2));
                        }
                        case DIAMOND -> {
                            player.sendMessage("§b吃鑽石阿，真不愧是有錢人！");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                        }
                        case GOLD_INGOT -> {
                            player.sendMessage("§6你感覺全身發金！");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1));
                        }
                        case REDSTONE -> {
                            player.sendMessage("§c你感覺到能量在體內流竄！");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1));
                        }
                        case COAL -> {
                            player.sendMessage("§8搞到自己眼睛上了……");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                        }
                        case IRON_INGOT -> {
                            player.sendMessage("§7多吃鐵，補充鐵質，沒問題的！");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0));
                        }
                        case COPPER_INGOT -> {
                            double baseChance = copperChanceMap.getOrDefault(player.getUniqueId(), 15.0);
                            double random = Math.random() * 100.0;
                            if (random <= baseChance) {
                                player.sendMessage("§c哈哈哈，就說了會中毒，不信喔！");
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
                                copperChanceMap.put(player.getUniqueId(), 15.0);
                            } else {
                                double newChance = Math.min(100.0, baseChance + 5.0);
                                copperChanceMap.put(player.getUniqueId(), newChance);
                                player.sendMessage("§c不是吧，你吃銅喔，你等等就金屬中毒");
                            }
                        }
                        case EMERALD -> {
                            player.sendMessage("§a綠綠的是不是長青苔？好吃嗎？");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 300, 1));
                        }
                        case AMETHYST_SHARD -> {
                            player.sendMessage("§d吃下去腸胃不會壞掉?");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0)); // 噁心
                            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 1));
                        }
                        case LAPIS_LAZULI -> {
                            player.sendMessage("§1嘴巴有沒有藍藍的?");
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 1));
                        }
                        case IRON_NUGGET -> {
                            UUID id = player.getUniqueId();
                            long now = System.currentTimeMillis();
                            List<Long> times = sodiumChlorideTimestamps.getOrDefault(id, new ArrayList<>());
                            times.removeIf(t -> now - t > 60000);
                            times.add(now);
                            sodiumChlorideTimestamps.put(id, times);

                            if (times.size() >= 10) {
                                player.sendMessage("§c吃太多了，對身體不好");
                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                                times.clear();
                            } else {
                                player.sendMessage("§f補充鹽分中...");
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 0));
                            }
                        }
                        case IRON_BLOCK -> {
                            UUID id = player.getUniqueId();
                            long now = System.currentTimeMillis();
                            List<Long> times = wetSodiumChlorideTimestamps.getOrDefault(id, new ArrayList<>());
                            times.removeIf(t -> now - t > 60000);
                            times.add(now);
                            wetSodiumChlorideTimestamps.put(id, times);

                            if (times.size() >= 5) {
                                player.sendMessage("§c吃太多了，對身體不好");
                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                                times.clear();
                            } else {
                                player.sendMessage("§7真硬！這一整塊鹽吃下去超撐的...");
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
