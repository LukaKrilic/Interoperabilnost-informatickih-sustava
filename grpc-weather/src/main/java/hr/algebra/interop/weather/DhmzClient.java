package hr.algebra.interop.weather;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DhmzClient {

    public static final String URL = "https://vrijeme.hr/hrvatska_n.xml";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public DhmzSnapshot fetch() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(URL))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    http.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new IllegalStateException("DHMZ je vratio HTTP " + response.statusCode());
            }
            try (InputStream body = response.body()) {
                return parse(body);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Ne mogu dohvatiti " + URL, e);
        }
    }

    DhmzSnapshot parse(InputStream xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = dbf.newDocumentBuilder().parse(xml);

        List<CityTemp> cities = new ArrayList<>();
        NodeList gradovi = doc.getElementsByTagName("Grad");
        for (int i = 0; i < gradovi.getLength(); i++) {
            Element grad = (Element) gradovi.item(i);
            String name = text(grad, "GradIme");
            if (name != null && !name.isEmpty()) {
                cities.add(new CityTemp(name, temperature(text(grad, "Temp"))));
            }
        }

        Element root = doc.getDocumentElement();
        return new DhmzSnapshot(text(root, "Datum"), text(root, "Termin"), cities);
    }

    private static String text(Element parent, String tag) {
        NodeList found = parent.getElementsByTagName(tag);
        return found.getLength() == 0 ? null : found.item(0).getTextContent().trim();
    }

    private static Double temperature(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
