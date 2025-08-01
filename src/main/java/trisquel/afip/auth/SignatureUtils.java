package trisquel.afip.auth;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class SignatureUtils {

    public SignatureUtils(String certificatePath, String privateKeyPath) {
        this.certificatePath = certificatePath;
        this.privateKeyPath = privateKeyPath;
    }

    private static String certificatePath;
    private static String privateKeyPath;

    private static final Logger logger = Logger.getLogger(SignatureUtils.class.getName());

    public static byte[] signCMS(String xmlContent) throws Exception {
        X509Certificate certificate = loadCertificateFromPEM(certificatePath);
        RSAPrivateKey privateKey = loadPrivateKeyFromPEM(privateKeyPath);
        verifyCertificate();
        return createCMSSignature(xmlContent.getBytes("UTF-8"), certificate, privateKey);
    }

    public static X509Certificate loadCertificateFromPEM(String certPath) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(certPath)) {
            return (X509Certificate) cf.generateCertificate(fis);
        }
    }

    public static RSAPrivateKey loadPrivateKeyFromPEM(String keyPath) throws Exception {
        String pemContent = new String(Files.readAllBytes(Paths.get(keyPath)));
        pemContent = pemContent.replace("-----BEGIN PRIVATE KEY-----", "");
        pemContent = pemContent.replace("-----END PRIVATE KEY-----", "");
        pemContent = pemContent.replace("-----BEGIN RSA PRIVATE KEY-----", "");
        pemContent = pemContent.replace("-----END RSA PRIVATE KEY-----", "");
        pemContent = pemContent.replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(pemContent);
        // Crear clave privada
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

    /**
     * BouncyCastle CMS signature -- Equals: openssl cms -sign -nodetach -outform der
     *
     * @param data
     * @param cert
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] createCMSSignature(byte[] data, X509Certificate cert, RSAPrivateKey privateKey)
            throws Exception {
        try {
            List<X509Certificate> certList = new ArrayList<>();
            certList.add(cert);
            Store certStore = new JcaCertStore(certList);
            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privateKey);
            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, cert));
            generator.addCertificates(certStore);
            CMSTypedData content = new CMSProcessableByteArray(data);
            CMSSignedData signedData = generator.generate(content, true);
            return signedData.getEncoded();
        } catch (Exception e) {
            throw new Exception("Error en firma CMS: " + e.getMessage(), e);
        }
    }

    /**
     * Utility for verifying if my AFIP certificate was created right
     */
    public static void verifyCertificate() throws Exception {
        System.out.println("=== Verificando certificado ===");

        X509Certificate certificate = loadCertificateFromPEM(certificatePath);

        // Verificar información del certificado
        System.out.println("Subject: " + certificate.getSubjectDN().getName());
        System.out.println("Issuer: " + certificate.getIssuerDN().getName());
        System.out.println("Serial Number: " + certificate.getSerialNumber());
        System.out.println("Valid From: " + certificate.getNotBefore());
        System.out.println("Valid Until: " + certificate.getNotAfter());

        // Verificar si está vencido
        try {
            certificate.checkValidity();
            System.out.println("✓ Certificado válido (no vencido)");
        } catch (Exception e) {
            logger.severe("✗ Certificado vencido o inválido: " + e.getMessage());
            throw e;
        }

        // Verificar que el Subject contenga el CUIT
        String subject = certificate.getSubjectDN().getName();
        if (!subject.contains("CUIT")) {
            logger.warning("⚠ El certificado no parece contener CUIT en el Subject");
        }

        System.out.println("=== Verificación completada ===");
    }
}
