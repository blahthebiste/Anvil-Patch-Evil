package com.lumberjacksparrow.anvilpatchevil.mixin;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

@Mixin(net.minecraft.inventory.ContainerRepair.class)
public abstract class ContainerRepairMixin extends Container {
	@Shadow
	public int maximumCost;
	@Shadow
	private @Final World world;
	@Shadow
	private @Final BlockPos pos;
	@Shadow
	public int materialCost;
	@Shadow
	private @Final IInventory inputSlots;
	@Shadow
	private @Final IInventory outputSlot;

	/**
	 * @author Lumberjacksparrow
	 */
	@Override
	protected Slot addSlotToContainer(Slot slotIn)
	{
//		System.out.println("ContainerRepair adding slot...");
		if(slotIn.getSlotIndex() == 2 && slotIn.xPos == 134 && slotIn.yPos == 47) {
//			System.out.println("Matched output slot!");
			return super.addSlotToContainer(new Slot(ContainerRepairMixin.this.outputSlot, 2, 134, 47)
			{
				public boolean isItemValid(ItemStack stack)
				{
//					System.out.println("mixin isItemValid");
					return false;
				}
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					// Remove code that prevents taking the item when its XP cost is 0:
//					System.out.println("Mixin canTakeStack = "+((playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= maximumCost) && this.getHasStack()));
					return this.getHasStack();
				}
				public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
				{
//					System.out.println("mixin onTake");
					// Remove code that subtracts player XP when they use the anvil:
//					if (!thePlayer.capabilities.isCreativeMode)
//					{
//						thePlayer.addExperienceLevel(-maximumCost);
//					}

					float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(thePlayer, stack, inputSlots.getStackInSlot(0), inputSlots.getStackInSlot(1));

					inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);

					if (materialCost > 0)
					{
						ItemStack itemstack = inputSlots.getStackInSlot(1);

						if (!itemstack.isEmpty() && itemstack.getCount() > materialCost)
						{
							itemstack.shrink(materialCost);
							inputSlots.setInventorySlotContents(1, itemstack);
						}
						else
						{
							inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
						}
					}
					else
					{
						inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
					}

					maximumCost = 0;
					IBlockState iblockstate = world.getBlockState(pos);

					if (!thePlayer.capabilities.isCreativeMode && !world.isRemote && iblockstate.getBlock() == Blocks.ANVIL && thePlayer.getRNG().nextFloat() < breakChance)
					{
						int l = iblockstate.getValue(BlockAnvil.DAMAGE);
						++l;

						if (l > 2)
						{
							world.setBlockToAir(pos);
							world.playEvent(1029, pos, 0);
						}
						else
						{
							world.setBlockState(pos, iblockstate.withProperty(BlockAnvil.DAMAGE, l), 2);
							world.playEvent(1030, pos, 0);
						}
					}
					else if (!world.isRemote)
					{
						world.playEvent(1030, pos, 0);
					}

					return stack;
				}
			});
		}
		return super.addSlotToContainer(slotIn);
	}
}