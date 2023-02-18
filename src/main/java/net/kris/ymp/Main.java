package net.kris.ymp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.io.IOException;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MODID = "yarn_mapping_provider";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		try {
			MappingFile.check();
			MappingFile.apply();
			Field field = FabricLoaderImpl.class.getDeclaredField("mappingResolver");
			field.setAccessible(true);
			field.set(FabricLoader.getInstance(),null);
			FabricLoader.getInstance().getMappingResolver();
			LOGGER.info("Yarn Mapping Provider initialized!");
		} catch (IOException|RuntimeException | NoSuchFieldException | IllegalAccessException e) {
			LOGGER.error("Yarn Mapping Provider failed: {}",e.toString());
		}
	}
}
