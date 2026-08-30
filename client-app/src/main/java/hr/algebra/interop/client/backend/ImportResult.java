package hr.algebra.interop.client.backend;

import java.util.List;

public record ImportResult(boolean valid,
                           Integer imported,
                           List<ValidationErrorView> errors) {
}
