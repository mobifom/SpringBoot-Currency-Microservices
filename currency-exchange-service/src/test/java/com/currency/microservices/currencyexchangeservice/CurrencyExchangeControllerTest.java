package com.currency.microservices.currencyexchangeservice;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class CurrencyExchangeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CurrencyExchangeController currencyExchangeController;

    @MockBean
    private CurrencyExchangeRepository currencyExchangeRepository;

    @Mock
    private Environment environment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(currencyExchangeController).build();
    }

    @Test
    public void testRetrieveExchangeValue() throws Exception {
        CurrencyExchange mockExchange = new CurrencyExchange(10001L, "USD", "INR", BigDecimal.valueOf(75.00));
        when(currencyExchangeRepository.findByFromAndTo(anyString(), anyString())).thenReturn(mockExchange);
        when(environment.getProperty("local.server.port")).thenReturn("8000");

        mockMvc.perform(get("/currency-exchange/from/USD/to/INR"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10001))
            .andExpect(jsonPath("$.from").value("USD"))
            .andExpect(jsonPath("$.to").value("INR"))
            .andExpect(jsonPath("$.conversionMultiple").value(75.00))
            .andExpect(jsonPath("$.environment").value("8000"));
    }

    @Test
    public void testRetrieveExchangeValue_NotFound() throws Exception {
        when(currencyExchangeRepository.findByFromAndTo(anyString(), anyString())).thenReturn(null);
        
        mockMvc.perform(get("/currency-exchange/from/USD/to/INR"))
            .andExpect(status().isInternalServerError());
    }
}