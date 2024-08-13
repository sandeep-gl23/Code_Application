package com.example.developer1;


import com.example.developer1.Entity.Field;
import com.example.developer1.Entity.Info;
import com.example.developer1.Entity.LatestFieldResult;
import com.example.developer1.Repository.MyRepository;
import com.example.developer1.Service.MyService;
import net.bytebuddy.TypeCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.group;
import static java.util.Collections.sort;
import static javax.management.Query.match;
import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusTest {

        @Mock
        private MongoTemplate mongoTemplate;

        @InjectMocks
        private MyService myService;

        @Mock
        private MyRepository myRepository;

    @Mock
    private KafkaTemplate<String, Info> template;


    private List<Field> mockFields;

    private List<Field> mockCodeFields;

    private List<LatestFieldResult> mockLatestFields;

    private  Field field;

    @Mock
    AggregationResults<Field> mockAggregationResults;

    @Mock
    AggregationResults<LatestFieldResult> mockAggregationResults2;


    @BeforeEach
        public void setUp() {

        field = new Field("1", "code1", "Field1", "Description1", new Date(), new Date(), true, 1.0);

        mockFields = Arrays.asList(
                    new Field("1", "code1", "Field1", "Description1", new Date(), new Date(), true, 1.0),
                    new Field("2", "code2", "Field2", "Description2", new Date(), new Date(), false, 1.1)
            );

            mockCodeFields = Arrays.asList(
                    new Field("1", "code1", "Field1", "Description1", new Date(), new Date(), true, 1.0),
                    new Field("2", "code2", "Field2", "Description2", new Date(), new Date(), false, 2.1)
            );


            Field field1=new Field("1", "code1", "Field1", "Description1", new Date(), new Date(), true, 1.0);
            Field field2=new Field("2", "code2", "Field2", "Description2", new Date(), new Date(), false, 1.1);

        mockLatestFields = Arrays.asList(
                new LatestFieldResult(field1),
                new LatestFieldResult(field2)
         );

        }

        @Test
        public void testAddField() {
            Field savedField = new Field("1", "code1", "Field1", "Description1", new Date(), new Date(), true, 1.0);
            when(myRepository.save(field)).thenReturn(savedField);

            ResponseEntity<Field> response = myService.addField(field);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(savedField, response.getBody());

        }

        @Test
        public void testGetFieldsByStatus_NullStatus() {
            when(myRepository.findAll()).thenReturn(mockFields);

             ResponseEntity<List<Field>> response = myService.getFieldsByStatus(null);

             assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockFields, response.getBody());
        }

        @Test
        public void testGetFieldsByStatus_TrueStatus() {
             when(mongoTemplate.find(any(Query.class), eq(Field.class), eq("fields"))).thenReturn(
                    Arrays.asList(new Field("1", "code1", "Field1", "Description1", new Date(), new Date(), true, 1.0))
            );

             ResponseEntity<List<Field>> response = myService.getFieldsByStatus(true);

             assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            assertTrue(response.getBody().get(0).getStatus());
            assertEquals("code1", response.getBody().get(0).getCode());
        }

        @Test
        public void testGetFieldsByStatus_FalseStatus() {
             when(mongoTemplate.find(any(Query.class), eq(Field.class), eq("fields"))).thenReturn(
                    Arrays.asList(new Field("2", "code2", "Field2", "Description2", new Date(), new Date(), false, 1.1))
            );

             ResponseEntity<List<Field>> response = myService.getFieldsByStatus(false);

             assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
            assertFalse(response.getBody().get(0).getStatus());
            assertEquals("code2", response.getBody().get(0).getCode());
        }

    @Test
    public void testGetLatestFields_WithCode()
    {

        when(mockAggregationResults.getMappedResults()).thenReturn(mockCodeFields);

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("fields"), eq(Field.class)))
                .thenReturn(mockAggregationResults);


        ResponseEntity<List<Field>> response = myService.getLatestFields("code1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCodeFields, response.getBody());

    }

    @Test
    public void testGetLatestFields_ElsePart() {
        // Mock the AggregationResults to return mockLatestFields
        when(mockAggregationResults2.getMappedResults()).thenReturn(mockLatestFields);

        // Define the expected aggregation operations and options

        // Mock mongoTemplate.aggregate with a custom matcher for Aggregation
        when(mongoTemplate.aggregate(
                any(Aggregation.class),  // Use any() to match any Aggregation
                eq("fields"),
                eq(LatestFieldResult.class)))
                .thenReturn(mockAggregationResults2);

        ResponseEntity<List<Field>> response = myService.getLatestFields(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Field> expectedFields = mockLatestFields.stream()
                .map(LatestFieldResult::getLatestField)
                .collect(Collectors.toList());

        assertEquals(expectedFields, response.getBody());
    }
}
