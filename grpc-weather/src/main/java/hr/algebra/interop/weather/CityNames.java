package hr.algebra.interop.weather;

import java.text.Normalizer;
import java.util.Locale;

public final class CityNames {

    private CityNames() {
    }

    public static String normalize(String value) {
        String lower = value.toLowerCase(Locale.ROOT).replace('đ', 'd');
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    public static boolean matches(String cityName, String term) {
        return normalize(cityName).contains(normalize(term));
    }
}
