package lumberwizard.anvilpatch.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class AnvilFixOnScreenException extends CustomModLoadingErrorDisplayException {

    public AnvilFixOnScreenException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        if (errorScreen == null || fontRenderer == null) {
            return;
        }
        errorScreen.drawCenteredString(fontRenderer, "Anvil Patch - Lawful is incompatible with AnvilFix!", errorScreen.width / 2, errorScreen.height / 2 - 20, 0xFFFFFF);
        errorScreen.drawCenteredString(fontRenderer, "Please remove one of the mods.", errorScreen.width / 2, errorScreen.height / 2 - 10, 0xFFFFFF);
    }
}
