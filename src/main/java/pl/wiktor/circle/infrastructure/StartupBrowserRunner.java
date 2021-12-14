package pl.wiktor.circle.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@ConditionalOnProperty(value = "project.env.web.start-browser", havingValue = "true")
public class StartupBrowserRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupBrowserRunner.class);
    private final String serverPort;

    public StartupBrowserRunner(@Value("${server.port}") String serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Launching browser and redirecting to console");
        browse(String.format("http://localhost:%s/console", serverPort));
    }

    public static void browse(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                logger.error("Launching browser failed", e);
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                logger.error("Launching browser failed", e);
            }
        }
    }
}
