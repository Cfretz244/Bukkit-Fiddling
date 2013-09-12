package com.gmail.cfretz244;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Redirection extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("redirect")) {
			getLogger().info("Command successfully detected!");
			return false;
		}
		return false;
	}
}
