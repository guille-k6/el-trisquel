package trisquel.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar contraseñas encriptadas con BCrypt
 * Ejecuta este archivo como una aplicación Java normal para generar hashes
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Cambia estas contraseñas por las que necesites
        String password1 = "admin123";

        String encoded1 = encoder.encode(password1);

        System.out.println("Contraseña original: " + password1);
        System.out.println("Hash BCrypt: " + encoded1);
        System.out.println();

        // Usa estos hashes en tu SQL INSERT
    }
}
