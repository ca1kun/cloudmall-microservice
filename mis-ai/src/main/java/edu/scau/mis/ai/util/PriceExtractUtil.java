package edu.scau.mis.ai.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PriceExtractUtil {

    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d{2,6})(?:\\s*)(元|块|w|W|万)?");

    private PriceExtractUtil() {
    }

    public static BigDecimal extractBudget(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        Matcher matcher = PRICE_PATTERN.matcher(message);
        BigDecimal best = null;
        while (matcher.find()) {
            BigDecimal value = new BigDecimal(matcher.group(1));
            String unit = matcher.group(2);
            if ("w".equalsIgnoreCase(unit) || "万".equals(unit)) {
                value = value.multiply(BigDecimal.valueOf(10000));
            }
            if (best == null || value.compareTo(best) > 0) {
                best = value;
            }
        }
        return best;
    }
}
