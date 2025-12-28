package one.moriLib.patcher;
import one.moriLib.entities.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Patcher {
    private static JavaPlugin plugin;
    private static final List<Block> blocks = new ArrayList<>();

    private static Patcher instance;

    public Patcher(JavaPlugin plugin) {
        instance = this;
        Patcher.plugin = plugin;
    }

    public static Patcher get() {
        return instance;
    }

    public void addBlockReference(String name) {
        blocks.add(new Block(name, blocks.size() + 1));
        plugin.getLogger().info("Added block " + name);
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
