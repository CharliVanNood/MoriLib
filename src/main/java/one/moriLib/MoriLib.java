package one.moriLib;

import one.moriLib.files.FileUtils;
import one.moriLib.patcher.NoteBlockPatcher;
import one.moriLib.patcher.Patcher;
import one.moriLib.web.ResourcePack;
import one.moriLib.web.ResourcePackServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MoriLib extends JavaPlugin {
    private ResourcePackServer server;
    private Patcher patcher;

    @Override
    public void onEnable() {
        ResourcePack resourcePack = new ResourcePack(this);
        resourcePack.createPatchedResourcePack();

        getLogger().info("Creating Patcher");
        patcher = new Patcher(this);

        getLogger().info("Creating NoteBlock Patcher");
        NoteBlockPatcher noteBlockPatcher = new NoteBlockPatcher(this);
        noteBlockPatcher.patchNoteBlock(resourcePack.getFinalDir(), patcher.getBlocks());
        noteBlockPatcher.generateNoteBlockItemJson(resourcePack.getFinalDir(), patcher.getBlocks());

        getLogger().info("MoriLib has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MoriLib is disabled!");
    }
}
