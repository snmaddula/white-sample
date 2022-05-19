package com.example.demo.edl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEdlProcessor {

	@Value("${edl.upsert-url}")
	private String edlUpsertUrl;
	
	private final RestTemplate restTemplate;
	
	public boolean process(Product product) {
		return upsert(product);
	}
	
	private boolean upsert(Product product) {
		HttpEntity<Product> entity = new HttpEntity<>(product);
		try {
			ResponseEntity<String> resp = restTemplate.exchange(edlUpsertUrl, HttpMethod.POST, entity, String.class);
			if(resp.getStatusCode().is2xxSuccessful()) {
				log.info(">> Received success response from EDL. Acknowledging the message");
				return true;
			}else {
				log.error(">> Response code = {}, Response body = {}", resp.getStatusCode(), resp.getBody());
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
}
