package net.example.batchgateway.adapter.output.useapplerootca;

import com.nimbusds.jose.util.X509CertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;

@Component
public class UseAppleRootCaAdapter {

    private File appleRootCaFile;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private X509Certificate appleRootCa;

    public UseAppleRootCaAdapter(File appleRootCaFile) throws IOException {
        this.appleRootCaFile = appleRootCaFile;
        appleRootCa = X509CertUtils.parse(new FileInputStream(appleRootCaFile).readAllBytes());
    }

    public File getAppleRootCaFile() {
        return appleRootCaFile;
    }
}
