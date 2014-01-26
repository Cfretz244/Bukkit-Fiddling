package com.gmail.cfretz244;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public final class SpleefListener implements Listener {
	
	HashSet<String> registeredPlayers;
	Location[] killRegion, floorRegion, fightingRoomRegion, spectatingRoomRegion, spawningRegion;
	Spleefer plugin;
	Executor utility;
	boolean settingUp, definingFightingRoom, definingFloor, definingSpawn, definingKill, definingSpectation, finishedDefinition;
	
	public SpleefListener(Location[] kill, Location[] floor, Location[] fighting, Location[] spectating, Location[] spawning, Executor utility, Spleefer plugin) {
		registeredPlayers = new HashSet<String>();
		killRegion = kill;
		floorRegion = floor;
		fightingRoomRegion = fighting;
		spectatingRoomRegion = spectating;
		spawningRegion = spawning;
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		Player sender = event.getPlayer();
		if(registeredPlayers.contains(sender)) {
			ItemStack currentItem = sender.getItemInHand();
			Material itemType = currentItem.getType();
			if(itemType == Material.BONE) {
				if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if(settingUp) {
						if(definingFightingRoom) {
							fightingRoomRegion[0] = sender.getTargetBlock(null, 50).getLocation();
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set");
						} else if(definingFloor) {
							floorRegion[0] = sender.getTargetBlock(null, 50).getLocation();
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set");
						} else if(definingSpawn) {
							spawningRegion[0] = sender.getTargetBlock(null, 50).getLocation();
						} else if(definingKill) {
							killRegion[0] = sender.getTargetBlock(null, 50).getLocation();
						} else if(definingSpectation) {
							spectatingRoomRegion[0] = sender.getTargetBlock(null, 50).getLocation();
						}
					}
				} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(settingUp) {
						if(definingFightingRoom) {
							fightingRoomRegion[1] = sender.getTargetBlock(null, 50).getLocation();
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set");
						} else if(definingFloor) {
							floorRegion[1] = sender.getTargetBlock(null, 50).getLocation();
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set");
						} else if(definingSpawn) {
							spawningRegion[1] = sender.getTargetBlock(null, 50).getLocation();
						} else if(definingKill) {
							killRegion[1] = sender.getTargetBlock(null, 50).getLocation();
						} else if(definingSpectation) {
							spectatingRoomRegion[1] = sender.getTargetBlock(null, 50).getLocation();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(registeredPlayers.contains(player.getName())) {
			if(utility.containedIn(killRegion, player.getLocation())) {
				player.sendMessage("You're in the space");
			} else {
				player.sendMessage("You're not in the space");
			}
		}
	}
}
