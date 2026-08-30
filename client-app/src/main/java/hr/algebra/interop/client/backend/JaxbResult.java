package hr.algebra.interop.client.backend;

import java.util.List;

public record JaxbResult(boolean valid,
                         String file,
                         int tagCount,
                         List<ValidationErrorView> errors) {
}
