package lumberwizard.anvilpatch.common;

import net.minecraftforge.fml.common.LoaderException;

public class CommonProxy {

    public void throwAnvilFixException(){
        throw new LoaderException("Anvil Patch - Lawful is incompatible with AnvilFix! Remove one of the mods.");
    }

}
