package lumberwizard.anvilpatch;

import lumberwizard.anvilpatch.common.CommonProxy;
import lumberwizard.anvilpatch.network.ModNetworkHandler;
import lumberwizard.anvilpatch.network.PacketConfigSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = AnvilPatch.MODID, name = AnvilPatch.NAME, version = AnvilPatch.VERSION)
@Mod.EventBusSubscriber(modid= AnvilPatch.MODID)
public class AnvilPatch
{
    public static final String MODID = "anvilpatch";
    public static final String NAME = "Anvil Patch - Lawful";
    public static final String VERSION = "0.2.2";

    @SidedProxy(clientSide = "lumberwizard.anvilpatch.client.ClientProxy", serverSide = "lumberwizard.anvilpatch.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static AnvilPatch instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if (Loader.isModLoaded("anvilfix")) {
            proxy.throwAnvilFixException();
        }
        ModNetworkHandler.registerPacketHandlers();
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(AnvilPatch.MODID)) {
            ConfigManager.sync(AnvilPatch.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void onPlayerConnected(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
            ModNetworkHandler.INSTANCE.sendTo(new PacketConfigSync(ModConfig.levelCap, ModConfig.costIncreaseSetting), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDisconnected(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.world.isRemote) {
            ModConfig.valuesOverriden = false;
        }
    }

}
