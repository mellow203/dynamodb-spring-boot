package io.wonwoo.dynamodb;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass(AmazonDynamoDB.class)
@Conditional(DynamoAutoConfiguration.AwsDynamoCondition.class)
@EnableConfigurationProperties(DynamoProperties.class)
public class DynamoAutoConfiguration {

  private final DynamoProperties properties;

  public DynamoAutoConfiguration(DynamoProperties properties) {
    this.properties = properties;
  }

  @Bean
  @ConditionalOnMissingBean(name = "awsDynamoCredentialsProvider")
  public AWSCredentialsProvider awsDynamoCredentialsProvider() {
    return new AWSStaticCredentialsProvider(new BasicAWSCredentials(
        this.properties.getAccessKey(), this.properties.getSecretKey()));
  }

  @Bean
  @ConditionalOnMissingBean
  public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider awsDynamoCredentialsProvider) {
    return AmazonDynamoDBClient
        .builder()
        .withCredentials(awsDynamoCredentialsProvider)
        .withRegion(properties.getRegions())
        .build();
  }

  protected static class AwsDynamoCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context,
                                            AnnotatedTypeMetadata metadata) {
      PropertyResolver resolver = new RelaxedPropertyResolver(
          context.getEnvironment(), "spring.data.dynamodb.");
      String accessKey = resolver.getProperty("accessKey");
      String secretKey = resolver.getProperty("secretKey");
      if (StringUtils.hasLength(accessKey) && StringUtils.hasLength(secretKey)) {
        return ConditionOutcome.match("found accessKey and secretKey property");
      }
      return ConditionOutcome.noMatch("not found accessKey and secretKey property");
    }
  }
}