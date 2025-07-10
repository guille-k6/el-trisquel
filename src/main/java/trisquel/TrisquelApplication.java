package trisquel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;
import java.util.TimeZone;

@SpringBootApplication
public class TrisquelApplication {

    static {
        // Register BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        SpringApplication.run(TrisquelApplication.class, args);
    }

}
