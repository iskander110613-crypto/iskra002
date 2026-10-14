package com.example.flytoggle.client;

import com.example.flytoggle.FlyToggle;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class FlyToggleClient implements ClientModInitializer {
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(FlyToggle.MOD_ID, "main"));

    public static final KeyMapping TOGGLE_FLIGHT = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.flytoggle.toggle_flight",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_KP_ADD,
                    CATEGORY
            ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_FLIGHT.consumeClick()) {
                if (client.player == null) {
                    return;
                }
                ClientPlayNetworking.send(FlyToggle.ToggleFlightPayload.INSTANCE);
            }
        });
    }
}
