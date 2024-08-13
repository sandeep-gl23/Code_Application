package com.example.developer1.Controller;

import com.example.developer1.Entity.Field;
import com.example.developer1.Service.MyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fields")
public class MyController {

    @Autowired
    MyService myService;

    @PostMapping()
    public ResponseEntity<Field> addField(@RequestBody Field field) throws JsonProcessingException {
        return myService.addField(field);
    }

    @GetMapping()
    public ResponseEntity<List<Field>> getFieldsByStatus(@RequestParam(required = false) Boolean status)
    {
        return myService.getFieldsByStatus(status);
    }

    @GetMapping("/latest-version")
    public ResponseEntity<List<Field>> getLatestVersion(@RequestParam(required = false) String code)
    {
        return myService.getLatestFields(code);
    }










}
