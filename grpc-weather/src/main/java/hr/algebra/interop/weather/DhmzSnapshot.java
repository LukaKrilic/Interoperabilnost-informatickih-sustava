package hr.algebra.interop.weather;

import java.util.List;

public record DhmzSnapshot(String datum, String termin, List<CityTemp> cities) {
}
