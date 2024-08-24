package me.lukyn76.emailer.spigot.common.utils.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lukyn76.emailer.spigot.common.utils.communication.Title;

public class ActionUtil {
	
	public static boolean execute(OfflinePlayer player, List<String> actions) {
		boolean containsCancel = false;
		
		if (player == null || player.isBanned()) {
			return false;
		}
		
		List<String> actionsCopy = new ArrayList<>(Arrays.asList(actions.toArray(new String[0]).clone()));
		
		for (int i = 0; i < actions.size(); i++) {
			String action = ChatColor.translateAlternateColorCodes('&', actions.get(i).replace("%player%", player.getName())
					.replace("%player_name%", player.getName())
					.replace("%player_uuid%", player.getUniqueId().toString()));
			
			if (player.isOnline()) {
				action.replace("%player_ip%", player.getPlayer().getAddress().getAddress().getHostAddress());
			}
			
			if (CommonUtils.isPluginEnabled("PlaceholderAPI")) {
				action = PlaceholderAPI.setPlaceholders(player, action);
			}
			
			if (action.equals("CANCEL")) {
				containsCancel = true;
				continue;
			}
			
			String[] actionSplited = action.split(";;");
			String type = null, arguments = null;
			
			try {
				type = actionSplited[0];
				arguments = actionSplited[1];
			} catch (ArrayIndexOutOfBoundsException ex) {
				CommonUtils.log("Could not execute action because the syntax is wrong.");
				return false;
			}
			
	        Messenger messenger = null;
	        ByteArrayOutputStream byteArray = null;
	        DataOutputStream out = null;

	        if (type.equals("DELAY")) {
	    		actionsCopy.remove(i);
	        }
	        
			switch (type) {
			case "DELAY":
				Bukkit.getScheduler().runTaskLater(CommonUtils.getPlugin(), () -> execute(player, actionsCopy), Integer.parseInt(arguments));
				return containsCancel;
			case "MESSAGE":
				if (player.isOnline()) {
					player.getPlayer().sendMessage(arguments);
				}
				break;
			case "TITLE":
				if (player.isOnline()) {
					try {
						Title.send((Player) player, Integer.valueOf(actionSplited[3]), actionSplited[1], actionSplited[2]);	
					} catch (ArrayIndexOutOfBoundsException e) {
						CommonUtils.log("Could not send TITLE to player. Make sure the syntax of the action is correct.");
					}
				}
				break;
			case "BROADCAST":
				Bukkit.broadcastMessage(arguments);
				break;
			case "KICK":
				if (!player.isOnline()) {
					break;
				}

				try {
					out.writeUTF("KickPlayer");
					out.writeUTF(player.getName());
					out.writeUTF(arguments);

					player.getPlayer().sendPluginMessage(CommonUtils.getPlugin(), "BungeeCord", byteArray.toByteArray());
				} catch (IOException e) {
					CommonUtils.log("Could not kick player from bungeecord: " + e.getLocalizedMessage());
				}
				
				break;
			case "BAN":
				Bukkit.getBanList(Type.NAME).addBan(player.getName(), arguments, null, null);
				execute(player, "KICK");
				break;
			case "TEMPBAN":		
			    Calendar date = Calendar.getInstance();
			    date.add(Calendar.SECOND, Integer.parseInt(actionSplited[2]));
			    
				Bukkit.getBanList(Type.NAME).addBan(player.getName(), arguments, date.getTime(), null);
				execute(player, "KICK");
				break;
			case "BAN_IP":
				if (player.isOnline()) {
					Bukkit.banIP(player.getPlayer().getAddress().getAddress().getHostAddress());
					execute(player, "KICK");
				}
				break;
			case "CONSOLE_COMMAND":
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), arguments);
				break;
			case "PLAYER_COMMAND":
				if (player.isOnline()) {
					player.getPlayer().performCommand(arguments);	
				}
				break;
			case "CLEAR_CHAT":
				if (player.isOnline()) {
					player.getPlayer().sendMessage(StringUtils.repeat(" \n", 100));
				}
				break;
			}
			
		}
		
		return containsCancel;
	}

	public static boolean execute(OfflinePlayer player, String action) {
		return execute(player, Collections.singletonList(action));
	}
	
}
