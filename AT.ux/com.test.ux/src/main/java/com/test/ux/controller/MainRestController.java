package com.test.ux.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Api(value = "UX Service", produces = "application/json")
public class MainRestController {
	
	
	@RequestMapping(value = "/data",method=RequestMethod.GET)
	public ResponseEntity<String> upload(){
		return new ResponseEntity<String>("OK", HttpStatus.OK);
		
	}
	
	
}
