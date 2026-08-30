package hr.algebra.interop.client.backend;

import hr.algebra.interop.client.soap.generated.SearchTagsRequest;
import hr.algebra.interop.client.soap.generated.SearchTagsResponse;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SoapSearchClient {

    private final WebServiceTemplate template;

    public SoapSearchClient(WebServiceTemplate tagsWebServiceTemplate) {
        this.template = tagsWebServiceTemplate;
    }

    public SearchTagsResponse pretrazi(String pojam) {
        SearchTagsRequest request = new SearchTagsRequest();
        request.setTerm(pojam == null ? "" : pojam);

        try {
            return (SearchTagsResponse) template.marshalSendAndReceive(request);
        } catch (WebServiceIOException e) {
            throw new BackendException(503, "SOAP servis nije dostupan. Radi li backend na portu 8080?");
        }
    }
}
