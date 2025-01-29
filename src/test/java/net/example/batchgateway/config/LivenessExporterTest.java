package net.example.batchgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LivenessExporterTest {

  @Autowired
  ApplicationContext applicationContext;

  @Test
  public void testIsAlive() {
    var f = new File(System.getProperty("java.io.tmpdir"), "alive");
    assertTrue(f.exists());
  }

  @Test
  public void testIsNotAlive() throws InterruptedException {
    AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN);
    var f = new File(System.getProperty("java.io.tmpdir"), "alive");
    // timeout after 10 seconds since the publish call provides no guarantees wrt. when it is complete
    for (int i = 0; i <= 10; i++) {
      if (!f.exists())
        break;
      sleep(Duration.ofSeconds(1));
    }
    assertFalse(f.exists());
    AvailabilityChangeEvent.publish(applicationContext, LivenessState.CORRECT);
  }

}
