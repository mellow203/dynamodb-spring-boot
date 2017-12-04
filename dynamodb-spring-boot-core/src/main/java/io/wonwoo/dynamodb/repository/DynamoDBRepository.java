package io.wonwoo.dynamodb.repository;

import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface DynamoDBRepository<T, ID extends Serializable> extends DynamoDBCrudRepository<T, ID> {

  @Override
  List<T> findAll();

  <S extends T> List<S> save(Iterable<S> entites);

}