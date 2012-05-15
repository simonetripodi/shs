package org.nnsoft.shs;

import java.net.InetAddress;

import org.nnsoft.shs.dispatcher.RequestDispatcher;

/**
 * The Server configuration.
 *
 * It has been designed as interface so users are free to implement their proxies on
 * Properties, XML, JSON, YAML, ...
 */
public interface HttpServerConfig
{

    InetAddress getBindingIp();

    int getPort();

    int getThreads();

    RequestDispatcher getRequestDispatcher();

}
