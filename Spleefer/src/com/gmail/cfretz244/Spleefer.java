package com.gmail.cfretz244;

import java.util.HashSet;

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
	
	HashSet<String> registeredPlayers;
	
	Location[] killRegion;
	Location[] floorRegion;
	Location[] fightingRoomRegion;
	Location[] spectatingRoomRegion;
	Location[] spawningRegion;
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		registeredPlayers = new HashSet<String>();
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
				if(registeredPlayers.contains(sender.getName())) {
					killRegion[0] = sender.getTargetBlock(null, 20).getLocation();
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set at " + killRegion[0].getBlockX() + ", " + killRegion[0].getBlockY() + ", " + killRegion[0].getBlockZ());
				}
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(registeredPlayers.contains(sender.getName())) {
					killRegion[1] = sender.getTargetBlock(null, 20).getLocation();
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set at " + killRegion[1].getBlockX() + ", " + killRegion[1].getBlockY() + ", " + killRegion[1].getBlockZ());
				}
			}
		}
	}
	
	public boolean containedIn(Location[] actionSpace, Location playerLoc) {
		if(actionSpace[0] == null || actionSpace[1] == null || playerLoc == null) {
			return false;
		}
		Location point1 = actionSpace[0];
		Location point2 = actionSpace[1];
		double X1 = point1.getX();
		double X2 = point2.getX();
		double Z1 = point1.getZ();
		double Z2 = point2.getZ();
		double Y1 = point1.getY();
		double Y2 = point2.getY();
		double pX = Math.floor(playerLoc.getX());
		double pY = Math.floor(playerLoc.getY()) - 1;
		double pZ = Math.floor(playerLoc.getZ());
		if(Y1 > Y2 && (pY < Y2 || pY > Y1)) {
			return false;
		} else if(Y1 == Y2 && pY != Y1) {
			return false;
		} else if(Y2 > Y1 && (pY < Y1 || pY > Y2)) {
			return false;
		}
		if(X1 < X2 && Z1 > Z2) {
			//case 1
			if(pX >= X1 && pX <= X2 && pZ >= Z2 && pZ <= Z1) {
				return true;
			} else {
				return false;
			}
		} else if(X2 < X1 && Z1 > Z2) {
			//case 2
			if(pX >= X2 && pX <= X1 && pZ >= X2 && pZ <= Z1) {
				return true;
			} else {
				return false;
			}
		} else if(X1 < X2 && Z2 > Z1) {
			//case 3
			if(pX >= X1 && pX <= X2 && pZ >= Z1 && pZ <= Z2) {
				return true;
			} else {
				return false;
			}
		} else if(X2 < X1 && Z2 > Z1) {
			//case 4
			if(pX >= X2 && pX <= X1 && pZ >= Z1 && pZ <= Z2) {
				return true;
			} else {
				return false;
			}
		} else if(X1 < X2 && Z1 == Z2) {
			//case 5
			pZ = Math.floor(pZ);
			if(pX >= X1 && pX <= X2 && pZ == Z1) {
				return true;
			} else {
				return false;
			}
		} else if(X2 < X1 && Z1 == Z2) {
			//case 6
			pZ = Math.floor(pZ);
			if(pX >= X2 && pX <= X1 && pZ == Z1) {
				return true;
			} else {
				return false;
			}
		} else if(X1 == X2 && Z2 > Z1) {
			//case 7
			pX = Math.floor(pX);
			if(pZ >= Z1 && pZ <= Z2 && pX == X1) {
				return true;
			} else {
				return false;
			}
		} else if(X1 == X2 && Z1 > Z2) {
			//case 8
			pX = Math.floor(pX);
			if(pZ >= Z2 && pZ >= Z1 && pX == X1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(registeredPlayers.contains(player.getName())) {
			if(containedIn(killRegion, player.getLocation())) {
				player.sendMessage("You're in the space");
			} else {
				player.sendMessage("You're not in the space");
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			String name = player.getName();
			registeredPlayers.add(name);
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
