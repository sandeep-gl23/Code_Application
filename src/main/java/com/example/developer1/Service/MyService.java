package com.example.developer1.Service;

 import com.example.developer1.Config.AppConstants;
 import com.example.developer1.Entity.Field;
 import com.example.developer1.Entity.Info;
 import com.example.developer1.Entity.LatestFieldResult;
 import com.example.developer1.Repository.MyRepository;

 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.data.domain.Sort;
 import org.springframework.data.mongodb.core.MongoTemplate;
 import org.springframework.data.mongodb.core.aggregation.*;
 import org.springframework.data.mongodb.core.query.Criteria;
 import org.springframework.data.mongodb.core.query.Query;
 import org.springframework.http.HttpStatus;
 import org.springframework.http.ResponseEntity;
 import org.springframework.kafka.core.KafkaTemplate;
 import org.springframework.stereotype.Service;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.stream.Collectors;

 import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Service
public class MyService {

    @Autowired
    MyRepository myRepository;

    @Autowired
    KafkaTemplate<String,Info> template;



   public ResponseEntity<Field> addField(Field field) {
       Field saved_field=myRepository.save(field);

       String code=field.getCode();
       Double version=field.getVersion();

       Info obj=new Info(code,version);

       template.send(AppConstants.TOPIC_NAME,obj);

       return new ResponseEntity<>(saved_field, HttpStatus.OK);
   }

    public ResponseEntity<List<Field>> getFields() {
           List<Field>  allFields= myRepository.findAll();
           return new ResponseEntity<>(allFields,HttpStatus.OK);
    }

    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<List<Field>> getFieldsByStatus(Boolean status) {
        if(status==null)
            return getFields();

        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(status));

        List<Field> fields = mongoTemplate.find(query, Field.class, "fields");

        return new ResponseEntity<>(fields, HttpStatus.OK);
    }

    public ResponseEntity<List<Field>> getLatestFields(String code) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        if(code!=null && !code.isEmpty())
        {
            MatchOperation matchStage = Aggregation.match(Criteria.where("code").is(code));
            aggregationOperations.add(matchStage);

            Aggregation aggregation = Aggregation.newAggregation(matchStage)
                    .withOptions(AggregationOptions.builder().allowDiskUse(true).build());

             AggregationResults<Field> result = mongoTemplate.aggregate(aggregation, "fields", Field.class);
            List<Field> fields = result.getMappedResults();


             return new ResponseEntity<>(fields, HttpStatus.OK);
        }
        else {
            SortOperation sortStage = sort(Sort.by(Sort.Direction.DESC, "version"));
            aggregationOperations.add(sortStage);

             GroupOperation groupStage = group("code")
                    .first(Aggregation.ROOT).as("latestField");
            aggregationOperations.add(groupStage);

            MatchOperation matchStage = match(Criteria.where("latestField.status").is(true));
            aggregationOperations.add(matchStage);


             Aggregation aggregation = newAggregation(aggregationOperations)
                    .withOptions(AggregationOptions.builder().allowDiskUse(true).build());


             AggregationResults<LatestFieldResult> result = mongoTemplate.aggregate(aggregation, "fields", LatestFieldResult.class);
            List<LatestFieldResult> latestFieldResults = result.getMappedResults();

            List<Field> latestFields = latestFieldResults.stream()
                    .map(LatestFieldResult::getLatestField)
                    .toList();

             return new ResponseEntity<>(latestFields, HttpStatus.OK);
        }
    }
}

