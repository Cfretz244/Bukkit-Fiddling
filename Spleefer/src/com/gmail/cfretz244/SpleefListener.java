package com.gmail.cfretz244;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
	
	HashSet<String> registeredPlayers, registeredSpectators, listenTo;
	HashMap<String, Boolean> startInfo;
	Location[] killRegion, floorRegion, fightingRoomRegion, spectatingRoomRegion, spawningRegion;
	Spleefer plugin;
	Executor utility;
	boolean settingUp, definingFightingRoom, definingFloor, definingSpawn, definingKill, definingSpectation, finishedDefinition, shouldListenForMovement, shouldListenForBlockInteraction;
	boolean waitingToCommence;
	
	public SpleefListener(Location[] kill, Location[] floor, Location[] fighting, Location[] spectating, Location[] spawning, HashSet<String> registeredPlayers, HashSet<String> registeredSpectators, HashSet<String> listenTo, Executor utility, Spleefer plugin) {
		registeredPlayers = new HashSet<String>();
		startInfo = new HashMap<String, Boolean>();
		killRegion = kill;
		floorRegion = floor;
		fightingRoomRegion = fighting;
		spectatingRoomRegion = spectating;
		spawningRegion = spawning;
		this.registeredPlayers = registeredPlayers;
		this.registeredSpectators = registeredSpectators;
		this.listenTo = listenTo;
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		if(shouldListenForBlockInteraction) {
			Player sender = event.getPlayer();
			if(listenTo.contains(sender.getName())) {
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
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set");
							} else if(definingKill) {
								killRegion[0] = sender.getTargetBlock(null, 50).getLocation();
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set");
							} else if(definingSpectation) {
								spectatingRoomRegion[0] = sender.getTargetBlock(null, 50).getLocation();
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "First Position Set");
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
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set");
							} else if(definingKill) {
								killRegion[1] = sender.getTargetBlock(null, 50).getLocation();
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set");
							} else if(definingSpectation) {
								spectatingRoomRegion[1] = sender.getTargetBlock(null, 50).getLocation();
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Second Position Set");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(shouldListenForMovement) {
			Player player = event.getPlayer();
			if(registeredPlayers.contains(player.getName())) {
				if(waitingToCommence) {
					boolean shouldCommence = true;
					Player mover = event.getPlayer();
					startInfo.put(mover.getName(), new Boolean(utility.containedIn(spawningRegion, event.getTo())));
					Iterator<String> names = startInfo.keySet().iterator();
					while(names.hasNext()) {
						if(!startInfo.get(names.next()).booleanValue()) {
							shouldCommence = false;
						}
					}
					if(shouldCommence) {
						plugin.runRound();
					}
				}
			}
		}
	}
}
