package net.example.batchgateway.adapter.output.useapplerootca;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UseAppleRootCaAdapterTest {

    private File appleRootCa = new File("./src/main/resources/AppleRootCA-G3.cer");

    private UseAppleRootCaAdapter useAppleRootCaAdapter = new UseAppleRootCaAdapter(appleRootCa);

    UseAppleRootCaAdapterTest() throws IOException {
    }

    @Test
    void getAppleRootCaFile() {
        File res = useAppleRootCaAdapter.getAppleRootCaFile();
        assertNotNull(res);
    }
}
