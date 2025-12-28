package one.moriLib.web;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class ResourcePackServer extends NanoHTTPD {
    private final JavaPlugin plugin;

    public ResourcePackServer(JavaPlugin plugin) {
        super(8080);
        this.plugin = plugin;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        if (Objects.equals(uri, "/resourcepack")) {
            File zipFile = new File("plugins/acropora/PatchedResourcePack.zip");

            if (zipFile.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(zipFile);
                    return newChunkedResponse(Response.Status.OK, "application/zip", fis);
                } catch (IOException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error serving resource pack.");
                }
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Resource pack not found.");
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }

    public void startServer() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            plugin.getLogger().info("ResourcePackServer started on port 8080");
        } catch (IOException e) {
            plugin.getLogger().severe("Resource Server could not be started on port 8080");
            plugin.getLogger().severe("This means players wont get the correct resource pack");
        }
    }
}
