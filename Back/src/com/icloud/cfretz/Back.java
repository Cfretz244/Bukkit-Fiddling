package com.icloud.cfretz;

import java.util.HashMap;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;

public final class Back extends JavaPlugin implements Listener {
	
	HashMap<String, Stack<Location>> histories, futures;
	HashMap<String, Boolean> dirt;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		histories = new HashMap<String, Stack<Location>>();
		futures = new HashMap<String, Stack<Location>>();
		dirt = new HashMap<String, Boolean>();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			String name = player.getName();
			if(cmd.getName().equals("back")) {
				if(histories.containsKey(name)) {
					Stack<Location> playerHistory = histories.get(name);
					if(!playerHistory.isEmpty()) {
						dirt.put(name, new Boolean(true));
						if(futures.containsKey(name)) {
							Stack<Location> playerFuture = futures.get(name);
							playerFuture.push(player.getLocation());
						} else {
							Stack<Location> playerFuture = new Stack<Location>();
							playerFuture.push(player.getLocation());
							futures.put(name, playerFuture);
						}
						player.teleport(playerHistory.pop());
						player.sendMessage(ChatColor.GREEN + "[Back] Moved backward one jump");
					} else {
						player.sendMessage(ChatColor.RED + "[Back] Sorry, you appear to be out of previous locations. Try teleporting somewhere.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "[Back] Sorry, you have no locations logged.");
				}
			} else {
				if(futures.containsKey(name)) {
					Stack<Location> playerFuture = futures.get(name);
					if(!playerFuture.isEmpty()) {
						dirt.put(name,  new Boolean(true));
						Stack<Location> playerHistory = histories.get(name);
						playerHistory.push(player.getLocation());
						player.teleport(playerFuture.pop());
						player.sendMessage(ChatColor.GREEN + "[Back] Moved forward one jump");
					} else {
						player.sendMessage(ChatColor.RED + "[Back] Sorry, you appear to be out of future locations. Try /back.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "[Back] Sorry, you have no locations logged");
				}
			}
		} else {
			sender.sendMessage("[Back] This command is to be used by players only.");
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player teleporter = event.getPlayer();
		String name = teleporter.getName();
		if(event.getCause() == TeleportCause.UNKNOWN) {
			return;
		}
		if(!dirt.containsKey(name) || !dirt.get(name).booleanValue()) {
			if(histories.containsKey(name)) {
				histories.get(name).push(event.getFrom());
			} else {
				Stack<Location> history = new Stack<Location>();
				history.push(event.getFrom());
				histories.put(name, history);
			}
			if(futures.containsKey(name) && !futures.get(name).isEmpty()) {
				futures.get(name).clear();
			}
		} else {
			if(dirt.containsKey(teleporter.getName())) {
				dirt.put(teleporter.getName(), new Boolean(false));
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player departed = event.getPlayer();
		if(histories.containsKey(departed.getName())) {
			histories.remove(departed.getName());
		}
		if(futures.containsKey(departed.getName())) {
			futures.remove(departed.getName());
		}
	}
	
}