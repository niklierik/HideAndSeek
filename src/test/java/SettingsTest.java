import me.fiveship.hideandseek.HNS;
import me.fiveship.hideandseek.game.Settings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SettingsTest {

    @Test
    public void test() {
        try {
            Settings global = new Settings();
            Settings overriding = new Settings();
            overriding.canChangeBlock = null;
            overriding.canSelectBlock = null;
            overriding.hideTime = 3;
            overriding.keepNotNull(global);
            Assertions.assertEquals(global.canChangeBlock, overriding.canChangeBlock);
            Assertions.assertEquals(global.canSelectBlock, overriding.canSelectBlock);
            Assertions.assertEquals(3, overriding.hideTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
