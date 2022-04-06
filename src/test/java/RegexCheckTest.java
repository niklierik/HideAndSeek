import org.intellij.lang.annotations.RegExp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class RegexCheckTest {

    @Test
    public void test() {
        @RegExp
        var pattern = "[A-Za-z][A-Za-z0-9]*";
        Assertions.assertTrue(Pattern.matches(pattern, "h"));
        Assertions.assertTrue(Pattern.matches(pattern, "H"));
        Assertions.assertTrue(Pattern.matches(pattern, "hh"));
        Assertions.assertTrue(Pattern.matches(pattern, "h1"));
        Assertions.assertFalse(Pattern.matches(pattern, "?"));
        Assertions.assertFalse(Pattern.matches(pattern, "1"));
    }

}
