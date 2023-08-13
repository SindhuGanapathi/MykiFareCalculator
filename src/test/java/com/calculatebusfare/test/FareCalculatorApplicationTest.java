package com.calculatebusfare.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.busfare.calculate.FareProcessor;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes={FareProcessor.class})
public class FareCalculatorApplicationTest {

	@Autowired
    private FareProcessor fareProcessor;
	
    @Test
    public void testCSVOutput() {

        fareProcessor.readTaps();
    }
	
}