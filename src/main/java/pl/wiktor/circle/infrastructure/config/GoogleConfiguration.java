package pl.wiktor.circle.infrastructure.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.wiktor.circle.domain.backup.FileSaver;
import pl.wiktor.circle.domain.exception.ApplicationException;
import pl.wiktor.circle.domain.exception.ExceptionType;
import pl.wiktor.circle.infrastructure.saver.GoogleFileSaver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@ConditionalOnProperty(value = "project.env.google.enabled", havingValue = "true")
@Configuration
public class GoogleConfiguration {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA);
    private static final Logger logger = LoggerFactory.getLogger(GoogleConfiguration.class);

    @Bean
    public Credential googleCredentials(
            @Value("${project.env.google.credentials-path}") String credentialsLocalization) {
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            InputStream in = new FileInputStream(credentialsLocalization);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (Exception e) {
            logger.error("Google credentials initialization failed, message:", e);
            throw new ApplicationException(ExceptionType.GOOGLE_CREDENTIALS_FAIL);
        }
    }

    @Bean
    public Drive googleDrive(Credential googleCredentials) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleCredentials)
                    .setApplicationName("host-custom-name")
                    .build();
        } catch (Exception e) {
            logger.error("Google drive initialization failed, message:", e);
            throw new ApplicationException(ExceptionType.GOOGLE_DRIVE_INIT_FAIL);
        }
    }

    @Bean
    @Primary
    public FileSaver googleFileSaver(Drive drive) {
        return new GoogleFileSaver(drive);
    }
}
