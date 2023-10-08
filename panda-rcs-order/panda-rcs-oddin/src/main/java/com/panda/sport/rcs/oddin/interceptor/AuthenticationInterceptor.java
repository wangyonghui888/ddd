package com.panda.sport.rcs.oddin.interceptor;

import io.grpc.*;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

/**
 * @author Z9-conway
 */
public class AuthenticationInterceptor implements ClientInterceptor {

    private final static String TOKEN_HEADER_KEY = "token";

    private final String token;

    public AuthenticationInterceptor(String token) {
        this.token = token;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new
                ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        headers.put(Metadata.Key.of(TOKEN_HEADER_KEY, ASCII_STRING_MARSHALLER), token);
                        super.start(responseListener, headers);
                    }
                };
    }
}
