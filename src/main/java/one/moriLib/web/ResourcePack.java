package one.moriLib.web;

import one.moriLib.files.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ResourcePack {
    private static JavaPlugin plugin;
    private File finalDir;

    public ResourcePack(JavaPlugin plugin) {
        ResourcePack.plugin = plugin;
    }

    public File getFinalDir() {
        return finalDir;
    }

    public void createPatchedResourcePack() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean can_create = dataFolder.mkdirs();
            if (!can_create) plugin.getLogger().severe("Failed to create data folder");
        }
        File unzipDir = new File(dataFolder, "ResourcePack");
        finalDir = new File(dataFolder, "PatchedResourcePack");

        // Download default pack if it doesn't exist yet
        FileUtils.downloadTexturePack(unzipDir, dataFolder, plugin);

        try {
            plugin.getLogger().info("Emptying PatchedResourcePack");
            FileUtils.deleteDirectoryContents(finalDir);
            plugin.getLogger().info("Copying to PatchedResourcePack");
            FileUtils.copyDirectory(unzipDir, finalDir);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to copy resource pack to " + finalDir.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
