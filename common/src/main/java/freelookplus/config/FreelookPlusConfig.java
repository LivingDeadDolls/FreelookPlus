package freelookplus.config;

import freelookplus.Constants;
import freelookplus.KeyMode;
import freelookplus.Movement;
import freelookplus.platform.Services;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

/** Settings saved to config/freelookplus.json5. */
public final class FreelookPlusConfig {
    public static final ConfigClassHandler<FreelookPlusConfig> HANDLER = ConfigClassHandler.createBuilder(FreelookPlusConfig.class)
            .id(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Services.PLATFORM.getConfigDir().resolve(Constants.MOD_ID + ".json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public boolean enabled = true;

    @SerialEntry
    public KeyMode keyMode = KeyMode.HOLD;

    @SerialEntry
    public boolean onFoot = true;

    @SerialEntry
    public boolean swimming = true;

    @SerialEntry
    public boolean elytra = true;

    @SerialEntry
    public boolean minecart = true;

    @SerialEntry
    public boolean boat = true;

    @SerialEntry
    public boolean mount = true;

    private static boolean loaded;

    /**
     * Loads the file on first use, so that other mods calling the API before our own entrypoint has run
     * neither read defaults nor save them over the user's settings.
     */
    public static FreelookPlusConfig get() {
        if (!loaded) {
            loaded = true;
            HANDLER.load();
            // An unknown value in the file is read as null.
            if (HANDLER.instance().keyMode == null) {
                HANDLER.instance().keyMode = KeyMode.HOLD;
                HANDLER.save();
            }
        }
        return HANDLER.instance();
    }

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            ConfigCategory.Builder movement = ConfigCategory.createBuilder()
                    .name(Component.translatable("freelookplus.config.category.movement"));
            for (Movement type : Movement.values()) {
                movement.option(movementOption(type, defaults, config));
            }
            return builder
                    .title(Component.translatable("freelookplus.config.title"))
                    .category(ConfigCategory.createBuilder()
                            .name(Component.translatable("freelookplus.config.category.general"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.translatable("freelookplus.config.enabled"))
                                    .description(OptionDescription.of(Component.translatable("freelookplus.config.enabled.desc")))
                                    .binding(defaults.enabled, () -> config.enabled, value -> config.enabled = value)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<KeyMode>createBuilder()
                                    .name(Component.translatable("freelookplus.config.key_mode"))
                                    .description(OptionDescription.of(Component.translatable("freelookplus.config.key_mode.desc")))
                                    .binding(defaults.keyMode, () -> config.keyMode, value -> config.keyMode = value)
                                    .controller(option -> EnumControllerBuilder.create(option)
                                            .enumClass(KeyMode.class)
                                            .formatValue(mode -> Component.translatable(
                                                    "freelookplus.config.key_mode." + mode.name().toLowerCase(Locale.ROOT))))
                                    .build())
                            .build())
                    .category(movement.build());
        }).generateScreen(parent);
    }

    /** Other mods that show this setting too are listed in the description, only when they are installed. */
    private static Option<Boolean> movementOption(Movement type, FreelookPlusConfig defaults, FreelookPlusConfig config) {
        String key = "freelookplus.config." + type.id();
        MutableComponent description = Component.translatable(key + ".desc");
        List<String> sharedWith = Services.PLATFORM.getModsSharingSetting(type.id());
        if (!sharedWith.isEmpty()) {
            description.append("\n\n").append(Component.translatable("freelookplus.config.shared", String.join(", ", sharedWith)));
        }
        return Option.<Boolean>createBuilder()
                .name(Component.translatable(key))
                .description(OptionDescription.of(description))
                .binding(type.isEnabled(defaults), () -> type.isEnabled(config), value -> type.setEnabled(config, value))
                .controller(TickBoxControllerBuilder::create)
                .build();
    }
}
