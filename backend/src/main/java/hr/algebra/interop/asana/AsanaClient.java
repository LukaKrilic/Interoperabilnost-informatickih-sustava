package hr.algebra.interop.asana;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AsanaClient {
    private final RestClient rest;
    private final String workspaceGid;
    private static final String TAGS_GID = "/tags/{gid}";

    public AsanaClient(@Value("${asana.pat}") String pat,
                       @Value("${asana.workspace-gid}") String workspaceGid) {
        this.rest = RestClient.builder()
                .baseUrl("https://app.asana.com/api/1.0")
                .defaultHeader("Authorization", "Bearer " + pat)
                .build();
        this.workspaceGid = workspaceGid;
    }

    public List<AsanaTag> listTags() {
        AsanaListResponse<AsanaTag> response = rest.get()
                .uri("/tags")
                .retrieve()
                .body(new ParameterizedTypeReference<AsanaListResponse<AsanaTag>>() {});
        return response == null ? List.of() : response.data();
    }

    public AsanaTag getTag(String gid) {
        AsanaSingleResponse<AsanaTag> response = rest.get()
                .uri(TAGS_GID, gid)
                .retrieve()
                .body(new ParameterizedTypeReference<AsanaSingleResponse<AsanaTag>>() {});
        return response == null ? null : response.data();
    }

    public AsanaTag createTag(String name, String color, String notes) {
        AsanaSingleResponse<AsanaTag> response = rest.post()
                .uri("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AsanaSingleResponse<>(new AsanaTagWrite(name, color, notes, workspaceGid)))
                .retrieve()
                .body(new ParameterizedTypeReference<AsanaSingleResponse<AsanaTag>>() {});
        return response == null ? null : response.data();
    }

    public AsanaTag updateTag(String gid, String name, String color, String notes) {
        AsanaSingleResponse<AsanaTag> response = rest.put()
                .uri(TAGS_GID, gid)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AsanaSingleResponse<>(new AsanaTagWrite(name, color, notes, null)))
                .retrieve()
                .body(new ParameterizedTypeReference<AsanaSingleResponse<AsanaTag>>() {});
        return response == null ? null : response.data();
    }

    public void deleteTag(String gid) {
        rest.delete()
                .uri(TAGS_GID, gid)
                .retrieve()
                .toBodilessEntity();
    }
}
