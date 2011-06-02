package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;
import com.onarandombox.utils.Destination;
import com.onarandombox.utils.DestinationType;

public class TeleportCommand extends BaseCommand {
    
    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
        name = "Teleport";
        description = "Teleports you to a different world.";
        usage = "/mvcoord" + ChatColor.RED + "{WORLD}";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("mvtp");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            Player p = (Player) sender;
            // If this command was sent from a Player then we need to check Permissions
            if (!(plugin.ph.has((p), "multiverse.tp"))) {
                p.sendMessage("You do not have access to this command.");
                return;
            }
            Destination d = Destination.parseDestination(args[1]);
            // TODO: I'd like to find a way to do these next bits inside Destination, so we're always valid --FF
            // TODO: Support portals, but I didn't see the portals list --FF
            if (this.plugin.worlds.containsKey(d.getName().toLowerCase())) {
                if(d.getType() == DestinationType.World) {
                    
                }
            } else {
                p.sendMessage("That was not a valid world (portals aren't yet supported)");
            }
            
        } else {
            sender.sendMessage("This command needs to be used from a Player.");
        }
    }
    
}
