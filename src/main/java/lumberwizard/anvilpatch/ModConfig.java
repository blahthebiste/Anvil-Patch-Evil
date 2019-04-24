package lumberwizard.anvilpatch;

import net.minecraftforge.common.config.Config;

@Config(modid = AnvilPatch.MODID, name = "Anvil Patch")
public class ModConfig {

    @Config.Comment("Set to -1 to remove the cap.")
    @Config.Name("New level cap")
    @Config.RangeInt(min = -1)
    public static int levelCap = -1;

    @Config.Comment("Set to keep in order to keep the cumulative repair cost, enchantment to make it only apply to transferring enchantments and not to repairing, and remove in order to remove it entirely.")
    @Config.Name("XP cost increase")
    public static EnumCostIncreaseSetting costIncreaseSetting = EnumCostIncreaseSetting.ENCHANTMENT;

    public enum EnumCostIncreaseSetting {
        KEEP,
        ENCHANTMENT,
        REMOVE
    }

}
