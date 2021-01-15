package lumberwizard.anvilpatch;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;

@Config(modid = AnvilPatch.MODID)
public class ModConfig {

    public static void setSyncedValues(int levelCap, EnumCostIncreaseSetting costIncreaseSetting, float breakChance){
        syncedLevelCap = levelCap;
        syncedCostIncreaseSetting = costIncreaseSetting;
        syncedBreakChance = breakChance;
    }

    @Config.Ignore
    private static boolean valuesOverridden = false;

    @Config.Comment({"Set to -1 to remove the cap.", "This setting will be ignored if Apotheosis is installed."})
    @Config.Name("New level cap")
    @Config.RangeInt(min = -1)
    public static int levelCap = -1;

    @Config.Ignore
    private static int syncedLevelCap = levelCap;

    public static int getLevelCap() {
        if (Loader.isModLoaded("apotheosis")) {
            return -1;
        }
        if (areValuesOverridden()) {
            return syncedLevelCap;
        }
        return levelCap;
    }

    @Config.Comment({
            "Valid values:",
            "KEEP - keeps the cumulative repair cost, same as vanilla.",
            "REMOVE_REPAIR_SCALING - repairs won't increase the xp cost, but enchantments will, even for repaired items.",
            "ENCHANTMENT_ONLY - repairs will always cost the same, but applying more enchantments will cost more.",
            "REMOVE - removes the cumulative repair cost entirely."
    })
    @Config.Name("XP cost increase")
    public static EnumCostIncreaseSetting costIncreaseSetting = EnumCostIncreaseSetting.REMOVE_REPAIR_SCALING;

    @Config.Ignore
    private static EnumCostIncreaseSetting syncedCostIncreaseSetting;

    public static EnumCostIncreaseSetting getCostIncreaseSetting(){
        if (areValuesOverridden()) {
            return syncedCostIncreaseSetting;
        }
        return costIncreaseSetting;
    }

    public static boolean areValuesOverridden() {
        return valuesOverridden;
    }

    public static void setValuesOverridden(boolean valuesOverridden) {
        ModConfig.valuesOverridden = valuesOverridden;
    }

    public enum EnumCostIncreaseSetting {
        KEEP,
        REMOVE_REPAIR_SCALING,
        ENCHANTMENT_ONLY,
        REMOVE
    }

    @Config.Comment({"Chance for the anvil to reach the next stage of breakage per use.",
            "Choose a value from 0 to 1. Vanilla default is 0.12 (= 12%)."})
    @Config.Name("Anvil break chance")
    @Config.RangeDouble(min = 0, max = 1)
    public static float breakChance = 0.12F;

    @Config.Ignore
    private static float syncedBreakChance = breakChance;

    public static float getBreakChance() {
        if (areValuesOverridden()) {
            return syncedBreakChance;
        }
        return breakChance;
    }

}
