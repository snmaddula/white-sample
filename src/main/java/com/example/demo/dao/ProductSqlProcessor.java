package com.example.demo.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSqlProcessor {
	
	@Value("${queries.insert}")
	private String insertQuery;

	@Value("${queries.update}")
	private String updateQuery;

	private final JdbcTemplate jdbcTemplate;
	
	public boolean process(Product product) {
		return insertProduct(product);
	}

	private boolean updateProduct(Product product) {
		try {
			int rowsUpdated = jdbcTemplate.update(updateQuery, new Object[] {
					product.getTitle(), product.getDescription(), product.getPrice(), product.getId()
			});
			log.info("Updated {} row(s)", rowsUpdated);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	private boolean insertProduct(Product product) {
		boolean update = false;
		try {
			int rowsAdded = jdbcTemplate.update(insertQuery, new Object[] {
					product.getId(), product.getTitle(), product.getDescription(), product.getPrice()
			});
			log.info("Insert {} row(s)", rowsAdded);
			return true;
		}catch (DuplicateKeyException e) {
			log.error(e.getMessage());
			if(e.getMessage().contains("Unique index or primary key violation")) {
				update = true;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(update) {
			return updateProduct(product);
		}
		return false;
	}
}
