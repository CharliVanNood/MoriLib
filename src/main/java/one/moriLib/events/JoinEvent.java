package one.moriLib.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;

public class JoinEvent implements Listener {

    private final JavaPlugin plugin;
    private final String packUrl;
    private final byte[] packHash;

    public JoinEvent(JavaPlugin plugin) {
        this.plugin = plugin;

        String localIp = getLocalIp();
        this.packUrl = "http://" + localIp + ":8080/resourcepack";
        plugin.getLogger().info("Pack URL: " + packUrl);

        File packFile = new File(plugin.getDataFolder(), "PatchedResourcePack.zip");
        this.packHash = calculateSHA1(packFile);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (packHash != null) {
            event.getPlayer().setResourcePack(packUrl, packHash);
            plugin.getLogger().info("Sent resource pack to " + event.getPlayer().getName());
        } else {
            plugin.getLogger().warning("Resource pack not found or SHA1 invalid, skipping for " + event.getPlayer().getName());
        }
    }

    private byte[] calculateSHA1(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                sha1.update(buffer, 0, read);
            }
            return sha1.digest();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to calculate SHA1 of resource pack: " + e.getMessage());
            return null;
        }
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface netIf = nets.nextElement();
                if (netIf.isLoopback() || !netIf.isUp()) continue;

                Enumeration<InetAddress> addresses = netIf.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(":") == -1) {
                        return addr.getHostAddress(); // IPv4
                    }
                }
            }
        } catch (Exception ignored) {}
        return "127.0.0.1"; // fallback
    }
}
