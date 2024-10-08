package me.lukyn76.emailer.spigot.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.lukyn76.emailer.global.EmailAddress;
import me.lukyn76.emailer.spigot.Emailer;
import me.lukyn76.emailer.spigot.api.events.PlayerVerifyEvent;
import me.lukyn76.emailer.spigot.common.commands.SpigotCommand;
import me.lukyn76.emailer.spigot.common.commands.SubCommand;
import me.lukyn76.emailer.spigot.common.utils.util.ActionUtil;
import me.lukyn76.emailer.spigot.common.utils.util.CommonUtils;
import me.lukyn76.emailer.spigot.utils.Checker;
import me.lukyn76.emailer.spigot.utils.Permissions;

public class VerifyCommand extends SpigotCommand {

	private final Emailer plugin = Emailer.getPlugin(Emailer.class);
	
	public VerifyCommand() {
		super(Permissions.VERIFY, 1, (SubCommand) null);
		super.setUsage("/verifyemail <code>");
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		// If the sender is not a player we return
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.getMessage("errors.onlyPlayers"));
			return;
		}
		
		Player player = (Player) sender;
    	
		// The code given by the player
		String code = args[0];

		// Check if the player has a pending verification requst
		if (!plugin.getPending().containsKey(player)) {
        	player.sendMessage(plugin.getMessage("commands.verify.noEmail"));
        	return;
    	}
	
		// The data of the pending request
    	String[] data = plugin.getPending().get(player).split(";;");
		String realCode = data[0];
		EmailAddress email = new EmailAddress(data[1]);
    	
		// We register our custom event
		PlayerVerifyEvent ese = new PlayerVerifyEvent(player);
		// If it's cancelled we return here
		if (ese.isCancelled()) {
			return;
		}
		
		// If no email has been sent we return 
		if (new Checker(player.getUniqueId(), email).isVerified()) {
			player.sendMessage(plugin.getMessage("commands.verify.noEmail"));
			return;
		}
		
		// If no email has been sent we return 
		if (!plugin.getPending().containsKey(player)) {
			player.sendMessage(plugin.getMessage("commands.verify.noEmail"));
			return;
		}
		
		// If the code is wrong we let the player know and return
		if (!code.equals(realCode)) {
			player.sendMessage(plugin.getMessage("errors.wrongCode"));
			return;
		}
		
		// We verify the player
    	new Checker(player.getUniqueId(), email).forceVerify();
    	// And remove them from the pending section
    	plugin.getPending().remove(player);
    	
    	// We send a successful verification message to the player
    	player.sendMessage(plugin.getMessage("commands.verify.success"));
    	
    	// We execute the actions specified in the config
		ActionUtil.execute(player, plugin.getConfigFile().getStringList("onVerify"));
		// And log what just happened
		CommonUtils.log(player.getName() + " has successfully verified themselves");
	}

}