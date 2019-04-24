package lumberwizard.anvilpatch.network;

import io.netty.buffer.ByteBuf;
import lumberwizard.anvilpatch.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSync implements IMessage {

    public PacketConfigSync(){}

    private int levelCap;
    private ModConfig.EnumCostIncreaseSetting costIncreaseSetting;
    public PacketConfigSync(int levelCap, ModConfig.EnumCostIncreaseSetting costIncreaseSetting) {
        this.levelCap = levelCap;
        this.costIncreaseSetting = costIncreaseSetting;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        levelCap = buf.readInt();
        costIncreaseSetting = ModConfig.EnumCostIncreaseSetting.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(levelCap);
        buf.writeInt(costIncreaseSetting.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {
        @Override
        public IMessage onMessage(PacketConfigSync message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ModConfig.valuesOverriden = true;
                ModConfig.syncedCostIncreaseSetting = message.costIncreaseSetting;
                ModConfig.syncedLevelCap = message.levelCap;
            });
            return null;
        }
    }
}
