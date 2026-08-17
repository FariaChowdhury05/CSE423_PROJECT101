package org.rakam.http;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.rakam.analysis.CustomParameter;
import org.rakam.analysis.RequestPreProcessorItem;
import org.rakam.server.http.HttpRequestHandler;
import org.rakam.server.http.HttpServer;
import org.rakam.server.http.HttpService;
import org.rakam.server.http.WebSocketService;

import javax.inject.Inject;
import java.util.Set;

@Singleton
public class WebServiceModule extends AbstractModule {

    private final Set<WebSocketService> webSocketServices;
    private final Set<HttpService> httpServices;
    private final HttpServerConfig config;
    private final Set<Tag> tags;
    private final Set<CustomParameter> customParameters;
    private final Set<RequestPreProcessorItem> requestPreProcessorItems;
    private final HttpRequestHandler requestHandler;

@Inject
public WebServiceModule(
        Set<HttpService> httpServices,
        Set<Tag> tags,
        Set<CustomParameter> customParameters,
        Set<RequestPreProcessorItem> requestPreProcessorItems,
        Set<WebSocketService> webSocketServices,
        @NotFoundHandler Optional<HttpRequestHandler> requestHandler,
        HttpServerConfig config) {

    this.httpServices = httpServices;
    this.webSocketServices = webSocketServices;
    this.requestPreProcessorItems = requestPreProcessorItems;
    this.config = config;
    this.tags = tags;
    this.customParameters = customParameters;
    this.requestHandler = requestHandler.orNull();
}

    @Override
    protected void configure() {
        HttpServerBuilderFactory factory =
                new HttpServerBuilderFactory(
                        httpServices,
                        webSocketServices,
                        tags,
                        customParameters,
                        requestPreProcessorItems,
                        config,
                        requestHandler);

        HttpServer server = factory.build();

        binder().bind(HttpServer.class).toInstance(server);
    }
}
