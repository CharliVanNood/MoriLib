package one.moriLib.patcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import one.moriLib.entities.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NoteBlockPatcher {
    private static JavaPlugin plugin;

    public NoteBlockPatcher(JavaPlugin plugin) {
        NoteBlockPatcher.plugin = plugin;
    }

    public void patchNoteBlock(File finalDir, List<Block> blocks) {
        File noteBlockFile = new File(finalDir, "assets/minecraft/blockstates/note_block.json");
        noteBlockFile.getParentFile().mkdirs();

        try {
            try (FileWriter writer = new FileWriter(noteBlockFile, false)) {
                writer.write(generateNoteBlockJson(blocks));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNoteBlockItemJson(File finalDir, List<Block> blocks) {
        JsonObject root = new JsonObject();
        root.addProperty("parent", "minecraft:block/note_block");

        JsonArray overrides = new JsonArray();
        int modelId = 1;

        for (Block block : blocks) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", modelId);

            override.add("predicate", predicate);
            override.addProperty("model", block.getModId() + ":block/" + block.getName().toLowerCase().replace(" ", "_"));

            overrides.add(override);

            modelId++;
        }

        root.add("overrides", overrides);

        File itemJsonFile = new File(finalDir, "assets/minecraft/models/item/note_block.json");
        itemJsonFile.getParentFile().mkdirs();

        try {
            try (FileWriter writer = new FileWriter(itemJsonFile)) {
                writer.write(root.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateNoteBlockJson(List<Block> blocks) {
        JsonObject root = new JsonObject();
        JsonObject variants = new JsonObject();

        String[] instruments = {
                "harp","basedrum","snare","hat","bass","flute","bell","guitar","chime","xylophone"
        };

        int blockIndex = 0;
        outer:
        for (String instrument : instruments) {
            for (int note = 0; note <= 24; note++) {
                for (boolean powered : new boolean[]{false, true}) {
                    if (blockIndex >= blocks.size()) break outer;

                    String key = "instrument=" + instrument + ",note=" + note + ",powered=" + powered;
                    JsonObject model = new JsonObject();
                    model.addProperty("model", blocks.get(blockIndex).getModId() + ":block/" + blocks.get(blockIndex).getName().toLowerCase().replace(" ", "_"));

                    Block block = blocks.get(blockIndex);
                    block.setParams(note, instrument, powered);
                    variants.add(key, model);
                    blockIndex += 1;
                }
            }
        }

        root.add("variants", variants);
        return root.toString();
    }
}
