package com.parses.server.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

public class InTraceCloseableHttpClient extends CloseableHttpClient {
    private final CloseableHttpClient httpClient;

    private InTraceCloseableHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static InTraceCloseableHttpClient wrapUp(CloseableHttpClient client) {
        return new InTraceCloseableHttpClient(client);
    }

    public void close() throws IOException {
        this.httpClient.close();
    }

    public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) {
        return this.doExecute(target, request, context);
    }

    public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws ClientProtocolException {
        return this.doExecute(determineTarget(request), request, context);
    }

    public CloseableHttpResponse execute(HttpUriRequest request) throws ClientProtocolException {
        return this.execute(request, (HttpContext)null);
    }

    public CloseableHttpResponse execute(HttpHost target, HttpRequest request) {
        return this.doExecute(target, request, (HttpContext)null);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return (T)this.execute((HttpUriRequest)request, (ResponseHandler)responseHandler, (HttpContext)null);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        HttpHost target = determineTarget(request);
        return (T)this.execute(target, request, responseHandler, context);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return (T)this.execute(target, request, responseHandler, (HttpContext)null);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        return (T)this.executeAux(target, request, responseHandler, context);
    }

    public HttpParams getParams() {
        return this.httpClient.getParams();
    }

    public ClientConnectionManager getConnectionManager() {
        return this.httpClient.getConnectionManager();
    }
    
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) {
        Object invoke = null;
        try {
            Method declaredMethod = this.httpClient.getClass().getDeclaredMethod("doExecute", HttpHost.class, HttpRequest.class, HttpContext.class);
            declaredMethod.setAccessible(true);
            invoke = declaredMethod.invoke(this.httpClient, target, request, context);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            if (e instanceof InvocationTargetException) {
                Throwable cause = ((InvocationTargetException)e).getTargetException();
                throw new RuntimeException(cause);
            }
        }

        return (CloseableHttpResponse)invoke;
    }


    private <T> T executeAux(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        Args.notNull(responseHandler, "Response handler");
        CloseableHttpResponse response = this.execute(target, request, context);

        T retVal;
        try {
            T result = (T)responseHandler.handleResponse(response);
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
            retVal = result;
        } catch (ClientProtocolException ex) {
            HttpEntity entity = response.getEntity();

            try {
                EntityUtils.consume(entity);
            } catch (Exception ex2) {

            }

            throw ex;
        } finally {
            response.close();
        }

        return retVal;
    }

    private static HttpHost determineTarget(HttpUriRequest request) throws ClientProtocolException {
        HttpHost target = null;
        URI requestURI = request.getURI();
        if (requestURI.isAbsolute()) {
            target = URIUtils.extractHost(requestURI);
            if (target == null) {
                throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
            }
        }

        return target;
    }
}
