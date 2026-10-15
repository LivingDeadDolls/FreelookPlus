package freelookplus;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class FreelookPlusFabric implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "main"));

    @Override
    public void onInitializeClient() {
        FreelookPlusCommon.init();

        // Unbound by default; set it in Controls.
        KeyMapping freelook = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(Freelook.KEY_NAME, InputConstants.UNKNOWN.getValue(), CATEGORY));
        Freelook.setKey(freelook);
        ClientTickEvents.END_CLIENT_TICK.register(Freelook::update);
    }
}
