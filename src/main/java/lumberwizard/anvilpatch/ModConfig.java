package lumberwizard.anvilpatch;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;

@Config(modid = AnvilPatch.MODID)
public class ModConfig {

    @Config.Ignore
    public static boolean valuesOverriden = false;

    @Config.Comment({"Set to -1 to remove the cap.", "This setting will be ignored if Apotheosis is installed."})
    @Config.Name("New level cap")
    @Config.RangeInt(min = -1)
    public static int levelCap = -1;

    @Config.Ignore
    public static int syncedLevelCap = levelCap;

    public static int getLevelCap() {
        if (Loader.isModLoaded("apotheosis")) {
            return -1;
        }
        if (valuesOverriden) {
            return syncedLevelCap;
        }
        return levelCap;
    }

    @Config.Comment({
            "Valid values:",
            "KEEP - keeps the cumulative repair cost, same as vanilla",
            "REMOVE_REPAIR_SCALING - repairs won't increase the xp cost, but enchantments will, even for repaired items",
            "ENCHANTMENT - repairs will always cost the same, but applying more enchantments will cost more",
            "REMOVE - removes the cumulative repair cost entirely"
    })
    @Config.Name("XP cost increase")
    public static EnumCostIncreaseSetting costIncreaseSetting = EnumCostIncreaseSetting.REMOVE_REPAIR_SCALING;

    @Config.Ignore
    public static EnumCostIncreaseSetting syncedCostIncreaseSetting;

    public static EnumCostIncreaseSetting getCostIncreaseSetting(){
        if (valuesOverriden) {
            return syncedCostIncreaseSetting;
        }
        return costIncreaseSetting;
    }

    public enum EnumCostIncreaseSetting {
        KEEP,
        REMOVE_REPAIR_SCALING,
        ENCHANTMENT_ONLY,
        REMOVE
    }

}
