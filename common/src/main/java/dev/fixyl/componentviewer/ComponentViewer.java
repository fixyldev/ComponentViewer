package dev.fixyl.componentviewer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.GameType;

import dev.fixyl.componentviewer.config.Configs;
import dev.fixyl.componentviewer.event.EventDispatcher;

/**
 * This mod's singleton.
 * <p>
 * Holds the logger, event dispatcher and configuration
 * of this mod.
 * <p>
 * Must be extended to implement platform-specific
 * initialization logic, like registering event listeners.
 */
public abstract class ComponentViewer implements DisablableMod {

    public static final String MOD_ID = "componentviewer";

    private static ComponentViewer instance;

    /**
     * This mod's logger.
     */
    public final Logger logger;
    /**
     * This mod's event dispatcher.
     */
    public final EventDispatcher eventDispatcher;
    /**
     * This mod's configuration.
     */
    public final Configs configs;

    protected ComponentViewer(EventDispatcher eventDispatcher, Path configDir) {
        ComponentViewer.setInstance(this);

        this.logger = LoggerFactory.getLogger(this.getClass());
        this.eventDispatcher = eventDispatcher;
        this.configs = new Configs(configDir, this.logger);
    }

    @Override
    public boolean isModDisabled() {
        return switch (this.configs.disableMod.getValue()) {
            case NEVER -> false;
            case IN_SURVIVAL -> this.currentlyInSurvival();
            case ON_SERVER -> this.currentlyOnServer();
            case BOTH -> this.currentlyInSurvival() && this.currentlyOnServer();
        };
    }

    /**
     * Dispatches an event safely to avoid unnecessary {@code null}
     * checks. This is useful if the event to be dispatched has also
     * a chance to fire, even if this mod hasn't initialized yet
     * (e.g. from mixins).
     *
     * @param consumer the consumer providing the event dispatcher
     */
    public static void dispatchEventSafely(Consumer<EventDispatcher> consumer) {
        if (ComponentViewer.instance != null) {
            consumer.accept(ComponentViewer.instance.eventDispatcher);
        }
    }

    /**
     * Dispatches an event safely to avoid unnecessary {@code null}
     * checks. This is useful if the event to be dispatched has also
     * a chance to fire, even if this mod hasn't initialized yet
     * (e.g. from mixins).
     * <p>
     * Unlike {@link ComponentViewer#dispatchEventSafely(Consumer)},
     * this allows the event to also safely return a result.
     * The returned {@link Optional} will be empty in case the
     * event could't be dispatched safely, and therefore has no
     * result to return.
     *
     * @param <R> the type of the result value
     * @param function the function providing the event dispatcher
     *                 and taking the result back
     * @return the result, wrapped in an {@link Optional}
     */
    public static <R> Optional<R> dispatchEventWithResultSafely(Function<EventDispatcher, R> function) {
        if (ComponentViewer.instance != null) {
            return Optional.ofNullable(function.apply(ComponentViewer.instance.eventDispatcher));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get this mod's instance.
     *
     * @return the mod's instance
     */
    public static ComponentViewer getInstance() {
        if (ComponentViewer.instance == null) {
            throw new IllegalStateException(String.format(
                "'%s' hasn't been instantiated yet!",
                ComponentViewer.class.getName()
            ));
        }

        return ComponentViewer.instance;
    }

    private static void setInstance(ComponentViewer instance) {
        if (ComponentViewer.instance != null) {
            throw new IllegalStateException(String.format(
                "Cannot instantiate '%s' twice!",
                ComponentViewer.class.getName()
            ));
        }

        ComponentViewer.instance = instance;
    }

    protected static Minecraft getMinecraftClient() {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (minecraftClient == null) {
            throw new IllegalStateException(
                "Minecraft hasn't been initialized yet, although it should!"
            );
        }

        return minecraftClient;
    }

    private boolean currentlyInSurvival() {
        LocalPlayer player = ComponentViewer.getMinecraftClient().player;

        if (player == null) {
            return false;
        }

        GameType gameMode = player.gameMode();
        return gameMode != null && gameMode.isSurvival();
    }

    private boolean currentlyOnServer() {
        Minecraft minecraftClient = ComponentViewer.getMinecraftClient();
        IntegratedServer integratedServer = minecraftClient.getSingleplayerServer();

        return (
            minecraftClient.getCurrentServer() != null
            || integratedServer != null && integratedServer.isPublished()
        );
    }
}
