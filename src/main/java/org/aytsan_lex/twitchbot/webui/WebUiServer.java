package org.aytsan_lex.twitchbot.webui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.javalin.Javalin;

// TODO: Implement simple Web-UI for TwitchBot
// https://github.com/javalin/javalin
// https://javalin.io/documentation#getting-started

public class WebUiServer
{
    public static class Builder
    {
        private String hostAddress = "127.0.0.1";
        private int port = 8080;

        public Builder withHostAddress(final String hostAddress) { this.hostAddress = hostAddress; return this; }
        public Builder withPort(final int port) { this.port = port; return this; }
        public WebUiServer build() { return new WebUiServer(this.hostAddress, this.port); }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUiServer.class);
    private final Javalin app;

    private static final RootPageGetHandler rootPageGetHandler = new RootPageGetHandler();
    private static final RootPagePostHandler rootPagePostHandler = new RootPagePostHandler();

    private WebUiServer(final String host, final int port)
    {
        this.app = Javalin.create(javalinConfig -> {
            javalinConfig.jetty.defaultPort = port;
            javalinConfig.jetty.defaultHost = host;
            javalinConfig.http.asyncTimeout = 10_000L;
            javalinConfig.useVirtualThreads = true;
        });

        this.app.get("/", rootPageGetHandler);
        this.app.post("/", rootPagePostHandler);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public void start()
    {
        LOGGER.info("Starting Web-UI server...");
        this.app.start();
    }

    public void stop()
    {
        LOGGER.info("Stopping Web-UI server...");
        this.app.stop();
    }

    public int getPort()
    {
        return this.app.port();
    }
}
