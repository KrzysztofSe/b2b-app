package b2b.config;

import com.mongodb.MongoCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import static org.springframework.util.StringUtils.isEmpty;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    private final String mongoHost;
    private final int mongoPort;
    private final String mongoUser;
    private final String mongoPassword;
    private final String mongoDatabase;

    public ApplicationConfig(@Value("${mongo.host}") String mongoHost, @Value("${mongo.port}") String mongoPort,
                             @Value("${mongo.user}") String mongoUser, @Value("${mongo.password}") String mongoPassword,
                             @Value("${spring.data.mongodb.database}") String mongoDatabase) {
        this.mongoHost = mongoHost;
        this.mongoPort = Integer.parseInt(mongoPort);
        this.mongoUser = mongoUser;
        this.mongoPassword = mongoPassword;
        this.mongoDatabase = mongoDatabase;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public MongoClientFactoryBean mongo() {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        mongo.setHost(mongoHost);
        mongo.setPort(mongoPort);

        if (!isEmpty(mongoUser) && !isEmpty(mongoPassword) && !isEmpty(mongoDatabase)) {
            MongoCredential creds = MongoCredential.createCredential(mongoUser, mongoDatabase, mongoPassword.toCharArray());
            mongo.setCredentials(new MongoCredential[]{creds});
        }

        return mongo;
    }
}
