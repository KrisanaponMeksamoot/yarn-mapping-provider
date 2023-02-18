package net.kris.ymp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.game.minecraft.McVersion;
import net.fabricmc.loader.impl.game.minecraft.McVersionLookup;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.launch.MappingConfiguration;
import net.fabricmc.mapping.reader.v2.MappingParseException;
import net.fabricmc.mapping.tree.TinyMappingFactory;

public class MappingFile {
    public static final String YARN_URL = "https://maven.fabricmc.net/net/fabricmc/yarn/";
    public static final McVersion MINECRAFT_VERSION = McVersionLookup.getVersionExceptClassVersion(FabricLoader.getInstance().getModContainer("minecraft").get().getRootPaths().iterator().next());
    private static int mc_build;
    public static void check() throws IOException {
        File config_dir = YmpConfig.getConfigDir();
        if (!config_dir.exists()) {
            config_dir.mkdir();
        }
        YmpConfig config = YmpConfig.getConfig();
        {
            Integer build_num = config.getBuildNumber(MINECRAFT_VERSION.getId());
            if (build_num == null) {
                JsonObject versions = new Gson().fromJson(new String(new URL(YARN_URL+"versions.json").openStream().readAllBytes()), JsonObject.class);
                int max = -1;
                if (versions.getAsJsonArray(MINECRAFT_VERSION.getId()) != null) {
                    for (JsonElement num : versions.getAsJsonArray(MINECRAFT_VERSION.getId())) {
                        max = Math.max(max,num.getAsInt());
                    }
                }
                mc_build = max;
            } else
                mc_build = build_num;
        }
        boolean num_not_found = mc_build == -1;
        if (num_not_found) {
            Main.LOGGER.warn("Unable to find mapping for version: {} using build.1",MINECRAFT_VERSION.toString());
            mc_build = 1;
        }
        try {
            File mappingFile = getMappingFile();
            if (!mappingFile.exists()) {
                downloadMappingFile(mappingFile);
            }
        } catch (IOException e) {
            if (num_not_found)
                throw new RuntimeException("Unable to find mapping for version: "+MINECRAFT_VERSION.toString());
            throw e;
        }
        config.setBuildNumber(MINECRAFT_VERSION.getId(), mc_build);
        config.updateFile();
    }
    public static void apply() {
        try {
            MappingConfiguration mappingConfiguration = FabricLauncherBase.getLauncher().getMappingConfiguration();
            Field field = MappingConfiguration.class.getDeclaredField("mappings");
            field.setAccessible(true);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(String.format("jar:%s!/mappings/mappings.tiny",getMappingFile().toURI().toURL().toString())).openStream()))) {
                field.set(mappingConfiguration, TinyMappingFactory.loadWithDetection(reader));
            }
        } catch (NoSuchFieldException|IllegalAccessException|MappingParseException|IOException e) {
            throw new RuntimeException("Apply mapping configuration failed!",e);
        }
    }
    public static File getMappingFile() {
        return new File(YmpConfig.getConfigDir(),getMappingFileName());
    }
    private static void downloadMappingFile(File file) throws IOException {
        Files.write(file.toPath(),new URL(String.format("%s%s/%s",YARN_URL,getMappingName(),getMappingFileName())).openStream().readAllBytes());
    }
    public static String getMappingFileName() {
        return String.format("yarn-%s.jar",getMappingName());
    }
    public static String getMappingName() {
        return String.format("%s+build.%d",MINECRAFT_VERSION.getId(),mc_build);
    }
}
