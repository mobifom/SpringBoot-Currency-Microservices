package com.currency.microservices.currencyexchangeservice;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Column;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long>{
	
	CurrencyExchange findByFromAndTo(String from, String to);
	
}
