package dev.emi.emi.network;

import dev.emi.emi.runtime.EmiLog;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.screen.ScreenHandler;

public class CreateItemC2SPacket implements EmiPacket {
	private final int mode;
	private final ItemStack stack;

	public CreateItemC2SPacket(int mode, ItemStack stack) {
		this.mode = mode;
		this.stack = stack;
	}

	public CreateItemC2SPacket(RegistryByteBuf buf) {
		this(buf.readByte(), ItemStack.OPTIONAL_PACKET_CODEC.decode(buf));
	}

	@Override
	public void write(RegistryByteBuf buf) {
		buf.writeByte(mode);
		ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, stack);
	}

	@Override
	public void apply(PlayerEntity player) {
		ScreenHandler handler = player.currentScreenHandler;
		if (handler != null) {
			if (stack.isEmpty()) {
				if (mode == 1 && !handler.getCursorStack().isEmpty()) {
					EmiLog.info(player.getName().getString() + " 删除了物品 " + handler.getCursorStack());
					handler.setCursorStack(ItemStack.EMPTY);
				}
			} else if (stack.getTranslationKey().contains("yuushya")) { // 检查名称中是否包含 "yuushya"
				EmiLog.info(player.getName().getString() + " 作弊获取 " + stack);
				if (mode == 0) {
					player.getInventory().offerOrDrop(stack.copy());
				} else if (mode == 1) {
					handler.setCursorStack(stack.copy());
				}
			}
		}
	}

	@Override
	public Id<CreateItemC2SPacket> getId() {
		return EmiNetwork.CREATE_ITEM;
	}
}
