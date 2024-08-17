package de.rayzs.eatable.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtils {

    public static List<String> replaceList(List<String> rawText, String... replacements) {
        final HashMap<String, String> REPLACEMENTS = new HashMap<>();
        List<String> result = new ArrayList<>();

        if(replacements != null) {
            String firstReplacementInput = null, secondReplacementInput = null;
            for (String replacement : replacements) {
                if (firstReplacementInput == null) firstReplacementInput = replacement;
                else secondReplacementInput = replacement;

                if (firstReplacementInput != null && secondReplacementInput != null) {
                    REPLACEMENTS.put(firstReplacementInput, secondReplacementInput);
                    firstReplacementInput = null;
                    secondReplacementInput = null;
                }
            }
        }

        rawText.forEach(text -> {
            if(replacements != null)
                for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet())
                    text = text.replace(entry.getKey(), entry.getValue());
            result.add(text);
        });

        return result;
    }
}
