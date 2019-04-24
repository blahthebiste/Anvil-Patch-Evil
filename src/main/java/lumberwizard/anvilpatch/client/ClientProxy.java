package lumberwizard.anvilpatch.client;

import lumberwizard.anvilpatch.common.AnvilFixException;
import lumberwizard.anvilpatch.common.CommonProxy;

public class ClientProxy extends CommonProxy {

    @Override
    public void throwAnvilFixException() {
        throw new AnvilFixOnScreenException(new AnvilFixException());
    }

}
