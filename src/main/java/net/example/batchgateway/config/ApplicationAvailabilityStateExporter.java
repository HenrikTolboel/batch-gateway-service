package net.example.batchgateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ApplicationAvailabilityStateExporter implements ApplicationListener<AvailabilityChangeEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ReadinessState readinessState = ReadinessState.REFUSING_TRAFFIC;
    private LivenessState livenessState = LivenessState.BROKEN;

    @Override
    public void onApplicationEvent(final AvailabilityChangeEvent event) {

        final File ready = new File(System.getProperty("java.io.tmpdir"), "ready");
        final File alive = new File(System.getProperty("java.io.tmpdir"), "alive");

        final ReadinessState oldReadinessState = readinessState;
        final LivenessState oldLivenessState = livenessState;

        switch (event.getState()) {
            case ReadinessState.ACCEPTING_TRAFFIC -> {
                makeFileExist(ready);
                ready.deleteOnExit();
                readinessState = ReadinessState.ACCEPTING_TRAFFIC;
            }
            case ReadinessState.REFUSING_TRAFFIC -> {
                removeFile(ready);
                readinessState = ReadinessState.REFUSING_TRAFFIC;
            }
            case LivenessState.CORRECT -> {
                makeFileExist(alive);
                alive.deleteOnExit();
                livenessState = LivenessState.CORRECT;
            }
            case LivenessState.BROKEN -> {
                removeFile(alive);
                livenessState = LivenessState.BROKEN;
            }
            default -> throw new IllegalStateException("Unexpected value: " + event.getState());
        }
        if (oldReadinessState != readinessState) {
            logger.info("ReadinessState is {}", readinessState);
        }
        if (oldLivenessState != livenessState) {
            logger.info("LivenessState is {}", livenessState);
        }
    }

    private void removeFile(final File file) {
        if (!file.delete()) {
            logger.debug("cannot remove file: {}", file.getAbsolutePath());
        }
    }

    private void makeFileExist(final File file) {
        try {
            if (!file.exists() && !file.createNewFile()) {
                logger.debug("cannot create file: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("cannot create file: {}", file.getAbsolutePath(), e);
        }
    }
}
