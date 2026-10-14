package com.example.flytoggle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class FlyToggle implements ModInitializer {
    public static final String MOD_ID = "flytoggle";

    public record ToggleFlightPayload() implements CustomPacketPayload {
        public static final ToggleFlightPayload INSTANCE = new ToggleFlightPayload();
        public static final CustomPacketPayload.Type<ToggleFlightPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "toggle_flight"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ToggleFlightPayload> CODEC =
                StreamCodec.unit(INSTANCE);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay()
                .register(ToggleFlightPayload.TYPE, ToggleFlightPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ToggleFlightPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            var abilities = player.getAbilities();
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                abilities.setFlying(true);
                player.displayClientMessage(Component.translatable("message.flytoggle.on"), true);
            } else if (abilities.flying) {
                abilities.setFlying(false);
                player.displayClientMessage(Component.translatable("message.flytoggle.off"), true);
            } else {
                abilities.mayfly = false;
                player.displayClientMessage(Component.translatable("message.flytoggle.off"), true);
            }
            player.sendAbilities();
        });
    }
}
