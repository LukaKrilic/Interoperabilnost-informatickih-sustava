package hr.algebra.interop.jaxb;

import hr.algebra.interop.imports.ValidationError;

import java.util.List;

public record JaxbValidationResult(boolean valid,
                                   String file,
                                   int tagCount,
                                   List<ValidationError> errors) {
}
