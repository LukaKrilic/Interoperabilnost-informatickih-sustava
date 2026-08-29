package hr.algebra.interop.weather;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

public class WeatherServer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new WeatherService(new DhmzClient()))
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        System.out.println("gRPC weather server slusa na portu " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
        server.awaitTermination();
    }
}
