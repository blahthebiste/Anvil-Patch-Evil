package lumberwizard.anvilpatch;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;

@Config(modid = AnvilPatch.MODID)
public class ModConfig {

    public static void setSyncedValues(int levelCap, EnumCostIncreaseSetting costIncreaseSetting, boolean breakEnchantLevelCap, float breakChance){
        syncedLevelCap = levelCap;
        syncedCostIncreaseSetting = costIncreaseSetting;
        syncedBreakEnchantLevelCap = breakEnchantLevelCap;
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

    @Config.Comment({
            "Vanilla behavior, when merging two items where the one on the right has an enchantment with a level beyond its max",
            "(for example power 8) is to reset the level to the max (so the resulting item would have power 5)",
            "This setting disables that.",
            "It does not, however, allow creating enchantments beyond the cap",
            "So, for example, merging two power V books would still result in power V"
    })
    @Config.Name(("Allow repairs beyond enchantment level cap."))
    public static boolean breakEnchantLevelCap = false;

    @Config.Ignore
    private static boolean syncedBreakEnchantLevelCap;

    public static boolean shouldBreakEnchantLevelCap() {
        if (areValuesOverridden()) {
            return syncedBreakEnchantLevelCap;
        }
        return breakEnchantLevelCap;
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

}
