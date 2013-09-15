package com.gmail.cfretz244;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spleefer extends JavaPlugin implements Listener {
	
	HashMap<String, Player> registeredPlayers;
	
	Location[] killRegion;
	Location[] floorRegion;
	Location[] fightingRoomRegion;
	Location[] spectatingRoomRegion;
	Location[] spawningRegion;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		registeredPlayers = new HashMap<String, Player>();
		killRegion = new Location[2];
		floorRegion = new Location[2];
		fightingRoomRegion = new Location[3];
		spectatingRoomRegion = new Location[3];
		spawningRegion = new Location[3];
		
		getLogger().info("Spleefer Loaded");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		Player sender = event.getPlayer();
		ItemStack currentItem = sender.getItemInHand();
		Material itemType = currentItem.getType();
		if(itemType == Material.BONE) {
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(registeredPlayers.get(sender.getName()) != null) {
					killRegion[0] = registeredPlayers.get(sender.getName()).getTargetBlock(null, 20).getLocation();
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set at " + killRegion[0].getBlockX() + ", " + killRegion[0].getBlockY() + ", " + killRegion[0].getBlockZ());
				}
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(registeredPlayers.get(sender.getName()) != null) {
					killRegion[1] = registeredPlayers.get(sender.getName()).getTargetBlock(null, 20).getLocation();
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set at " + killRegion[1].getBlockX() + ", " + killRegion[1].getBlockY() + ", " + killRegion[1].getBlockZ());
				}
			}
		}
	}
	
	public boolean containedIn(Location[] actionSpace, Location playerLoc) {
		if(actionSpace[0] == null) {
			return false;
		}
		Location point1 = actionSpace[0];
		Location point2 = actionSpace[1];
		if(point1.getY() > point2.getY()) {
			
		} else if(point1.getY() == point2.getY()) {
			if(((playerLoc.getX() < point1.getX() && playerLoc.getX() > point2.getX()) && ((playerLoc.getZ() < point1.getZ() && playerLoc.getZ() > point2.getZ())) || ((playerLoc.getX() > point1.getX() && playerLoc.getX() < point2.getX()) && (playerLoc.getZ() > point1.getX() && playerLoc.getZ() < point2.getX()))) || ((playerLoc.getX() < point2.getX() && playerLoc.getX() > point1.getX()) && (playerLoc.getZ() < point2.getZ() && playerLoc.getZ() > point1.getZ())) || ((playerLoc.getX() > point2.getX() && playerLoc.getX() < point1.getX()) && (playerLoc.getZ() > point2.getZ() && playerLoc.getZ() < point1.getZ()))) {
				return true;
			} else {
				return false;
			}
		} else {
			
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(containedIn(killRegion, player.getLocation())) {
			player.sendMessage("You're in the space");
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			String name = player.getName();
			registeredPlayers.put(name, player);
			if(cmd.getName().toLowerCase().equals("spleefsetup")) {
				Inventory playerInv = player.getInventory();
				boolean needsToBeReplaced = true;
				int chosenSlot = -1;
				for(int i = 0; i < 36; i++) {
					ItemStack stack = playerInv.getItem(i);
					if(stack == null) {
						if(chosenSlot == -1) {
							chosenSlot = i;
						}
					} else {
						Material stacktype = stack.getType();
						if(stacktype.getId() == Material.BONE.getId()) {
							needsToBeReplaced = false;
							chosenSlot = i;
						}
					}
				}
				if(chosenSlot != -1) {
					if(needsToBeReplaced) {
						ItemStack replacementStack = new ItemStack(Material.BONE);
						playerInv.setItem(chosenSlot, replacementStack);
					}
				}
				
			}
		} else {
			sender.sendMessage("Sender must be a player");
		}
		return false;
	}
	
}
