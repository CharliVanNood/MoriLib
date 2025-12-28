package one.moriLib.entities;

import org.bukkit.Instrument;
import org.bukkit.Note;

public class Block {
    private final String name;
    private final String DisplayName;
    private final String modId;
    private final int id;
    private Note note;
    private Instrument instrument;
    private boolean powered;

    public Block(String name, String modId, int id) {
        this.DisplayName = name.replace("_", " ");
        this.name = name;
        this.modId = modId;
        this.id = id;
        note = null;
        instrument = null;
        powered = false;
    }

    public String getName() {
        return DisplayName;
    }
    public String getNameDefault() {
        return name;
    }
    public String getModId() {
        return modId;
    }

    public int getId() {
        return id;
    }

    public Note getNote() {
        return note;
    }
    public Instrument getInstrument() {
        return instrument;
    }
    public boolean getPowered() {
        return powered;
    }

    public Instrument stringToInstrument(String instrumentName) {
        if (instrumentName == null) return Instrument.PIANO;

        try {
            return Instrument.valueOf(instrumentName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Instrument.PIANO;
        }
    }

    public void setParams(int note_in, String instrument_in, boolean powered_in) {
        note = new Note(note_in);
        instrument = stringToInstrument(instrument_in);
        powered = powered_in;
    }
}
