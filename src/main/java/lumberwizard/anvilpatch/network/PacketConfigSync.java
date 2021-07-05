package lumberwizard.anvilpatch.network;

import io.netty.buffer.ByteBuf;
import lumberwizard.anvilpatch.AnvilPatch;
import lumberwizard.anvilpatch.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConfigSync implements IMessage {

    public PacketConfigSync() {}

    private int levelCap;
    private ModConfig.EnumCostIncreaseSetting costIncreaseSetting;
    private boolean breakEnchantLevelCap;
    private float breakChance;
    public PacketConfigSync(int levelCap, ModConfig.EnumCostIncreaseSetting costIncreaseSetting, boolean breakEnchantLevelCap, float breakChance) {
        this.levelCap = levelCap;
        this.costIncreaseSetting = costIncreaseSetting;
        this.breakEnchantLevelCap = breakEnchantLevelCap;
        this.breakChance = breakChance;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        levelCap = buf.readInt();
        costIncreaseSetting = ModConfig.EnumCostIncreaseSetting.values()[buf.readInt()];
        breakEnchantLevelCap = buf.readBoolean();
        breakChance = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(levelCap);
        buf.writeInt(costIncreaseSetting.ordinal());
        buf.writeBoolean(breakEnchantLevelCap);
        buf.writeFloat(breakChance);
    }

    public static class Handler implements IMessageHandler<PacketConfigSync, IMessage> {
        @Override
        public IMessage onMessage(PacketConfigSync message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                ModConfig.setValuesOverridden(true);
                ModConfig.setSyncedValues(message.levelCap, message.costIncreaseSetting, message.breakEnchantLevelCap, message.breakChance);
                AnvilPatch.logger.info("Synced configs from server");
            });
            return null;
        }
    }
}
