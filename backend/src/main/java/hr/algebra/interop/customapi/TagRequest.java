package hr.algebra.interop.customapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(@Size(max = 30) String gid,
                         @NotBlank @Size(max = 200) String name,
                         @Size(max = 30) String color,
                         String notes,
                         @Size(max = 30) String workspaceGid) {
}
