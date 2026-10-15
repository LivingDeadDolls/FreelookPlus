package freelookplus;

import freelookplus.config.FreelookPlusConfig;
import java.lang.ref.WeakReference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

/**
 * Freelook: while the key is held (or after pressing it, in toggle mode), the mouse turns only the camera,
 * so you can look around without changing where the player faces. The view (first or third person) is left as is.
 * Each {@link Movement} can be turned off in the settings.
 *
 * <p>The camera direction is fixed in the world, so in a turning vehicle it does not turn with it.
 */
public final class Freelook {
    /**
     * Name of the key mapping. Other mods with a built-in Freelook can register theirs under the same name
     * (only when this mod is absent) so the binding saved in options.txt carries over both ways.
     */
    public static final String KEY_NAME = "key.freelookplus.freelook";

    private static KeyMapping key;
    /** The player the state below belongs to. Weak, so it does not keep a closed world in memory. */
    private static WeakReference<LocalPlayer> lastPlayer = new WeakReference<>(null);
    /** Toggle mode: whether the last press turned Freelook on. */
    private static boolean toggledOn;
    private static boolean active;
    private static float yRot;
    private static float xRot;

    private Freelook() {
    }

    /** Set once by the loader's client entrypoint after registering the key. */
    public static void setKey(KeyMapping keyMapping) {
        key = keyMapping;
    }

    /**
     * Turns Freelook on or off to match the key. Called every client tick, and also right before mouse movement
     * is applied so that pressing or releasing the key takes effect on the very next mouse movement.
     */
    public static void update(Minecraft minecraft) {
        if (key == null) {
            return;
        }
        FreelookPlusConfig config = FreelookPlusConfig.get();
        boolean pressed = false;
        // Always drain presses so that switching to toggle mode does not replay old ones.
        while (key.consumeClick()) {
            pressed = !pressed;
        }
        LocalPlayer player = minecraft.player;
        if (lastPlayer.get() != player) {
            // Respawned or joined another world: start over instead of keeping the old direction.
            lastPlayer = new WeakReference<>(player);
            toggledOn = false;
            active = false;
        }
        boolean allowed = player != null
                && config.enabled
                // Spectating another entity: the camera follows that entity, not the player.
                && minecraft.getCameraEntity() == player
                && Movement.of(player).isEnabled(config);
        if (!allowed) {
            // Toggle mode starts over once Freelook can be used again.
            toggledOn = false;
            active = false;
            return;
        }
        boolean wanted;
        if (config.keyMode == KeyMode.TOGGLE) {
            if (pressed) {
                toggledOn = !toggledOn;
            }
            wanted = toggledOn;
        } else {
            toggledOn = false;
            wanted = key.isDown();
        }
        if (wanted && !active) {
            yRot = player.getYRot();
            xRot = player.getXRot();
        }
        active = wanted;
    }

    public static boolean isActive() {
        return active;
    }

    public static float yRot() {
        return yRot;
    }

    public static float xRot() {
        return xRot;
    }

    /** Mouse movement while active; same scale and clamp as Entity.turn. */
    public static void turn(double xo, double yo) {
        yRot += (float) xo * 0.15F;
        xRot = Mth.clamp(xRot + (float) yo * 0.15F, -90.0F, 90.0F);
    }
}
