package com.example.developer1.Repository;

import com.example.developer1.Entity.Field;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyRepository extends MongoRepository<Field,String> {
//
//    @Query("{ 'status' : ?0 }")
//    List<Field> findByStatus(Boolean status);
}
