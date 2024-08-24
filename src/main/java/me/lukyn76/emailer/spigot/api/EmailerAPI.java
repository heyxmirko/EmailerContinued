package me.lukyn76.emailer.spigot.api;

import java.util.Map;

import javax.mail.Session;

import org.bukkit.OfflinePlayer;

import me.lukyn76.emailer.spigot.Emailer;

public class EmailerAPI {

	private final Emailer plugin = Emailer.getPlugin(Emailer.class);
	
	public Map<OfflinePlayer, String> getPending() {
		return plugin.getPending();
	}
	
	public Session getSession() {
		
		return plugin.getSession();
	}
}
