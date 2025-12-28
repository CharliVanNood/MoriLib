package one.moriLib;

import one.moriLib.api.MoriLibAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class MoriLibImpl extends JavaPlugin implements MoriLibAPI {
    private static JavaPlugin plugin;

    MoriLibImpl(JavaPlugin plugin) {
        MoriLibImpl.plugin = plugin;
    }

    @Override
    public void registerBlock(String name, String texture) {
        plugin.getLogger().info("Registering block " + name);
    }
}
