package com.gmail.cfretz244;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Executor {
	
	HashSet<String> registeredPlayers;
	HashMap<String, Location> previousLocations;
	Location[] killRegion, floorRegion, fightingRoomRegion, spectatingRoomRegion, spawningRegion;
	Spleefer plugin;
	
	public Executor(Location[] kill, Location[] floor, Location[] fighting, Location[] spectating, Location[] spawning, Spleefer plugin) {
		killRegion = kill;
		floorRegion = floor;
		fightingRoomRegion = fighting;
		spectatingRoomRegion = spectating;
		spawningRegion = spawning;
		this.plugin = plugin;
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
			if(pX >= X1 && pX <= X2 && pZ == Z1) {
				return true;
			} else {
				return false;
			}
		} else if(X2 < X1 && Z1 == Z2) {
			//case 6
			if(pX >= X2 && pX <= X1 && pZ == Z1) {
				return true;
			} else {
				return false;
			}
		} else if(X1 == X2 && Z2 > Z1) {
			//case 7
			if(pZ >= Z1 && pZ <= Z2 && pX == X1) {
				return true;
			} else {
				return false;
			}
		} else if(X1 == X2 && Z1 > Z2) {
			//case 8
			if(pZ >= Z2 && pZ >= Z1 && pX == X1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public void giveSpleefWand(Player asker) {
		Inventory playerInv = asker.getInventory();
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
				if(stacktype == Material.BONE) {
					needsToBeReplaced = false;
					chosenSlot = i;
				}
			}
		}
		if(chosenSlot != -1 && needsToBeReplaced) {
			ItemStack replacementStack = new ItemStack(Material.BONE);
			playerInv.setItem(chosenSlot, replacementStack);
		}
	}
	
	public boolean validateSpleefState() {
		boolean mayBegin = true;
		for(int i = 0; i < 3; i++) {
			if(i < 3) {
				if(killRegion[i] == null || floorRegion[i] == null) {
					mayBegin = false;
				}
			}
			if(fightingRoomRegion[i] == null || spectatingRoomRegion == null || spawningRegion[i] == null) {
				mayBegin = false;
			}
		}
		return mayBegin;
	}
	
	public boolean validatePlayerNames(Player sender, String[] args, Server server) {
		for(int i = 0; i < args.length; i++) {
			if(server.getPlayer(args[i]) == null) {
				sender.sendMessage("Sorry, player " + args[i] + " does not appear to exist. Perhaps they're not online?");
				return false;
			}
		}
		return true;
	}
	
	public void movePlayersToSpawn(Server server) {
		Iterator<String> players = registeredPlayers.iterator();
		previousLocations = new HashMap<String, Location>();
		while(players.hasNext()) {
			String name = players.next();
			Player teleporter = server.getPlayer(name);
			previousLocations.put(name, teleporter.getLocation());
		}
	}

}