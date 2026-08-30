package hr.algebra.interop.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backend")
public record BackendProperties(String url, String grpcHost, int grpcPort) {
}
