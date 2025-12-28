package one.moriLib.files;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public static File downloadFile(String urlString, File targetFile) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Referer", "https://texture-packs.com/");
        connection.connect();

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return targetFile;
    }

    public static void downloadTexturePack(File unzipDir, File dataFolder, JavaPlugin plugin) {
        File zipFile = new File(dataFolder, "resourcepack.zip");

        if (!unzipDir.exists()) {
            plugin.getLogger().info("Default resource pack not found, downloading...");

            try {
                downloadFile("https://ch4rli.me/acropora/1.21.zip", zipFile);
                plugin.getLogger().info("Downloaded resource pack, extracting...");
                unzip(zipFile, unzipDir);
                plugin.getLogger().info("Resource pack unzipped to " + unzipDir.getAbsolutePath());
                plugin.getLogger().info("Deleting ResourcePack.zip");
                FileUtils.deleteDirectory(zipFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to download or unzip resource pack!");
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().info("Default resource pack already exists");
        }
    }

    public static void zipDirectory(File sourceDir, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFile(sourceDir, sourceDir.getName(), zos);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }

    public static void zipDirectoryContents(File sourceDir, File outputZip) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputZip);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Path sourcePath = sourceDir.toPath();

            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            // Relative path inside the zip (no leading folder)
                            String entryName = sourcePath.relativize(path).toString().replace("\\", "/");
                            zos.putNextEntry(new ZipEntry(entryName));
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    public static void unzip(File zipFile, File targetDir) throws IOException {
        if (!targetDir.exists()) targetDir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    // Ensure parent directories exist
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        Files.walk(sourceDir.toPath()).forEach(source -> {
            Path destination = targetDir.toPath().resolve(sourceDir.toPath().relativize(source));
            try {
                if (Files.isDirectory(source)) {
                    if (!Files.exists(destination)) {
                        Files.createDirectories(destination);
                    }
                } else {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static void deleteFile(File dir) throws IOException {
        if (dir.exists()) {
            try {
                Files.delete(dir.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete " + dir.toPath(), e);
            }
        }
    }

    public static void deleteDirectory(File dir) throws IOException {
        if (dir.exists()) {
            Files.walk(dir.toPath())
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete " + path, e);
                    }
                });
        }
    }

    public static void deleteDirectoryContents(File dir) throws IOException {
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectoryContents(file);
                }
                if (!file.delete()) {
                    throw new IOException("Failed to delete " + file.getAbsolutePath());
                }
            }
        }
    }
}