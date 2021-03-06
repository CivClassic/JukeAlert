package com.untamedears.jukealert.commands;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.util.JASettingsManager;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.group.Group;

@CivCommand(id = "jamute")
public class MuteCommand extends StandaloneCommand {
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Players only");
			return true;
		}
		if (args.length == 0) {
			return false;
		}
		Player player = (Player) sender;
		Group group = GroupManager.getGroup(args[0]);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "The group " + args[0] + " does not exist");
			return true;
		}
		JASettingsManager settingsManager = JukeAlert.getInstance().getSettingsManager();
		if (settingsManager.doesIgnoreAlert(group.getName(), player.getUniqueId())) {
			settingsManager.getIgnoredGroupAlerts().removeElement(player.getUniqueId(), group.getName());
			player.sendMessage(ChatColor.GREEN + "You have unmuted " + group.getName());
			return true;
		}
		settingsManager.getIgnoredGroupAlerts().addElement(player.getUniqueId(), group.getName());
		player.sendMessage(ChatColor.GREEN + "You have muted " + group.getName());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null;
	}
}
