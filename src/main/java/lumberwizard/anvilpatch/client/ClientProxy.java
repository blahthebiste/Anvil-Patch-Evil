package lumberwizard.anvilpatch.client;

import lumberwizard.anvilpatch.common.CommonProxy;
import net.minecraftforge.fml.common.LoaderException;

public class ClientProxy extends CommonProxy {

    @Override
    public void throwAnvilFixException() {
        throw new AnvilFixOnScreenException(new LoaderException("Anvil Patch - Lawful is incompatible with AnvilFix! Remove one of the mods."));
    }

}
