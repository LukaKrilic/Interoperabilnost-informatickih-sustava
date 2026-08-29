package hr.algebra.interop.soap;

import java.util.List;

public record SearchResult(int totalInFile, List<FoundTag> matches) {
}
