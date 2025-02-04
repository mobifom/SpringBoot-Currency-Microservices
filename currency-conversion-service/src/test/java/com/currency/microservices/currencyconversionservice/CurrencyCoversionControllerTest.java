package com.currency.microservices.currencyconversionservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class CurrencyCoversionControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CurrencyCoversionController currencyCoversionController;

    @MockBean
    private CurrencyExchangeProxy proxy;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(currencyCoversionController).build();
    }

    @Test
    public void testCalculateCurrencyConversion() throws Exception {
        CurrencyConversion mockConversion = new CurrencyConversion(1L, "USD", "INR", BigDecimal.ONE, BigDecimal.valueOf(75), BigDecimal.valueOf(75), "8000");
        ResponseEntity<CurrencyConversion> responseEntity = ResponseEntity.ok(mockConversion);
        ResponseEntity forEntity = restTemplate.getForEntity(any(String.class), eq(CurrencyConversion.class), any(HashMap.class));
		when(forEntity).thenReturn(responseEntity);

        mockMvc.perform(get("/currency-conversion/from/USD/to/INR/quantity/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.from").value("USD"))
            .andExpect(jsonPath("$.to").value("INR"))
            .andExpect(jsonPath("$.conversionMultiple").value(75))
            .andExpect(jsonPath("$.quantity").value(1))
            .andExpect(jsonPath("$.totalCalculatedAmount").value(75))
            .andExpect(jsonPath("$.environment").value("8000"));
    }

    @Test
    public void testCalculateCurrencyConversionFeign() throws Exception {
        CurrencyConversion mockConversion = new CurrencyConversion(1L, "USD", "INR", BigDecimal.ONE, BigDecimal.valueOf(75), BigDecimal.valueOf(75), "8000");
        when(proxy.retrieveExchangeValue("USD", "INR")).thenReturn(mockConversion);

        mockMvc.perform(get("/currency-conversion-feign/from/USD/to/INR/quantity/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.from").value("USD"))
            .andExpect(jsonPath("$.to").value("INR"))
            .andExpect(jsonPath("$.conversionMultiple").value(75))
            .andExpect(jsonPath("$.quantity").value(1))
            .andExpect(jsonPath("$.totalCalculatedAmount").value(75))
            .andExpect(jsonPath("$.environment").value("8000"));
    }
}