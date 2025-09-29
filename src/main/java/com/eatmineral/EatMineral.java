package com.eatmineral;

import com.eatmineral.commands.EatMineralCommand;
import com.eatmineral.commands.ConvertCommand;
import com.eatmineral.listeners.PlayerInteractListener;
import com.eatmineral.managers.MineralManager;
import com.eatmineral.utils.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class EatMineral extends JavaPlugin {
    
    private static EatMineral instance;
    private MineralManager mineralManager;
    private MessageUtil messageUtil;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默認配置
        saveDefaultConfig();
        
        // 初始化管理器
        this.mineralManager = new MineralManager(this);
        this.messageUtil = new MessageUtil(this);
        
        // 註冊命令
        getCommand("eatmineral").setExecutor(new EatMineralCommand(this));
        getCommand("ceat").setExecutor(new ConvertCommand(this));
        
        // 註冊事件監聽器
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        
        // 初始化礦物系統
        mineralManager.initializeMinerals();
        
        getLogger().info("EatMineral 插件已成功啟用！");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("EatMineral 插件已停用！");
    }
    
    public static EatMineral getInstance() {
        return instance;
    }
    
    public MineralManager getMineralManager() {
        return mineralManager;
    }
    
    public MessageUtil getMessageUtil() {
        return messageUtil;
    }
}
