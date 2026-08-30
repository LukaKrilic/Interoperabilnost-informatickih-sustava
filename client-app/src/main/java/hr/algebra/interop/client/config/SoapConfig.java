package hr.algebra.interop.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapConfig {

    /**
     * Paket generiran xjc-om iz src/main/resources/soap/tags-service.xsd.
     * Jaxb2Marshaller je InitializingBean, pa Spring sam pozove afterPropertiesSet()
     * i izgradi JAXBContext prije nego ga itko dobije ubrizganog.
     */
    @Bean
    public Jaxb2Marshaller tagsSoapMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("hr.algebra.interop.client.soap.generated");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate tagsWebServiceTemplate(Jaxb2Marshaller tagsSoapMarshaller,
                                                     BackendProperties backend) {
        WebServiceTemplate template = new WebServiceTemplate(tagsSoapMarshaller);
        template.setDefaultUri(backend.url() + "/ws");
        return template;
    }
}
