package lumberwizard.anvilfix;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Mod(modid = AnvilFix.MODID, name = AnvilFix.NAME, version = AnvilFix.VERSION)
@Mod.EventBusSubscriber(modid=AnvilFix.MODID)
public class AnvilFix
{
    public static final String MODID = "anvilfix";
    public static final String NAME = "AnvilFix - Lawful";
    public static final String VERSION = "0.1.1";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void anvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        ItemStack outputItem = left.copy();
        int addedRepairCost = 0;
        Map<Enchantment, Integer> outputItemEnchantments = EnchantmentHelper.getEnchantments(outputItem);

        boolean isRightItemEnchantedBook = right.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(right).isEmpty();

        if (outputItem.isItemStackDamageable() && outputItem.getItem().getIsRepairable(left, right))
        {
            int amountRepairedByMat = Math.min(outputItem.getItemDamage(), outputItem.getMaxDamage() / 4);

            if (amountRepairedByMat <= 0)
            {
                return;
            }

            int materialCost;

            for (materialCost = 0; amountRepairedByMat > 0 && materialCost < right.getCount(); ++materialCost)
            {
                int newDamage = outputItem.getItemDamage() - amountRepairedByMat;
                outputItem.setItemDamage(newDamage);
                ++addedRepairCost;
                amountRepairedByMat = Math.min(outputItem.getItemDamage(), outputItem.getMaxDamage() / 4);
            }

            event.setMaterialCost(materialCost);
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
                            addedRepairCost = 40;
                        }
                    }
                }
            }

            if (rightItemHasIncompatibleEnchantments && !rightItemHasCompatibleEnchantments)
            {
                return;
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

            if (addedRepairCost <= 0)
            {
                outputItem = ItemStack.EMPTY;
            }

            if (!outputItem.isEmpty()) {
                EnchantmentHelper.setEnchantments(outputItemEnchantments, outputItem);
                event.setCost(addedRepairCost);
                event.setOutput(outputItem);
            }
        }
    }

}
