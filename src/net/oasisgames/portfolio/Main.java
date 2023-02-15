package net.oasisgames.portfolio;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.oasisgames.portfolio.APIs.LuckpermsExample;

/*
 * Nick Doxa Portfolio Project
 * Main class connecting and initializing the plugin with Spigot, Bukkit, ETC.
 * Please refer to the README.txt for more information!
 */

public class Main extends JavaPlugin {

	//Class declarations
	private ExampleCommands cmds;
	
	@Override
	public void onEnable() {
		Bukkit.getLogger().info("Enabling Portfolio!");
		
		saveDefaultConfig();
		loadConfig();
		
		cmds = new ExampleCommands();
		this.getCommand("example").setExecutor(cmds);
		
		LuckpermsExample.initializeLuckperms();
	}
	
	@Override
	public void onDisable() {
		Bukkit.getLogger().info("Disabling Portfolio!");
	}
	
	//Static config access
	
	public static FileConfiguration getPluginConfig() {
		return config;
	}
	
	private static FileConfiguration config;
	
	private void loadConfig() {
		File file = new File(this.getDataFolder().getAbsolutePath() + "/config.yml");
		config = YamlConfiguration.loadConfiguration(file);
		try {
			config.save(file);
		} catch (IOException e) {
			Bukkit.getLogger().info("Could not save plugin config!");
		}
	}
	
	@Override
	public void reloadConfig() {
		loadConfig();
	}
	
	@Override
	public FileConfiguration getConfig() {
		return config;
	}
	
}
