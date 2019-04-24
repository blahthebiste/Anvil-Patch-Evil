package lumberwizard.anvilpatch;

import lumberwizard.anvilpatch.common.CommonProxy;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Mod(modid = AnvilPatch.MODID, name = AnvilPatch.NAME, version = AnvilPatch.VERSION)
@Mod.EventBusSubscriber(modid= AnvilPatch.MODID)
public class AnvilPatch
{
    public static final String MODID = "anvilpatch";
    public static final String NAME = "Anvil Patch - Lawful";
    public static final String VERSION = "0.2";

    @SidedProxy(clientSide = "lumberwizard.anvilpatch.client.ClientProxy", serverSide = "lumberwizard.anvilpatch.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static AnvilPatch instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if (Loader.isModLoaded("anvilfix")) {
            proxy.throwAnvilFixException();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void anvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        ItemStack outputItem = left.copy();
        boolean shouldIncreaseCost = ModConfig.costIncreaseSetting == ModConfig.EnumCostIncreaseSetting.KEEP;
        int addedRepairCost = 0;
        Map<Enchantment, Integer> outputItemEnchantments = EnchantmentHelper.getEnchantments(outputItem);

        boolean isRightItemEnchantedBook = right.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(right).isEmpty();

        int materialCost = 1;
        if (outputItem.isItemStackDamageable() && outputItem.getItem().getIsRepairable(left, right))
        {
            int amountRepairedByMat = Math.min(outputItem.getItemDamage(), outputItem.getMaxDamage() / 4);

            if (amountRepairedByMat <= 0)
            {
                return;
            }


            for (materialCost = 0; amountRepairedByMat > 0 && materialCost < right.getCount(); ++materialCost)
            {
                int newDamage = outputItem.getItemDamage() - amountRepairedByMat;
                outputItem.setItemDamage(newDamage);
                ++addedRepairCost;
                amountRepairedByMat = Math.min(outputItem.getItemDamage(), outputItem.getMaxDamage() / 4);
            }

        }
        else
        {
            if (!isRightItemEnchantedBook && (outputItem.getItem() != right.getItem() || !outputItem.isItemStackDamageable()))
            {
                return;
            }

            if (outputItem.isItemStackDamageable() && !isRightItemEnchantedBook)
            {
                int leftDurability = left.getMaxDamage() - left.getItemDamage();
                int rightDurability = right.getMaxDamage() - right.getItemDamage();
                int newDurability = leftDurability + rightDurability + outputItem.getMaxDamage() * 12 / 100;
                int newDamage = outputItem.getMaxDamage() - newDurability;

                if (newDamage < 0)
                {
                    newDamage = 0;
                }

                if (newDamage < outputItem.getItemDamage()) // vanilla uses metadata here instead of damage.
                {
                    outputItem.setItemDamage(newDamage);
                    addedRepairCost += 2;
                }
            }

            Map<Enchantment, Integer> enchantmentsToApply = EnchantmentHelper.getEnchantments(right);
            boolean rightItemHasCompatibleEnchantments = false;
            boolean rightItemHasIncompatibleEnchantments = false;

            for (Enchantment enchantmentToAdd : enchantmentsToApply.keySet())
            {
                if (enchantmentToAdd != null)
                {
                    int currentEnchantmentLevel = outputItemEnchantments.containsKey(enchantmentToAdd) ? (outputItemEnchantments.get(enchantmentToAdd)).intValue() : 0;
                    int enchantmentNewLevel = (enchantmentsToApply.get(enchantmentToAdd)).intValue();
                    enchantmentNewLevel = currentEnchantmentLevel == enchantmentNewLevel ? enchantmentNewLevel + 1 : Math.max(enchantmentNewLevel, currentEnchantmentLevel);
                    boolean canEnchantmentBeAppliedToLeftItem = enchantmentToAdd.canApply(left);

                    if (left.getItem() == Items.ENCHANTED_BOOK) // supposed to also apply to players in creative mode but I guess it's not possible
                    {
                        canEnchantmentBeAppliedToLeftItem = true;
                    }

                    for (Enchantment enchantment : outputItemEnchantments.keySet())
                    {
                        if (enchantment != enchantmentToAdd && !enchantmentToAdd.isCompatibleWith(enchantment))
                        {
                            canEnchantmentBeAppliedToLeftItem = false;
                            ++addedRepairCost;
                        }
                    }

                    if (!canEnchantmentBeAppliedToLeftItem)
                    {
                        rightItemHasIncompatibleEnchantments = true;
                    }
                    else
                    {
                        rightItemHasCompatibleEnchantments = true;

                        if (enchantmentNewLevel > enchantmentToAdd.getMaxLevel())
                        {
                            enchantmentNewLevel = enchantmentToAdd.getMaxLevel();
                        }

                        outputItemEnchantments.put(enchantmentToAdd, Integer.valueOf(enchantmentNewLevel));
                        int repairCostAddedByEnchantmentRarity = 0;

                        switch (enchantmentToAdd.getRarity())
                        {
                            case COMMON:
                                repairCostAddedByEnchantmentRarity = 1;
                                break;
                            case UNCOMMON:
                                repairCostAddedByEnchantmentRarity = 2;
                                break;
                            case RARE:
                                repairCostAddedByEnchantmentRarity = 4;
                                break;
                            case VERY_RARE:
                                repairCostAddedByEnchantmentRarity = 8;
                        }

                        if (isRightItemEnchantedBook)
                        {
                            repairCostAddedByEnchantmentRarity = Math.max(1, repairCostAddedByEnchantmentRarity / 2);
                        }

                        addedRepairCost += repairCostAddedByEnchantmentRarity * enchantmentNewLevel;

                        if (left.getCount() > 1)
                        {
                            return;
                        }
                    }
                }
            }

            if (rightItemHasIncompatibleEnchantments && !rightItemHasCompatibleEnchantments)
            {
                return;
            }

            if (rightItemHasCompatibleEnchantments && ModConfig.costIncreaseSetting == ModConfig.EnumCostIncreaseSetting.ENCHANTMENT) {
                shouldIncreaseCost = true;
            }

            int renameAddedCost = 0;

            String repairedItemName = event.getName();

            if (StringUtils.isBlank(repairedItemName))
            {
                if (left.hasDisplayName())
                {
                    renameAddedCost = 1;
                    addedRepairCost += renameAddedCost;
                    outputItem.clearCustomName();
                }
            }


            else if (!repairedItemName.equals(left.getDisplayName()))
            {
                renameAddedCost = 1;
                addedRepairCost += renameAddedCost;
                outputItem.setStackDisplayName(repairedItemName);
            }
            if (isRightItemEnchantedBook && !outputItem.getItem().isBookEnchantable(outputItem, right)) outputItem = ItemStack.EMPTY;

            int totalRepairCost = (shouldIncreaseCost ? event.getCost() : 0) + addedRepairCost;

            if (totalRepairCost <= 0)
            {
                outputItem = ItemStack.EMPTY;
            }

            if (addedRepairCost == renameAddedCost && ModConfig.levelCap >= 0 && totalRepairCost >= ModConfig.levelCap) {
                totalRepairCost = ModConfig.levelCap - 1;
            }

            if (ModConfig.levelCap >= 0 && totalRepairCost >= ModConfig.levelCap)
            {
                outputItem = ItemStack.EMPTY;
            }

            if (!outputItem.isEmpty()) {
                if (shouldIncreaseCost) {
                    int newCost = outputItem.getRepairCost();
                    if (!right.isEmpty() && newCost < right.getRepairCost()) {
                        newCost = right.getRepairCost();
                    }
                    if (renameAddedCost != addedRepairCost || renameAddedCost == 0) {
                        newCost = newCost * 2 + 1;
                    }
                    outputItem.setRepairCost(newCost);
                }
                EnchantmentHelper.setEnchantments(outputItemEnchantments, outputItem);
                if (outputItem.isItemStackDamageable() && outputItem.getItem().getIsRepairable(left, right)) {
                    event.setMaterialCost(materialCost);
                }
                event.setCost(addedRepairCost);
                event.setOutput(outputItem);
            }
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(AnvilPatch.MODID)) {
            ConfigManager.sync(AnvilPatch.MODID, Config.Type.INSTANCE);
        }
    }

}
