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
	
	HashSet<String> registeredPlayers, registeredSpectators, listenTo;
	HashMap<String, Location> previousPlayerLocations, previousSpectatorLocations;
	Location[][] regions;
	Spleefer plugin;
	static final int KILL = 0, FLOOR = 1, FIGHTING = 2, SPECTATING = 3, SPAWNING = 4;
	
	public Executor(Location[][] regions, HashSet<String>registeredPlayers, HashSet<String> registeredSpectators, HashSet<String> listenTo, Spleefer plugin) {
		this.regions = regions;
		this.registeredPlayers = registeredPlayers;
		this.registeredSpectators = registeredSpectators;
		this.listenTo = listenTo;
		this.plugin = plugin;
	}
	
	public void broadcastToRegisteredPlayers(Server server, String message) {
		Iterator<String> players = listenTo.iterator();
		while(players.hasNext()) {
			Player player = server.getPlayer(players.next());
			player.sendMessage(message);
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
		for(int i = 0; i < 2; i++) {
			if(regions[FIGHTING][i] == null || regions[SPECTATING][i] == null || regions[SPAWNING][i] == null || regions[KILL][i] == null || regions[FLOOR][i] == null) {
				mayBegin = false;
			}
		}
		return mayBegin;
	}
	
	public boolean validatePlayerNames(Player sender, String[] args, Server server) {
		for(int i = 1; i < args.length; i++) {
			if(server.getPlayer(args[i]) == null) {
				sender.sendMessage("Sorry, player " + args[i] + " does not appear to exist. Perhaps they're not online?");
				return false;
			}
		}
		return true;
	}
	
	public void movePlayersToSpawn(Server server) {
		double y = Math.min(regions[SPAWNING][0].getY(), regions[SPAWNING][1].getY()) + 1;
		double x = (regions[SPAWNING][0].getX() + regions[SPAWNING][1].getX()) / 2;
		double z = (regions[SPAWNING][0].getZ() + regions[SPAWNING][1].getZ()) / 2;
		Location newLocale = new Location(regions[SPAWNING][0].getWorld(), x, y, z);
		Iterator<String> players = registeredPlayers.iterator();
		previousPlayerLocations = new HashMap<String, Location>();
		while(players.hasNext()) {
			String name = players.next();
			Player teleporter = server.getPlayer(name);
			previousPlayerLocations.put(name, teleporter.getLocation());
			teleporter.teleport(newLocale);
		}
	}
	
	public void moveSpectator(Server server, Player spectator) {
		double y = Math.min(regions[SPECTATING][0].getY(), regions[SPECTATING][1].getY());
		double x = (regions[SPECTATING][0].getX() + regions[SPECTATING][1].getX()) / 2;
		double z = (regions[SPECTATING][0].getZ() + regions[SPECTATING][1].getZ()) / 2;
		Location newLocale = new Location(regions[SPECTATING][0].getWorld(), x, y, z);
		String name = spectator.getName();
		previousSpectatorLocations.put(name, spectator.getLocation());
		spectator.teleport(newLocale);
	}
	
	public void removePlayer(Server server, String name) {
		if(registeredPlayers.contains(name)) {
			registeredPlayers.remove(name);
			Player player = server.getPlayer(name);
			player.teleport(previousPlayerLocations.get(name));
			previousPlayerLocations.remove(name);
		} else {
			registeredSpectators.remove(name);
			Player spectator = server.getPlayer(name);
			spectator.teleport(previousSpectatorLocations.get(name));
			previousSpectatorLocations.remove(name);
		}
		if(listenTo.contains(name)) {
			listenTo.remove(name);
		}
	}

}