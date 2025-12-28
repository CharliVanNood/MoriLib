package one.moriLib.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import one.moriLib.entities.Block;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GiveBlock implements CommandExecutor {
    private final List<Block> blocks;

    public GiveBlock(List<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /giveBlock name");
            return true;
        }

        int customModelData = -1;
        String blockName = args[0];
        for (Block block : blocks) {
            if (block.getNameDefault().equalsIgnoreCase(args[0])) {
                customModelData = block.getId();
                blockName = block.getName();
            }
        }
        if (customModelData == -1) {
            player.sendMessage("This block does not exist.");
            return true;
        }

        ItemStack noteBlock = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta meta = noteBlock.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            meta.displayName(Component.text(blockName).decoration(TextDecoration.ITALIC, false));
            noteBlock.setItemMeta(meta);
        }

        player.getInventory().addItem(noteBlock);
        player.sendMessage("Gave " + blockName);

        return true;

    }
}