package com.lumberjacksparrow.anvilpatchevil;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AnvilPatchEvil.MOD_ID)
public class AnvilPatchEvil {
    public static final String MOD_ID = "anvilpatchevil";

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        System.out.println("Anvil Patch Evil initializing");
    }
}
