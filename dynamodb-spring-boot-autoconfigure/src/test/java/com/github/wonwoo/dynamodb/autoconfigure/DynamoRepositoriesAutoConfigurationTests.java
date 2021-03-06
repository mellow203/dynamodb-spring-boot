/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.wonwoo.dynamodb.autoconfigure;

import com.github.wonwoo.dynamodb.TestAutoConfigurationPackage;
import com.github.wonwoo.dynamodb.autoconfigure.person.Person;
import com.github.wonwoo.dynamodb.autoconfigure.person.PersonRepository;
import com.github.wonwoo.dynamodb.empty.EmptyDataPackage;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author wonwoo
 */
public class DynamoRepositoriesAutoConfigurationTests {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class,
          DynamoAutoConfiguration.class, DynamoDataAutoConfiguration.class,
          DynamoRepositoriesAutoConfiguration.class));


  @Test
  public void defaultRepository() throws Exception {

    contextRunner.withUserConfiguration(DefaultConfiguration.class)
        .withPropertyValues("spring.data.dynamodb.access-key=test", "spring.data.dynamodb.secret-key=test")
        .run(context -> assertThat(context).hasSingleBean(PersonRepository.class));
  }

  @Test
  public void disabledRepositories() {
    contextRunner.withUserConfiguration(DefaultConfiguration.class)
        .withPropertyValues("spring.data.dynamodb.access-key=test", "spring.data.dynamodb.secret-key=test",
            "spring.data.dynamodb.repositories.enabled=none")
        .run(context -> assertThat(context).doesNotHaveBean(PersonRepository.class));
  }

  @Test
  public void noRepositoryAvailable() throws Exception {
    contextRunner.withUserConfiguration(NoRepositoryConfiguration.class)
        .withPropertyValues("spring.data.dynamodb.access-key=test", "spring.data.dynamodb.secret-key=test")
        .run(context -> assertThat(context).doesNotHaveBean(PersonRepository.class));
  }

  @Configuration
  @TestAutoConfigurationPackage(Person.class)
  static class DefaultConfiguration {

  }

  @Configuration
  @TestAutoConfigurationPackage(Person.class)
  static class DynamoNotAvailableConfiguration {

  }


  @Configuration
  @TestAutoConfigurationPackage(EmptyDataPackage.class)
  static class NoRepositoryConfiguration {

  }
}