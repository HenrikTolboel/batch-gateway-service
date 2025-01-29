package net.example.batchgateway.adapter.input.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {
    private final BuildProperties buildProperties;

    @Value("${henrik.special:missing}")
    private String henrikSpecial;

    public InfoController(final BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping("/version")
    public String version() {
        final String buildVersion = buildProperties.get("BUILD_VERSION");
        if (null != buildVersion) {
            return "version=" + buildVersion+", henrik.special=" + henrikSpecial;
        } else {
            return "unknown";
        }
    }
}
