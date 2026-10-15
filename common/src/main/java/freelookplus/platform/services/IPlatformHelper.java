package freelookplus.platform.services;

import java.nio.file.Path;
import java.util.List;

/** Loader-specific lookups that common code needs. Each loader provides one through ServiceLoader. */
public interface IPlatformHelper {

    /** The name of the mod loader, for logging. */
    String getPlatformName();

    /** The loader's config directory. */
    Path getConfigDir();

    /**
     * Names of the installed mods that show the setting for this movement id in their own config screen,
     * as declared in their metadata under "freelookplus:shared_settings".
     */
    List<String> getModsSharingSetting(String movementId);
}
