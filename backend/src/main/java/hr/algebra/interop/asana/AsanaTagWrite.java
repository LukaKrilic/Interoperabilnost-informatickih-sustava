package hr.algebra.interop.asana;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AsanaTagWrite(String name, String color, String notes, String workspaceGid) {
}
