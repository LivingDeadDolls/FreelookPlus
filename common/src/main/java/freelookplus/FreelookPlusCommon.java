package freelookplus;

import freelookplus.config.FreelookPlusConfig;

/** Loader-independent startup, called from each loader's client entrypoint. */
public final class FreelookPlusCommon {
    private FreelookPlusCommon() {
    }

    public static void init() {
        FreelookPlusConfig.get();
        Constants.LOG.info("Freelook Plus loaded on {}", freelookplus.platform.Services.PLATFORM.getPlatformName());
    }
}
