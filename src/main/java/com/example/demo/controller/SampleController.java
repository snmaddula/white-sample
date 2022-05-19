package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.RabbitProducer;

@RestController
public class SampleController {

	@Autowired
	private RabbitProducer rabbitProducer;

	@GetMapping(value = "/produce")
	public String producer(@RequestParam String message) {
		rabbitProducer.send(message);
		return "Message sent to the Rabbit Successfully";
	}

}
