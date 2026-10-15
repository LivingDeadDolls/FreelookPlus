package freelookplus.api;

import freelookplus.Movement;
import freelookplus.config.FreelookPlusConfig;

/**
 * For other mods that show Freelook Plus settings in their own config screen. Only static methods with
 * JDK types, so they can be called through reflection without a compile-time dependency on this mod.
 *
 * <p>Movement ids: "on_foot", "swimming", "elytra", "minecart", "boat", "mount". A mod that shows one of these
 * settings should list its id in its fabric.mod.json, so that Freelook Plus names it in the setting's description:
 * {@code "custom": {"freelookplus:shared_settings": ["elytra"]}}.
 */
public final class FreelookPlusApi {
    private FreelookPlusApi() {
    }

    /** The overall Freelook setting; when off, Freelook is off for every kind of movement. */
    public static boolean isEnabled() {
        return FreelookPlusConfig.get().enabled;
    }

    /** The setting for this movement (Movement tab). Throws IllegalArgumentException for an unknown id. */
    public static boolean isMovementEnabled(String movementId) {
        return movement(movementId).isEnabled(FreelookPlusConfig.get());
    }

    /** Changes the setting for this movement (Movement tab) and saves it. Throws IllegalArgumentException for an unknown id. */
    public static void setMovementEnabled(String movementId, boolean value) {
        movement(movementId).setEnabled(FreelookPlusConfig.get(), value);
        FreelookPlusConfig.HANDLER.save();
    }

    private static Movement movement(String id) {
        Movement movement = Movement.byId(id);
        if (movement == null) {
            throw new IllegalArgumentException("Unknown movement: " + id);
        }
        return movement;
    }
}
