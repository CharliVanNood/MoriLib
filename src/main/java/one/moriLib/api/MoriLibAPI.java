package one.moriLib.api;

public interface MoriLibAPI {
    /**
     * Registers a new block.
     *
     * @param name    your block's name
     * @param texture the path to the texture
     */
    void registerBlock(String name, String texture);
}
