package net.kris.ymp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import net.fabricmc.loader.api.FabricLoader;

public class YmpConfig {
    private static YmpConfig current_config;
    protected File configFile;
    protected ConfigValues config;
    protected YmpConfig(File configFile) throws IOException {
        this.configFile = configFile;
        try {
            reloadFromFile();
        } catch (FileNotFoundException|JsonParseException e) {
            config = new ConfigValues();
            updateFile();
        }
    }
    public static File getConfigDir() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(),"yarn-mapping");
    }
    public File getConfigFile() {
        return this.configFile;
    }
    public Integer getBuildNumber(String version) {
        return this.config.buildNumbers.get(version);
    }
    public void setBuildNumber(String version,int number) {
        this.config.buildNumbers.put(version,number);
    }
    public static YmpConfig getConfig() {
        if (current_config != null)
            return current_config;
        try {
            return current_config = new YmpConfig(new File(getConfigDir(),"config.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void reloadFromFile() throws IOException {
        config = new Gson().fromJson(new String(Files.readAllBytes(configFile.toPath())),ConfigValues.class);
    }
    public void updateFile() throws IOException {
        Files.write(configFile.toPath(),new Gson().toJson(config).getBytes());
    }
    class ConfigValues {
        protected HashMap<String,Integer> buildNumbers = new HashMap<>();
    }
}
