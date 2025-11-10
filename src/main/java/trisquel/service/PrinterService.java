package trisquel.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.stereotype.Service;
import trisquel.model.ConfigurationMap;
import trisquel.model.Invoice;
import trisquel.model.InvoiceIvaBreakdown;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@Service
public class PrinterService {

    /**
     * Genera un PDF usando el JRXML sin cálculos.
     *
     * @param headerParams parámetros del reporte (P_SELLER_*, P_CLIENT_*, P_INVOICE_*, P_SUBTOTAL, P_IVA, P_TOTAL, P_QR_IMAGE).
     * @param itemRows     filas del detalle como Map<String,Object> (description, quantity, unit_price, iva_rate, line_net_amount, line_total).
     */
    public byte[] generateInvoicePdf(Map<String, ?> headerParams, List<Map<String, ?>> itemRows) throws Exception {
        // 1) Cargar y compilar el JRXML desde classpath
        JasperReport jasperReport;
        try (InputStream in = getClass().getResourceAsStream("/reports/Factura_A_A4.jrxml")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el JRXML en /reports/");
            }
            jasperReport = JasperCompileManager.compileReport(in);
        }

        // 2) DataSource del detalle (usamos Map para matchear nombres con underscores del JRXML)
        JRDataSource ds = new JRMapCollectionDataSource(itemRows);

        // 3) Parámetros (agregamos Locale si querés formato es-AR)
        Map<String, Object> params = new HashMap<>(headerParams);
        params.putIfAbsent(JRParameter.REPORT_LOCALE, new Locale("es", "AR"));

        // 4) Llenado del reporte
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, ds);

        // 5) Exportar a PDF (byte[])
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            JasperExportManager.exportReportToPdfStream(print, baos);
            return baos.toByteArray();
        }
    }

    /* ================== Helpers de ejemplo ================== */

    // Ejemplo de construcción de parámetros esperados por el JRXML SIN cálculos
    public static Map<String, Object> exampleParams(BufferedImage qrImage) {
        Map<String, Object> p = new HashMap<>();
        p.put("P_SELLER_NAME", "Mi Empresa S.A.");
        p.put("P_SELLER_ADDRESS", "Calle 123, Rosario");
        p.put("P_SELLER_CUIT", "30-12345678-9");

        p.put("P_CLIENT_NAME", "Cliente SRL");
        p.put("P_CLIENT_ADDRESS", "Av. Siempreviva 742, Rosario");
        p.put("P_CLIENT_CUIT", "20-23456789-0");

        p.put("P_INVOICE_NUMBER", "0001-00001234");
        p.put("P_INVOICE_DATE", LocalDate.now().toString()); // ya formateado si querés

        // Totales precalculados por tu backend
        p.put("P_SUBTOTAL", new BigDecimal("4500.50"));
        p.put("P_IVA", new BigDecimal("945.11"));
        p.put("P_TOTAL", new BigDecimal("5445.61"));

        // QR opcional (java.awt.Image)
        if (qrImage != null) {
            p.put("P_QR_IMAGE", qrImage);
        }
        return p;
    }

    public static Map<String, Object> invoiceParams(Invoice invoice, InvoiceIvaBreakdown ivaBreakDown,
                                                    ConfigurationMap orgConfig) {
        Map<String, Object> p = new HashMap<>();
        p.put("P_SELLER_NAME", orgConfig.getValueAsString("razonSocial"));
        p.put("P_SELLER_ADDRESS", orgConfig.getValueAsString("domicilioComercial"));
        p.put("P_SELLER_CUIT", orgConfig.getValueAsString("cuit"));

        p.put("P_CLIENT_NAME", invoice.getClient().getName());
        p.put("P_CLIENT_ADDRESS", invoice.getClient().getAddress());
        p.put("P_CLIENT_CUIT", invoice.getClient().getDocNumber().toString());

        p.put("P_INVOICE_NUMBER", invoice.getNumero());
        p.put("P_INVOICE_DATE", invoice.getDate()); // ya formateado si querés
        // Totales precalculados por tu backend
        p.put("P_SUBTOTAL", ivaBreakDown.getInvoiceNetTotal());
        p.put("P_IVA", ivaBreakDown.getIvaTotal());
        p.put("P_TOTAL", ivaBreakDown.getInvoiceTotal());

        // QR opcional (java.awt.Image)
        String qrImage = null;
        if (qrImage != null) {
            p.put("P_QR_IMAGE", qrImage);
        }
        return p;
    }

    // Ejemplo de filas del detalle (las cifras YA vienen calculadas por tu backend)
    public static List<Map<String, ?>> exampleItems() {
        List<Map<String, ?>> rows = new ArrayList<>();

        rows.add(row("Producto A", bd("2"), bd("1500.50"), bd("0.21"), bd("3001.00"),  // line_net_amount
                bd("3631.21")   // line_total (neto + iva de la línea)
        ));
        rows.add(row("Producto B", bd("1"), bd("1499.50"), bd("0.105"), bd("1499.50"), bd("1657.40")));
        return rows;
    }

    private static Map<String, ?> row(String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal ivaRate,
                                      BigDecimal lineNet, BigDecimal lineTotal) {
        Map<String, Object> m = new HashMap<>();
        m.put("description", description);
        m.put("quantity", quantity);
        m.put("unit_price", unitPrice);
        m.put("iva_rate", ivaRate);
        m.put("line_net_amount", lineNet);
        m.put("line_total", lineTotal);
        // Si querés también podés enviar "line_iva_amount"
        // m.put("line_iva_amount", ...);
        return m;
    }

    private static BigDecimal bd(String s) {
        return new BigDecimal(s);
    }

    /* ========== Demo standalone: genera un PDF en target/ ==========
       Quitalo si solo lo vas a usar dentro de un controller de Spring */
    public static void main(String[] args) throws Exception {
        PrinterService svc = new PrinterService();

        // Si no querés QR, pasá null
        BufferedImage qr = null; // generateQrImage("TEXTO/URL QR", 120); // ver helper abajo
        Map<String, Object> params = exampleParams(qr);
        List<Map<String, ?>> items = exampleItems();

        byte[] pdf = svc.generateInvoicePdf(params, items);
        Path out = Path.of("target", "factura-demo.pdf");
        Files.createDirectories(out.getParent());
        Files.write(out, pdf);
        System.out.println("PDF generado: " + out.toAbsolutePath());
    }

    /* ========== (Opcional) Generar QR con ZXing para P_QR_IMAGE ==========
       Si AFIP/ARCA te pide un texto/URL para QR, lo convertís a BufferedImage */
    /*
    import com.google.zxing.BarcodeFormat;
    import com.google.zxing.WriterException;
    import com.google.zxing.client.j2se.MatrixToImageWriter;
    import com.google.zxing.common.BitMatrix;
    import com.google.zxing.qrcode.QRCodeWriter;

    public static BufferedImage generateQrImage(String content, int size) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
    */
}
