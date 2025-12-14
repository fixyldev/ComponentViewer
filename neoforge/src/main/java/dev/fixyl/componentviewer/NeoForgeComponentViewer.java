package dev.fixyl.componentviewer;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import dev.fixyl.componentviewer.control.ControlFlow;
import dev.fixyl.componentviewer.control.keyboard.Keyboard;
import dev.fixyl.componentviewer.control.keyboard.NeoForgeKeyboard;
import dev.fixyl.componentviewer.event.KeyboardEvents;
import dev.fixyl.componentviewer.event.MixinEvents;
import dev.fixyl.componentviewer.event.NeoForgeEventDispatcher;
import dev.fixyl.componentviewer.screen.MainConfigScreen;

/**
 * This mod's entry point and singleton for the NeoForge platform.
 * <p>
 * Handles initialization logic specific to NeoForge.
 *
 * @see ComponentViewer
 */
@Mod(value = ComponentViewer.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ComponentViewer.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeComponentViewer extends ComponentViewer {

    public NeoForgeComponentViewer(ModContainer modContainer) {
        super(new NeoForgeEventDispatcher(), FMLPaths.CONFIGDIR.get());

        modContainer.registerExtensionPoint(
            IConfigScreenFactory.class,
            (container, lastScreen) -> new MainConfigScreen(lastScreen, this.configs)
        );
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent clientSetupEvent) {
        Minecraft minecraftClient = ComponentViewer.getMinecraftClient();
        ComponentViewer instance = ComponentViewer.getInstance();

        instance.configs.loadFromDisk();

        ControlFlow controlFlow = new ControlFlow(minecraftClient, instance, instance.configs);
        Keyboard keyboard = new NeoForgeKeyboard(minecraftClient, instance, instance.eventDispatcher, instance.configs);

        NeoForge.EVENT_BUS.addListener(MixinEvents.StartRenderEvent.class, event -> controlFlow.onStartRender());
        NeoForge.EVENT_BUS.addListener(MixinEvents.TooltipEvent.class, event -> controlFlow.onTooltip(event.itemStack, event.tooltip));
        NeoForge.EVENT_BUS.addListener(MixinEvents.BundleTooltipImageEvent.class, event -> event.setResult(controlFlow.onBundleTooltipImage()));
        NeoForge.EVENT_BUS.addListener(MixinEvents.MouseScrollEvent.class, event -> event.setResult(controlFlow.onMouseScroll(event.yOffset)));
        NeoForge.EVENT_BUS.addListener(KeyboardEvents.CycleComponentEvent.class, event -> controlFlow.onCycleComponent(event.cycleType));
        NeoForge.EVENT_BUS.addListener(KeyboardEvents.CopyActionEvent.class, event -> controlFlow.onCopyAction());

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> keyboard.onEndClientTick());
        NeoForge.EVENT_BUS.addListener(MixinEvents.KeyInputEvent.class, event -> keyboard.onKeyInput(event.keyEvent, event.action));
        NeoForge.EVENT_BUS.addListener(MixinEvents.ButtonInputEvent.class, event -> keyboard.onButtonInput(event.mouseButtonInfo, event.action));
        NeoForge.EVENT_BUS.addListener(MixinEvents.ClearToastManagerEvent.class, event -> keyboard.clearAllOptionCycleToasts());
    }
}
