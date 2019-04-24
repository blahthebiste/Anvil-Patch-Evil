package lumberwizard.anvilpatch.client;

import lumberwizard.anvilpatch.common.CommonProxy;
import net.minecraftforge.fml.common.LoaderExceptionModCrash;

public class ClientProxy extends CommonProxy {

    @Override
    public void throwAnvilFixException() {
        throw new AnvilFixOnScreenException(new LoaderExceptionModCrash("Anvil Patch - Lawful is incompatible with AnvilFix! Remove one of the mods.", new RuntimeException("Error while loading Anvil Patch, please see message above.")));
    }

}
