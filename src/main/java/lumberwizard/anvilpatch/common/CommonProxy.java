package lumberwizard.anvilpatch.common;

import net.minecraftforge.fml.common.LoaderExceptionModCrash;

public class CommonProxy {

    public void throwAnvilFixException(){
        throw new LoaderExceptionModCrash("Anvil Patch - Lawful is incompatible with AnvilFix! Remove one of the mods.", new RuntimeException("Error while loading Anvil Patch, please see message above."));
    }

}
