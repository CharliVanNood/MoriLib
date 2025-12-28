package one.moriLib;

import one.moriLib.api.MoriLibAPI;
import one.moriLib.commands.BlockTabComplete;
import one.moriLib.commands.GiveBlock;
import one.moriLib.events.JoinEvent;
import one.moriLib.events.OnInteract;
import one.moriLib.events.OnPlace;
import one.moriLib.files.FileUtils;
import one.moriLib.patcher.NoteBlockPatcher;
import one.moriLib.patcher.Patcher;
import one.moriLib.web.ResourcePack;
import one.moriLib.web.ResourcePackServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MoriLib extends JavaPlugin {
    private ResourcePackServer resourcePackServer;
    private Patcher patcher;
    private static MoriLibAPI api;

    @Override
    public void onEnable() {
        ResourcePack resourcePack = new ResourcePack(this);
        resourcePack.createPatchedResourcePack();

        getLogger().info("Creating Patcher");
        patcher = new Patcher(this);

        getLogger().info("Starting API handler");
        api = new MoriLibImpl(this);

        getLogger().info("Creating NoteBlock Patcher");
        NoteBlockPatcher noteBlockPatcher = new NoteBlockPatcher(this);
        noteBlockPatcher.patchNoteBlock(resourcePack.getFinalDir(), patcher.getBlocks());
        noteBlockPatcher.generateNoteBlockItemJson(resourcePack.getFinalDir(), patcher.getBlocks());
        resourcePack.buildPatchedResourcePack();
        getLogger().info("Resource Pack has been patched");

        resourcePackServer = new ResourcePackServer(this, resourcePack.getFinalDir());
        resourcePackServer.startServer();

        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getLogger().info("Resource pack join handler enabled.");

        getServer().getPluginManager().registerEvents(new OnPlace(patcher.getBlocks()), this);
        getServer().getPluginManager().registerEvents(new OnInteract(this, patcher.getBlocks()), this);
        getLogger().info("Events enabled.");

        this.getCommand("giveBlock").setExecutor(new GiveBlock(patcher.getBlocks()));
        this.getCommand("giveBlock").setTabCompleter(new BlockTabComplete(patcher.getBlocks()));
        getLogger().info("Commands enabled.");

        getLogger().info("MoriLib has been enabled!");
    }

    public static MoriLibAPI getAPI() {
        return api;
    }

    @Override
    public void onDisable() {
        if (resourcePackServer != null) {
            resourcePackServer.stop();
            getLogger().info("ResourcePackServer stopped.");
        }
        getLogger().info("MoriLib is disabled!");
    }
}
