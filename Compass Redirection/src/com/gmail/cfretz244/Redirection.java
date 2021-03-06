package com.gmail.cfretz244;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Redirection extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		//do something
	}
	
	@Override
	public void onDisable() {
		//do something
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("redirect")) {
			if(sender instanceof Player) {
				Player player = (Player)sender;
				if(args.length == 1) {
					if(args[0].toLowerCase().equals("here")) {
						Location playerLoc = player.getLocation();
						player.setCompassTarget(playerLoc);
						player.sendMessage(ChatColor.GOLD + "Location set!");
					} else if(args[0].equals("there")) {
						Location playerGaze = player.getTargetBlock(null, 1000).getLocation();
						player.setCompassTarget(playerGaze);
						player.sendMessage(ChatColor.GOLD + "Location set!");
					}
				} else if(args.length == 3) {
					if(args[0].toLowerCase().equals("there")) {
						try {
							double x = Double.parseDouble(args[1]);
							double z = Double.parseDouble(args[2]);
							World world = player.getWorld();
							Location targetLoc = new Location(world, x, 0, z);
							player.setCompassTarget(targetLoc);
							player.sendMessage(ChatColor.GOLD + "Location set!");
						} catch(Exception e) {
							player.sendMessage("Exception");
							player.sendMessage(ChatColor.GOLD + "Invalid Parameters");
						}
					} else {
						player.sendMessage(ChatColor.GOLD + "Invalid Parameters");
					}
				} else {
					player.sendMessage(ChatColor.GOLD + "Invalid number of parameters");
				}
			} else {
				sender.sendMessage("Sender must be a player");
			}
		}
		return false;
	}
}
