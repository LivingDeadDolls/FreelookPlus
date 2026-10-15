package freelookplus;

import freelookplus.config.FreelookPlusConfig;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

/**
 * Ways of moving that Freelook can be turned on or off for. The id is used in the lang keys, in
 * {@link freelookplus.api.FreelookPlusApi} and in other mods' "freelookplus:shared_settings" metadata.
 */
public enum Movement {
    ON_FOOT("on_foot", config -> config.onFoot, (config, value) -> config.onFoot = value),
    SWIMMING("swimming", config -> config.swimming, (config, value) -> config.swimming = value),
    ELYTRA("elytra", config -> config.elytra, (config, value) -> config.elytra = value),
    MINECART("minecart", config -> config.minecart, (config, value) -> config.minecart = value),
    BOAT("boat", config -> config.boat, (config, value) -> config.boat = value),
    MOUNT("mount", config -> config.mount, (config, value) -> config.mount = value);

    private final String id;
    private final Predicate<FreelookPlusConfig> getter;
    private final BiConsumer<FreelookPlusConfig, Boolean> setter;

    Movement(String id, Predicate<FreelookPlusConfig> getter, BiConsumer<FreelookPlusConfig, Boolean> setter) {
        this.id = id;
        this.getter = getter;
        this.setter = setter;
    }

    public String id() {
        return id;
    }

    public boolean isEnabled(FreelookPlusConfig config) {
        return getter.test(config);
    }

    public void setEnabled(FreelookPlusConfig config, boolean value) {
        setter.accept(config, value);
    }

    /** Null for an unknown id. */
    public static Movement byId(String id) {
        for (Movement movement : values()) {
            if (movement.id.equals(id)) {
                return movement;
            }
        }
        return null;
    }

    /** How the player is moving now: elytra, then minecart, boat, other mounts, swimming, and on foot. */
    public static Movement of(LocalPlayer player) {
        if (player.isFallFlying()) {
            return ELYTRA;
        }
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof AbstractMinecart) {
            return MINECART;
        }
        if (vehicle instanceof AbstractBoat) {
            return BOAT;
        }
        if (vehicle != null) {
            return MOUNT;
        }
        if (player.isSwimming()) {
            return SWIMMING;
        }
        return ON_FOOT;
    }
}
