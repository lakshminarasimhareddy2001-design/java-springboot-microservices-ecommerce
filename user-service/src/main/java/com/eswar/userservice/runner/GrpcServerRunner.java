package com.eswar.userservice.runner;

import com.eswar.userservice.service.GrpcUserService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class GrpcServerRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        int port = args.containsOption("grpc.port") ?
                Integer.parseInt(Objects.requireNonNull(args.getOptionValues("grpc.port")).getFirst()) : 9090;

        ServerBuilder.forPort(port)
                .addService(new GrpcUserService())
                .build()
                .start();

        log.info("gRPC server started on port {}" , port);
    }
}
