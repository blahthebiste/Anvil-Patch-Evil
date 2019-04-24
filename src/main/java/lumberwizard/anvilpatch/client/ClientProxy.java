package lumberwizard.anvilpatch.client;

import lumberwizard.anvilpatch.AnvilFixException;
import lumberwizard.anvilpatch.CommonProxy;

public class ClientProxy extends CommonProxy {

    @Override
    public void throwAnvilFixException() {
        throw new AnvilFixOnScreenException(new AnvilFixException());
    }

}
