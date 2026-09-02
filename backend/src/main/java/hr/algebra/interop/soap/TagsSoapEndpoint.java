package hr.algebra.interop.soap;

import hr.algebra.interop.soap.generated.SearchTagsRequest;
import hr.algebra.interop.soap.generated.SearchTagsResponse;
import hr.algebra.interop.soap.generated.SoapTag;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.nio.file.Path;

@Endpoint
public class TagsSoapEndpoint {

    private static final String NAMESPACE = "http://algebra.hr/interop/soap";

    private final TagXmlGeneratorService generator;
    private final TagXPathSearchService search;

    public TagsSoapEndpoint(TagXmlGeneratorService generator, TagXPathSearchService search) {
        this.generator = generator;
        this.search = search;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "SearchTagsRequest")
    @ResponsePayload
    public SearchTagsResponse searchTags(@RequestPayload SearchTagsRequest request) {
        Path file = generator.generate();
        SearchResult result = search.search(file, request.getTerm());

        SearchTagsResponse response = new SearchTagsResponse();
        response.setGeneratedFile(file.toString());
        response.setTotalInFile(result.totalInFile());
        response.setMatchCount(result.matches().size());

        for (FoundTag tag : result.matches()) {
            SoapTag soapTag = new SoapTag();
            soapTag.setGid(tag.gid());
            soapTag.setName(tag.name());
            soapTag.setColor(tag.color());
            soapTag.setNotes(tag.notes());
            soapTag.setWorkspaceGid(tag.workspaceGid());
            response.getTag().add(soapTag);
        }
        return response;
    }
}
