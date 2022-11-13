package tools.micasa.com;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UtilsTest {

    @Test
    @Tag("find")
    @DisplayName("finds the variable name")
    void oneVariable() {
        var input = "ab{{n1}}cd";
        var result = Utils.variables(input);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().size(), is(1));
        assertThat(result.get().get(0), is("n1"));
    }

    @Test
    @Tag("find")
    @DisplayName("finds the variables names")
    void twoVariable() {
        var input = "ab{{n1}}cd{{n11}}ef";
        var result = Utils.variables(input);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().size(), is(2));
        assertThat(result.get().get(0), is("n1"));
        assertThat(result.get().get(1), is("n11"));
    }

    @Test
    @Tag("find")
    @DisplayName("finds the variables names at the start and end of the string")
    void edges() {
        var input = "{{n1}}cd{{n11}}";
        var result = Utils.variables(input);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().size(), is(2));
        assertThat(result.get().get(0), is("n1"));
        assertThat(result.get().get(1), is("n11"));
    }

    @Test
    @Tag("find")
    @DisplayName("finds the variables names without any characters between them")
    void noChars() {
        var input = "{{n1}}{{n11}}";
        var result = Utils.variables(input);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().size(), is(2));
        assertThat(result.get().get(0), is("n1"));
        assertThat(result.get().get(1), is("n11"));
    }

    @Test
    @Tag("find")
    @DisplayName("finds the variable name even if it contains curly braces")
    void curlyBraces() {
        var input = "ab{{n{1}2}}cd";
        var result = Utils.variables(input);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().size(), is(1));
        assertThat(result.get().get(0), is("n{1}2"));
    }

    @Test
    @Tag("find")
    @DisplayName("ignores a variable if it's not closed")
    void notClosed() {
        var input = "{{n1}}cd{{n11}";
        var result = Utils.variables(input);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().size(), is(1));
        assertThat(result.get().get(0), is("n1"));
    }

    @Test
    @Tag("replace")
    @DisplayName("replaces all variables")
    void all() {
        var input = "ab{{n1}}cd{{n11}}ef";
        var result = Utils.replace(input, Map.of("n1", "m1", "n11", "m2"));
        assertThat(result, is("abm1cdm2ef"));
    }

    @Test
    @Tag("replace")
    @DisplayName("replaces some variables")
    void some() {
        var input = "ab{{n1}}cd{{n11}}ef";
        var result = Utils.replace(input, Map.of("n1", "m1"));
        assertThat(result, is("abm1cd{{n11}}ef"));
    }
}
