package com.lumberjacksparrow.anvilpatchevil.mixin;

import net.minecraft.inventory.ContainerRepair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.GuiRepair.class)
public abstract class GuiRepairMixin {
    @Shadow
    private @Final
    ContainerRepair anvil;

    @Inject(method = "drawGuiContainerForegroundLayer", at = @At("HEAD"))
    private void onDrawGuiContainerForegroundLayer(CallbackInfo ci) {
//        System.out.println("GuiRepairMixin removing cost");
        anvil.maximumCost = 0;
    }
}