package org.rakam.collection;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

public final class HeaderDefaultFullHttpResponse
        extends DefaultHttpResponse
        implements FullHttpResponse {

    private final ByteBuf content;
    private final HttpHeaders trailingHeaders;

    public HeaderDefaultFullHttpResponse(
            HttpVersion version,
            HttpResponseStatus status,
            ByteBuf content,
            HttpHeaders headers) {

        super(version, status);

        this.content = content;
        this.trailingHeaders = headers;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return trailingHeaders;
    }

    @Override
    public HttpHeaders headers() {
        return trailingHeaders;
    }

    @Override
    public ByteBuf content() {
        return content;
    }

    @Override
    public int refCnt() {
        return content.refCnt();
    }

    @Override
    public FullHttpResponse retain() {
        content.retain();
        return this;
    }

    @Override
    public FullHttpResponse retain(int increment) {
        content.retain(increment);
        return this;
    }

    @Override
    public boolean release() {
        return content.release();
    }

    @Override
    public boolean release(int decrement) {
        return content.release(decrement);
    }

    @Override
    public FullHttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public FullHttpResponse copy() {
        return createResponse(content.copy());
    }

    @Override
    public FullHttpResponse duplicate() {
        return createResponse(content.duplicate());
    }

    private FullHttpResponse createResponse(ByteBuf content) {
        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(
                        getProtocolVersion(),
                        getStatus(),
                        content,
                        true
                );

        response.headers().set(headers());
        response.trailingHeaders().set(trailingHeaders());

        return response;
    }
}
