package one.moriLib.web;

import one.moriLib.files.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ResourcePack {
    private static JavaPlugin plugin;
    private File dataFolder;
    private File finalDir;
    private File zipOut;

    public ResourcePack(JavaPlugin plugin) {
        ResourcePack.plugin = plugin;
    }

    public File getFinalDir() {
        return finalDir;
    }

    public File getZipOut() {
        return zipOut;
    }

    public void createPatchedResourcePack() {
        dataFolder = plugin.getDataFolder();
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

    public void buildPatchedResourcePack() {
        zipOut = new File(dataFolder, "PatchedResourcePack.zip");
        try {
            FileUtils.zipDirectoryContents(finalDir, zipOut);
            plugin.getLogger().info("Repacked PatchedResourcePack into " + zipOut.getAbsolutePath());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to zip PatchedResourcePack!");
            e.printStackTrace();
        }
    }
}
