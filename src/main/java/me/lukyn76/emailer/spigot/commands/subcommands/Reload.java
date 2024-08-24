package me.lukyn76.emailer.spigot.commands.subcommands;

import me.lukyn76.emailer.spigot.utils.PlayerDataCache;
import org.bukkit.command.CommandSender;

import me.lukyn76.emailer.spigot.Emailer;
import me.lukyn76.emailer.spigot.common.commands.SubCommand;
import me.lukyn76.emailer.spigot.utils.Permissions;

public class Reload extends SubCommand {

	private final Emailer plugin = Emailer.getPlugin(Emailer.class);
	
	public Reload() {
		super("reload:r", Permissions.RELOAD);
	}

	@Override
	public void onCommand(CommandSender player, String[] args) {
		// We reload the files & data
		PlayerDataCache.save(false);
		plugin.reloadFiles();
		plugin.initiate();

		//Send a successful message to the player
		player.sendMessage(plugin.getMessage("commands.reload.success"));
	}

}