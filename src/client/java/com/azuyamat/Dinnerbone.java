package com.azuyamat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import java.util.Objects;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class Dinnerbone implements ClientModInitializer {

	String text = "";
	String loc = "";

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("coordinates")
				.executes(context -> {
					ClientPlayerEntity p =  context.getSource().getPlayer();
					p.sendMessage(Text.literal(text));
					return 1;
				})));
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("setdestination")
				.then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
						.then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
								.then(ClientCommandManager.argument("z", IntegerArgumentType.integer())
										.executes(ctx -> {
											loc = ctx.getArgument("x", String.class)+"|"+ctx.getArgument("y", String.class)+"|"+ctx.getArgument("z", String.class);
											ctx.getSource().getPlayer().sendMessage(Text.literal("Set destination"));
											return 1;
										})
								)
		))));

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			ClientPlayerEntity p = client.player;
			if (p != null) {
				double x = (double) Math.round(p.getX() * 100) / 100;
				double y = (double) Math.round(p.getY() * 100) / 100;
				double z = (double) Math.round(p.getZ() * 100) / 100;
				text = "Pos: ("+x+", "+y+", "+z+")";
			}
		});

		HudRenderCallback.EVENT.register(((matrixStack, tickDelta) -> {
			renderText(matrixStack);
		}));
	}

	private void renderText(MatrixStack stack){
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer renderer = client.textRenderer;



		int x = client.getWindow().getWidth()/8;
		int y = 10;
		int color = 0x00FF00;

		DrawableHelper.drawCenteredTextWithShadow(stack, renderer, text, x, y, color);
		if (!Objects.equals(loc, "")){
			DrawableHelper.drawCenteredTextWithShadow(stack, renderer, "Dest: "+loc.replace("|", ", "), x, y+10, 0xFFA500);
		}
	}
}