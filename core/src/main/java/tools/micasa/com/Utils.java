package tools.micasa.com;

import java.util.*;

public class Utils {

    /**
     * Finds all the variables in a string. <br/>
     * A variable is a string with the following format: {@code {{name}}}
     * @param input the input string
     * @return the list of variable names or {@code Optional.EMPTY} if the string doesn't contain any variable
     */
    public static Optional<List<String>> variables(String input) {
        List<String> result = new ArrayList<>();
        var i = 0;
        while (i < input.length() - 5) {
            if (input.charAt(i) == '{' && input.charAt(i + 1) == '{') {
                var j = i + 2;
                while (j < input.length() - 2 && !(input.charAt(j + 1) == '}' && input.charAt(j + 2) == '}')) {
                    j += 1;
                }
                if (j != input.length() - 2) {
                    result.add(input.substring(i + 2, j + 1));
                    i = j + 3;
                } else {
                    break;
                }
            } else {
                i += 1;
            }
        }
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    /**
     * Replaces all the variables with their value in the given string.
     * @param input the input string
     * @param values the variable names and values
     * @return the string with the variables substituted
     */
    public static String replace(String input, Map<String, String> values) {
        if (values == null || values.isEmpty()) return input;
        return variables(input)
                .map(names -> names
                    .stream()
                    .filter(values::containsKey)
                    .reduce(input, (newValue, name) -> newValue.replaceAll("\\{\\{" + name + "\\}\\}", values.get(name)))
                )
                .orElse(input);
    }

}
