package hr.algebra.interop.client.backend;

import hr.algebra.interop.client.config.BackendProperties;
import hr.algebra.interop.weather.grpc.TemperatureRequest;
import hr.algebra.interop.weather.grpc.TemperatureResponse;
import hr.algebra.interop.weather.grpc.WeatherServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WeatherClient {

    private final ManagedChannel channel;
    private final WeatherServiceGrpc.WeatherServiceBlockingStub stub;

    public WeatherClient(BackendProperties backend) {
        this.channel = ManagedChannelBuilder
                .forAddress(backend.grpcHost(), backend.grpcPort())
                .usePlaintext()
                .build();
        this.stub = WeatherServiceGrpc.newBlockingStub(channel);
    }

    public TemperatureResponse temperature(String grad) {
        try {
            return stub.withDeadlineAfter(20, TimeUnit.SECONDS)
                    .getTemperature(TemperatureRequest.newBuilder()
                            .setCity(grad == null ? "" : grad)
                            .build());
        } catch (StatusRuntimeException e) {
            throw new BackendException(503,
                    "gRPC servis nije dostupan (" + e.getStatus().getCode() + "). "
                            + "Radi li grpc-weather na portu 9090?");
        }
    }

    @PreDestroy
    public void zatvori() {
        channel.shutdown();
    }
}
