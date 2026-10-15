package freelookplus.platform;

import freelookplus.platform.services.IPlatformHelper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class FabricPlatformHelper implements IPlatformHelper {
    /**
     * fabric.mod.json custom value of mods that share settings, for example
     * {@code "custom": {"freelookplus:shared_settings": ["elytra"]}}.
     */
    private static final String SHARED_SETTINGS_KEY = "freelookplus:shared_settings";

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public List<String> getModsSharingSetting(String movementId) {
        List<String> names = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            CustomValue value = metadata.getCustomValue(SHARED_SETTINGS_KEY);
            if (value != null && declares(value, movementId)) {
                names.add(metadata.getName());
            }
        }
        // getAllMods() has no fixed order.
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    /** Accepts an array of ids, or a single id as a string. */
    private static boolean declares(CustomValue value, String movementId) {
        if (value.getType() == CustomValue.CvType.STRING) {
            return value.getAsString().equals(movementId);
        }
        if (value.getType() == CustomValue.CvType.ARRAY) {
            for (CustomValue entry : value.getAsArray()) {
                if (entry.getType() == CustomValue.CvType.STRING && entry.getAsString().equals(movementId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
