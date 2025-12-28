package one.moriLib;

import one.moriLib.api.MoriLibAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class MoriLibImpl implements MoriLibAPI {
    private static JavaPlugin plugin;

    public MoriLibImpl(JavaPlugin plugin) {
        MoriLibImpl.plugin = plugin;
    }

    @Override
    public void registerBlock(String name, String texture) {
        plugin.getLogger().info("Registering block " + name);
    }
}
