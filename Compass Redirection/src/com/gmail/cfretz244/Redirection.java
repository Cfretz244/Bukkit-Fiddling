package com.gmail.cfretz244;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
			if(sender instanceof Player) {
				Player player = (Player)sender;
				if(args.length > 0) {
					if(args[0].toLowerCase().equals("here")) {
						Location playerLoc = player.getLocation();
						player.setCompassTarget(playerLoc);
						player.sendMessage("Location set!");
					}
				}
			}
		}
		return false;
	}
}
