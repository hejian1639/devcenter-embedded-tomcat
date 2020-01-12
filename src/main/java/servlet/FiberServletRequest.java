/*
 * COMSAT
 * Copyright (c) 2013-2016, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package servlet;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author eitan
 */
public class FiberServletRequest implements ServletRequest {
    private final ServletRequest req;
    private final ServletContext servletContext;
    private final FiberHttpServlet servlet;

    public FiberServletRequest(FiberHttpServlet servlet, ServletRequest req) {
        this.servlet = servlet;
        this.req = req;
        // Jetty (what about other containers?) nullifies the following values in the request
        // when the service method returns. If we want to access them in an async context (in
        // the fiber), we need to capture them.
        servletContext = req.getServletContext();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (!servlet.disableSyncForward)
            return new FiberRequestDispatcher(path, req.getAsyncContext());
        else
            return req.getRequestDispatcher(path);
    }

    javax.servlet.ServletRequest getReq() {
        return req;
    }

    // Delegations
    @Override
    public Object getAttribute(String name) {
        return req.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return req.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return req.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        req.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        return req.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return req.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return req.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = req.getInputStream();
        inputStream.setReadListener(new ReadListener() {
            @Override
            public void onDataAvailable() throws IOException {
            }

            @Override
            public void onAllDataRead() throws IOException {
//                executor.execute(() -> {
//                    new LongRunningProcess().run();
//                    try {
//                        asyncContext.getResponse().getWriter().write("Hello World!");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    asyncContext.complete();
//                });
            }

            @Override
            public void onError(Throwable t) {
//                asyncContext.complete();
            }
        });
        return inputStream;
//        return req.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return req.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return req.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return req.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return req.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return req.getProtocol();
    }

    @Override
    public String getScheme() {
        return req.getScheme();
    }

    @Override
    public String getServerName() {
        return req.getServerName();
    }

    @Override
    public int getServerPort() {
        return req.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return req.getReader();
    }

    @Override
    public String getRemoteAddr() {
        return req.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return req.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        req.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        req.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return req.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return req.getLocales();
    }

    @Override
    public boolean isSecure() {
        return req.isSecure();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public String getRealPath(String path) {
        return req.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return req.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return req.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return req.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return req.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("you can't use startAsync mechanism from fiber.servlet");
    }

    @Override
    public AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("you can't use startAsync mechanism from fiber.servlet");
    }

    @Override
    public boolean isAsyncStarted() {
        return req.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return req.getDispatcherType();
    }

    @Override
    public int hashCode() {
        return req.hashCode();
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return req.equals(obj);
    }

    @Override
    public String toString() {
        return req.toString();
    }
}
