package hr.algebra.interop.weather;

import hr.algebra.interop.weather.grpc.CityTemperature;
import hr.algebra.interop.weather.grpc.TemperatureRequest;
import hr.algebra.interop.weather.grpc.TemperatureResponse;
import hr.algebra.interop.weather.grpc.WeatherServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class WeatherService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final DhmzClient dhmz;

    public WeatherService(DhmzClient dhmz) {
        this.dhmz = dhmz;
    }

    @Override
    public void getTemperature(TemperatureRequest request,
                               StreamObserver<TemperatureResponse> observer) {
        try {
            DhmzSnapshot snapshot = dhmz.fetch();

            TemperatureResponse.Builder response = TemperatureResponse.newBuilder()
                    .setSource(DhmzClient.URL)
                    .setMeasuredAt(snapshot.datum() + " u " + snapshot.termin() + "h")
                    .setTotalCities(snapshot.cities().size());

            int matches = 0;
            for (CityTemp city : snapshot.cities()) {
                if (!CityNames.matches(city.name(), request.getCity())) {
                    continue;
                }
                CityTemperature.Builder entry = CityTemperature.newBuilder()
                        .setName(city.name());
                if (city.temperatureC() != null) {
                    entry.setTemperatureC(city.temperatureC());
                }
                response.addCity(entry);
                matches++;
            }

            observer.onNext(response.setMatchCount(matches).build());
            observer.onCompleted();
        } catch (Exception e) {
            observer.onError(Status.UNAVAILABLE
                    .withDescription("DHMZ nedostupan: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}
