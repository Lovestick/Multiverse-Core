package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.FancyColorScheme;
import com.onarandombox.utils.FancyHeader;
import com.onarandombox.utils.FancyMessage;
import com.onarandombox.utils.FancyText;

public class InfoCommand extends MultiverseCommand {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Information");
        this.setCommandUsage("/mv info" + ChatColor.GOLD + " [WORLD] [PAGE]");
        this.setArgRange(0, 2);
        this.addKey("mvinfo");
        this.addKey("mvi");
        this.addKey("mv info");
        this.setPermission("multiverse.core.info", "Returns detailed information on the world.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        String worldName = "";
        int pageNum = 0;

        if (args.size() == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                worldName = p.getWorld().getName();
            } else {
                sender.sendMessage("You must enter a" + ChatColor.LIGHT_PURPLE + " world" + ChatColor.WHITE + " from the console!");
                return;
            }
        }
        else if (args.size() == 1) {
            if (this.plugin.getMVWorld(args.get(0)) != null) {
                // then we have a world!
                worldName = args.get(0);
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    worldName = p.getWorld().getName();
                    try {
                        pageNum = Integer.parseInt(args.get(0)) - 1;
                    } catch (NumberFormatException e) {
                        pageNum = 0;
                    }
                } else {
                    sender.sendMessage("You must enter a" + ChatColor.LIGHT_PURPLE + " world" + ChatColor.WHITE + " from the console!");
                    return;
                }
            }
        }
        else if (args.size() == 2) {
            worldName = args.get(0);
            try {
                pageNum = Integer.parseInt(args.get(1)) - 1;
            } catch (NumberFormatException e) {
                pageNum = 0;
            }
        }

        if (this.plugin.isMVWorld(worldName)) {
            showPage(pageNum, sender, this.buildEntireCommand(this.plugin.getMVWorld(worldName)));
        } else if (this.plugin.getServer().getWorld(worldName) != null) {
            sender.sendMessage("That world exists, but multiverse does not know about it!");
            sender.sendMessage("You can import it with" + ChatColor.AQUA + "/mv import " + ChatColor.GREEN + worldName + ChatColor.LIGHT_PURPLE + "{ENV}");
            sender.sendMessage("For available environments type " + ChatColor.GREEN + "/mv env");
        }
    }

    private List<List<FancyText>> buildEntireCommand(MVWorld world) {
        List<FancyText> message = new ArrayList<FancyText>();
        List<List<FancyText>> worldInfo = new ArrayList<List<FancyText>>();
        // Page 1
        FancyColorScheme colors = new FancyColorScheme(ChatColor.AQUA, ChatColor.AQUA, ChatColor.GOLD, ChatColor.WHITE);
        message.add(new FancyHeader("General Info", colors));
        message.add(new FancyMessage("World Name: ", world.getName(), colors));
        message.add(new FancyMessage("World Alias: ", world.getColoredWorldString(), colors));
        message.add(new FancyMessage("Seed: ", world.getCBWorld().getSeed() + "", colors));
        Location spawn = world.getCBWorld().getSpawnLocation();
        message.add(new FancyMessage("Spawn Location: ", "(" + spawn.getBlockX() + ", " + spawn.getBlockY() + ", " + spawn.getBlockZ() + ")", colors));
        message.add(new FancyMessage("World Scale: ", world.getScaling().toString(), colors));
        if (world.getPrice() > 0) {
            message.add(new FancyMessage("Price to enter this world: ", this.plugin.getBank().getFormattedAmount(world.getPrice(), world.getCurrency()), colors));
        } else {
            message.add(new FancyMessage("Price to enter this world: ", ChatColor.GREEN + "FREE!", colors));
        }

        if (world.getRespawnToWorld() != null) {
            message.add(new FancyMessage("Players will respawn in: ", world.getColoredWorldString(), colors));
        }

        worldInfo.add(message);
        // Page 2
        message = new ArrayList<FancyText>();
        message.add(new FancyHeader("PVP Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.getPvp().toString(), colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getPVP() + "", colors));
        message.add(new FancyMessage("Fake PVP Enabled: ", world.getFakePVP() + "", colors));
        worldInfo.add(message);
        // Page 3
        message = new ArrayList<FancyText>();
        message.add(new FancyHeader("Monster Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.allowMonsterSpawning().toString(), colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getAllowMonsters() + "", colors));
        if (MultiverseCore.MobsDisabledInDefaultWorld) {
            message.add(new FancyMessage(ChatColor.RED + "WARNING: ", "Monsters WILL NOT SPAWN IN THIS WORLD.", colors));
            message.add(new FancyMessage(ChatColor.RED + "WARNING: ", "Check your server log for more details.", colors));
        }
        if (world.getMonsterList().size() > 0) {
            if (world.allowMonsterSpawning()) {
                message.add(new FancyMessage("Monsters that" + ChatColor.RED + " CAN NOT " + ChatColor.GREEN + "spawn: ", toCommaSeperated(world.getMonsterList()), colors));
            } else {
                message.add(new FancyMessage("Monsters that" + ChatColor.GREEN + " CAN SPAWN: ", toCommaSeperated(world.getMonsterList()), colors));
            }
        } else {
            message.add(new FancyMessage("Monsters that CAN spawn: ", world.allowMonsterSpawning() ? "ALL" : "NONE", colors));
        }
        worldInfo.add(message);

        // Page 4
        message = new ArrayList<FancyText>();
        message.add(new FancyHeader("Animal Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.allowAnimalSpawning().toString(), colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getAllowAnimals() + "", colors));
        if (world.getMonsterList().size() > 0) {
            if (world.allowMonsterSpawning()) {
                message.add(new FancyMessage("Animals that" + ChatColor.RED + " CAN NOT " + ChatColor.GREEN + "spawn: ", toCommaSeperated(world.getMonsterList()), colors));
            } else {
                message.add(new FancyMessage("Animals that" + ChatColor.GREEN + " CAN SPAWN: ", toCommaSeperated(world.getMonsterList()), colors));
            }
        } else {
            message.add(new FancyMessage("Animals that CAN spawn: ", world.allowMonsterSpawning() ? "ALL" : "NONE", colors));
        }
        worldInfo.add(message);

        return worldInfo;
    }

    private String toCommaSeperated(List<String> list) {
        String result = list.get(0);

        for (int i = 1; i < list.size() - 1; i++) {
            result += ", " + list.get(i);
        }
        result += " and " + list.get(list.size() - 1);
        return result;
    }

    protected ChatColor getChatColor(boolean positive) {
        return positive ? ChatColor.GREEN : ChatColor.RED;
    }

    private void showPage(int page, CommandSender sender, List<List<FancyText>> doc) {
        page = page < 0 ? 0 : page;
        page = page > doc.size() - 1 ? doc.size() - 1 : page;
        boolean altColor = false;
        boolean appendedPageNum = false;
        if (sender instanceof Player) {
            List<FancyText> list = doc.get(page);
            for (FancyText fancyT : list) {
                if (fancyT instanceof FancyMessage) {
                    FancyMessage text = (FancyMessage) fancyT;
                    text.setAltColor(altColor);
                    altColor = !altColor;
                    sender.sendMessage(text.getFancyText());
                } else if (fancyT instanceof FancyHeader) {
                    FancyHeader text = (FancyHeader) fancyT;
                    if (!appendedPageNum) {
                        text.appendText(ChatColor.DARK_PURPLE + " [ Page " + (page + 1) + " of " + doc.size() + " ]");
                        appendedPageNum = true;
                    }
                    sender.sendMessage(text.getFancyText());
                    altColor = false;
                }
            }

        } else {
            for (List<FancyText> list : doc) {
                for (FancyText fancyT : list) {
                    if (fancyT instanceof FancyMessage) {
                        FancyMessage text = (FancyMessage) fancyT;
                        text.setAltColor(altColor);
                        altColor = !altColor;
                        sender.sendMessage(text.getFancyText());
                    } else {
                        FancyText text = fancyT;
                        if (appendedPageNum) {
                            sender.sendMessage(" ");
                        } else {
                            appendedPageNum = true;
                        }
                        sender.sendMessage(text.getFancyText());
                        altColor = false;
                    }
                }
            }
        }
    }

}
