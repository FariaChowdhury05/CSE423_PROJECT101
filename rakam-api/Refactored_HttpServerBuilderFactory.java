package org.rakam.http;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.swagger.models.*;
import io.swagger.models.Response;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.properties.RefProperty;
import io.swagger.util.PrimitiveType;
import org.apache.avro.generic.GenericRecord;
import org.rakam.ServiceStarter;
import org.rakam.analysis.CustomParameter;
import org.rakam.analysis.RequestPreProcessorItem;
import org.rakam.server.http.*;
import org.rakam.server.http.HttpServerBuilder.IRequestParameterFactory;
import org.rakam.server.http.annotations.ApiOperation;
import org.rakam.util.JsonHelper;
import org.rakam.util.LogUtil;
import org.rakam.util.RakamException;

import java.time.ZoneId;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;

public class HttpServerBuilderFactory {

    private final Set<HttpService> httpServices;
    private final Set<WebSocketService> webSocketServices;
    private final Set<Tag> tags;
    private final Set<CustomParameter> customParameters;
    private final Set<RequestPreProcessorItem> requestPreProcessorItems;
    private final HttpServerConfig config;
    private final HttpRequestHandler requestHandler;

    public HttpServerBuilderFactory(
            Set<HttpService> httpServices,
            Set<WebSocketService> webSocketServices,
            Set<Tag> tags,
            Set<CustomParameter> customParameters,
            Set<RequestPreProcessorItem> requestPreProcessorItems,
            HttpServerConfig config,
            HttpRequestHandler requestHandler) {

        this.httpServices = httpServices;
        this.webSocketServices = webSocketServices;
        this.tags = tags;
        this.customParameters = customParameters;
        this.requestPreProcessorItems = requestPreProcessorItems;
        this.config = config;
        this.requestHandler = requestHandler;
    }

    public HttpServer build() {

        Swagger swagger = createSwagger();
        EventLoopGroup eventExecutors = createEventLoopGroup();

        HttpServerBuilder builder = new HttpServerBuilder()
                .setHttpServices(httpServices)
                .setWebsocketServices(webSocketServices)
                .setSwagger(swagger)
                .setEventLoopGroup(eventExecutors)
                .setSwaggerOperationProcessor(this::processSwaggerOperation)
                .setMapper(JsonHelper.getMapper())
                .setProxyProtocol(config.getProxyProtocol())
                .setExceptionHandler(this::handleException)
                .setOverridenMappings(
                        ImmutableMap.of(
                                GenericRecord.class,
                                PrimitiveType.OBJECT,
                                ZoneId.class,
                                PrimitiveType.STRING));

        addPreprocessors(builder);
        addCustomParameters(builder);

        builder.setMaximumBody(
                config.getMaximumRequestSize());

        HttpServer server = builder.build();

        if (requestHandler != null) {
            server.setNotFoundHandler(requestHandler);
        }

        bind(server);

        return server;
    }

    private Swagger createSwagger() {

        Info info = new Info()
                .title("Rakam API Documentation")
                .version(ServiceStarter.RAKAM_VERSION)
                .description(
                        "An analytics platform API that lets you create your own analytics services.")
                .contact(new Contact().email("contact@rakam.io"))
                .license(new License()
                        .name("Apache License 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));

        return new Swagger()
                .info(info)
                .basePath("/")
                .tags(ImmutableList.copyOf(tags))
                .securityDefinition(
                        "write_key",
                        new ApiKeyAuthDefinition()
                                .in(In.HEADER)
                                .name("write_key"))
                .securityDefinition(
                        "master_key",
                        new ApiKeyAuthDefinition()
                                .in(In.HEADER)
                                .name("master_key"));
    }

    private EventLoopGroup createEventLoopGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        }

        return new NioEventLoopGroup();
    }

    private void processSwaggerOperation(
            java.lang.reflect.Method method,
            Operation operation) {

        ApiOperation annotation =
                method.getAnnotation(ApiOperation.class);

        if (annotation != null &&
                annotation.authorizations() != null &&
                annotation.authorizations().length > 0) {

            String value =
                    annotation.authorizations()[0].value();

            if (value != null && !value.isEmpty()) {
                operation.response(
                        FORBIDDEN.code(),
                        new Response()
                                .schema(new RefProperty("ErrorMessage"))
                                .description(value + " is invalid"));
            }
        }
    }

    private void handleException(
            RakamHttpRequest request,
            Throwable ex) {

        if (ex instanceof RakamException) {
            RakamException rakamException =
                    (RakamException) ex;

            if (rakamException.getStatusCode() != FORBIDDEN) {
                LogUtil.logException(request, rakamException);
            }
        }

        if (!(ex instanceof HttpRequestException)) {
            LogUtil.logException(request, ex);
        }
    }

    private void addPreprocessors(
            HttpServerBuilder builder) {

        requestPreProcessorItems.forEach(
                item -> builder.addJsonPreprocessor(
                        item.processor,
                        item.predicate));
    }

    private void addCustomParameters(
            HttpServerBuilder builder) {

        ImmutableMap.Builder<String, IRequestParameterFactory>
                parameterBuilder = ImmutableMap.builder();

        for (CustomParameter parameter : customParameters) {
            parameterBuilder.put(
                    parameter.parameterName,
                    parameter.factory);
        }

        builder.setCustomRequestParameters(
                parameterBuilder.build());
    }

    private void bind(HttpServer server) {

        HostAndPort address = config.getAddress();

        try {
            server.bind(
                    address.getHostText(),
                    address.getPort());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
