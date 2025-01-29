package net.example.batchgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ReadinessExporterTest {

  @Autowired
  ApplicationContext applicationContext;

  @Test
  public void testIsReady() {
    var f = new File(System.getProperty("java.io.tmpdir"), "ready");
    assertTrue(f.exists());
  }

  @Test
  public void testIsNotReady() throws InterruptedException {
    AvailabilityChangeEvent.publish(applicationContext, ReadinessState.REFUSING_TRAFFIC);
    var f = new File(System.getProperty("java.io.tmpdir"), "ready");
    for (int i = 0; i <= 10; i++) {
      if (!f.exists())
        break;
      sleep(Duration.ofSeconds(1));
    }
    assertFalse(f.exists());

    AvailabilityChangeEvent.publish(applicationContext, ReadinessState.ACCEPTING_TRAFFIC);
  }

}
