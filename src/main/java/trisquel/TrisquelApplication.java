package trisquel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class TrisquelApplication {

    static {
        // Register BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        SpringApplication.run(TrisquelApplication.class, args);
    }

}
