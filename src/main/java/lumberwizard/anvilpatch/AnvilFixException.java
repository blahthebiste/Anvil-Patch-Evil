package lumberwizard.anvilpatch;

import net.minecraftforge.fml.common.LoaderException;

public class AnvilFixException extends LoaderException {

    public AnvilFixException() {
        super("Anvil Patch - Lawful is incompatible with AnvilFix! Remove one of the mods.");
    }

}
