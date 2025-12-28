package one.moriLib;

import one.moriLib.api.MoriLibAPI;
import one.moriLib.patcher.NoteBlockPatcher;
import one.moriLib.patcher.Patcher;
import one.moriLib.web.ResourcePack;
import org.bukkit.plugin.java.JavaPlugin;

public class MoriLibImpl implements MoriLibAPI {
    private static JavaPlugin plugin;
    private Patcher patcher;
    private NoteBlockPatcher noteBlockPatcher;
    private ResourcePack resourcePack;

    public MoriLibImpl(JavaPlugin plugin,
                       Patcher patcher,
                       NoteBlockPatcher noteBlockPatcher,
                       ResourcePack resourcePack) {
        MoriLibImpl.plugin = plugin;
        this.patcher = patcher;
        this.noteBlockPatcher = noteBlockPatcher;
        this.resourcePack = resourcePack;
    }

    @Override
    public void registerBlock(String name, String modId) {
        patcher.addBlockReference(name, modId);
    }

    @Override
    public void flush() {
        noteBlockPatcher.patchNoteBlock(resourcePack.getFinalDir(), patcher.getBlocks());
        noteBlockPatcher.generateNoteBlockItemJson(resourcePack.getFinalDir(), patcher.getBlocks());
        resourcePack.buildPatchedResourcePack();
        plugin.getLogger().info("Flushed mod changes!");
    }
}
