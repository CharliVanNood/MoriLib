package one.moriLib.commands;

import one.moriLib.entities.Block;
import one.moriLib.patcher.Patcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockTabComplete implements TabCompleter {
    private final Patcher patcher;

    public BlockTabComplete(Patcher patcher) {
        this.patcher = patcher;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String current = args[0].toLowerCase();
            for (Block block : patcher.getBlocks()) {
                if (block.getNameDefault().startsWith(current)) {
                    suggestions.add(block.getNameDefault());
                }
            }
        }

        return suggestions;
    }
}
