package trisquel.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import trisquel.afip.model.AfipIva;
import trisquel.model.ConfigurationMap;
import trisquel.model.Invoice;
import trisquel.model.InvoiceItem;
import trisquel.model.InvoiceIvaBreakdown;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Service
public class InvoiceReportService {

    private volatile JasperReport cachedReport;
    private volatile Image cachedLogo;

    /**
     * Compila el JRXML 1 sola vez y lo cachea
     */
    private JasperReport getReport() throws Exception {
        if (cachedReport == null || 1 == 1) {
            synchronized (this) {
                if (cachedReport == null) {
                    try (InputStream in = new ClassPathResource("reportdemo/Factura_A_A4.jrxml").getInputStream()) {
                        cachedReport = JasperCompileManager.compileReport(in);
                    }
                }
            }
        }
        return cachedReport;
    }

    private Image loadLogo() throws Exception {
        if (cachedLogo == null) {
            synchronized (this) {
                if (cachedLogo == null) {
                    try (InputStream in = new ClassPathResource("image/trisquellogo.jpg").getInputStream()) {
                        cachedLogo = ImageIO.read(in); // BufferedImage -> Image
                    }
                }
            }
        }
        return cachedLogo;
    }

    public byte[] generateInvoiceReport(Invoice invoice, InvoiceIvaBreakdown ivaBreakdown, ConfigurationMap orgInfo)
            throws Exception {
        List<Map<String, ?>> items = new ArrayList<Map<String, ?>>();
        for (InvoiceItem item : invoice.getItems()) {
            Map<String, Object> itemParameters = new HashMap<>();
            itemParameters.put("product_code", item.getProduct().getId().toString());
            itemParameters.put("description", item.getProductNameAlias() == null ? item.getProduct().getName() : item.getProductNameAlias());
            itemParameters.put("unit_measure", item.getProduct().getMeasureUnit());
            itemParameters.put("quantity", BigDecimal.valueOf(item.getAmount()));
            itemParameters.put("unit_price", item.getPricePerUnit());
            itemParameters.put("iva_rate", BigDecimal.valueOf(item.getIva().getPercentage()));
            itemParameters.put("line_net_amount", item.getTotal().subtract(item.getIvaAmount()));
            itemParameters.put("line_iva_amount", item.getIvaAmount());
            itemParameters.put("line_total", item.getTotal());
            items.add(itemParameters);
        }

        Map<String, Object> p = new HashMap<>(); // Mapa de parametros del reporte

        p.put("P_SELLER_NAME", orgInfo.getValueAsString("razonSocial"));
        p.put("P_SELLER_ADDRESS", orgInfo.getValueAsString("domicilioComercial"));
        p.put("P_SELLER_CITY", orgInfo.getValueAsString("ciudad"));
        p.put("P_SELLER_PROVINCE", orgInfo.getValueAsString("provincia"));
        p.put("P_SELLER_POSTAL_CODE", orgInfo.getValueAsString("codigoPostal"));
        p.put("P_SELLER_CUIT", orgInfo.getValueAsString("cuit"));
        p.put("P_SELLER_IVA_CONDITION", orgInfo.getValueAsString("condicionIva"));
        p.put("P_SELLER_GROSS_INCOME", orgInfo.getValueAsString("ingresosBrutos"));
        p.put("P_SELLER_START_DATE", orgInfo.getValueAsString("fechaInicioActividades"));
        p.put("P_SELLER_PHONE", orgInfo.getValueAsString("telefono"));
        p.put("P_SELLER_EMAIL", orgInfo.getValueAsString("mail"));
        p.put("P_TRISQUEL_LOGO", loadLogo());
        // Client
        p.put("P_CLIENT_NAME", invoice.getClient().getName());
        p.put("P_CLIENT_ADDRESS", invoice.getClient().getAddress());
        p.put("P_CLIENT_CUIT", invoice.getClient().getDocNumber().toString());
        p.put("P_CLIENT_IVA_CONDITION", invoice.getClient().getCondicionIva().getDescription());
        p.put("P_CLIENT_DOC_TYPE", invoice.getClient().getDocType().getDescription());
        // Comprobante
        p.put("P_INVOICE_TYPE", invoice.getComprobante().getDescription());
        p.put("P_INVOICE_TYPE_CODE", invoice.getComprobante().getLetter());
        p.put("P_POS_NUMBER", invoice.getSellPoint().toString());
        p.put("P_INVOICE_NUMBER", invoice.getNumero());
        p.put("P_INVOICE_DATE", d(invoice.getDate()));
        //        p.put("P_SERVICE_DATE_FROM", d(LocalDate.now()));
        //        p.put("P_SERVICE_DATE_TO", d(LocalDate.now().plusDays(3)));
        //        p.put("P_DUE_DATE", d(LocalDate.now().plusDays(10)));
        // Moneda
        p.put("P_CURRENCY_CODE", invoice.getMoneda().getCode());
        p.put("P_CURRENCY_RATE", "1");
        // CAE / QR / Código de barras (demo)
        p.put("P_CAE", invoice.getCae());
        p.put("P_CAE_DUE_DATE", d(invoice.getVtoCae()));
        //p.put("P_BARCODE", "6112345678901234567890123456789012345678");
        // p.put("P_QR_IMAGE", qr("https://www.afip.gob.ar/")); TODO: Acomodar el QR
        // Totales
        p.put("P_SUBTOTAL_NETO", ivaBreakdown.getInvoiceNetTotal());
        p.put("P_TOTAL_IVA", ivaBreakdown.getIvaTotal());
        p.put("P_TOTAL_TRIBUTOS", bd("0.00"));
        p.put("P_TOTAL_EXENTO", bd("0.00"));
        p.put("P_TOTAL_FINAL", ivaBreakdown.getInvoiceTotal());

        p.put("P_IVA_105_BASE", ivaBreakdown.getIvaMap().get(AfipIva.IVA_105p).getBaseImponible());
        p.put("P_IVA_105_AMOUNT", ivaBreakdown.getIvaMap().get(AfipIva.IVA_105p).getImporteIva());
        p.put("P_IVA_210_BASE", ivaBreakdown.getIvaMap().get(AfipIva.IVA_21p).getBaseImponible());
        p.put("P_IVA_210_AMOUNT", ivaBreakdown.getIvaMap().get(AfipIva.IVA_21p).getImporteIva());
        p.put("P_IVA_270_BASE", ivaBreakdown.getIvaMap().get(AfipIva.IVA_27p).getBaseImponible());
        p.put("P_IVA_270_AMOUNT", ivaBreakdown.getIvaMap().get(AfipIva.IVA_27p).getImporteIva());
        p.put("P_IVA_0_BASE", ivaBreakdown.getIvaMap().get(AfipIva.IVA_0p).getBaseImponible());

        p.put("P_PERC_IIBB_AMOUNT", bd("0.00"));
        p.put("P_PERC_IVA_AMOUNT", bd("0.00"));
        p.put("P_PERC_GANANCIAS_AMOUNT", bd("0.00"));
        p.put("P_IMP_INTERNOS_AMOUNT", bd("0.00"));

        // ===== 4) Llenar y exportar =====
        JRMapCollectionDataSource ds = new JRMapCollectionDataSource(items);
        JasperPrint jp = JasperFillManager.fillReport(getReport(), p, ds);
        return JasperExportManager.exportReportToPdf(jp);
    }

    /**
     * Endpoint de ejemplo: genera datos dummy y devuelve el PDF
     */
    public byte[] generateDemoPdf() throws Exception {
        // ===== 1) Items del detalle (coinciden EXACTO con tus <field> =====
        List<Map<String, ?>> items = new ArrayList<Map<String, ?>>();
        items.add(item("UREA", "kg", bd("150.2500"), bd("1200.00"), bd("0.21")));
        items.add(item("AMONIACO", "L", bd("75.50"), bd("1800.00"), bd("0.105")));
        items.add(item("SERVICIO DE APLICACIÓN", "serv", bd("1"), bd("50000.00"), bd("0")));

        // ===== 2) Totales y desgloses =====
        Totals totals = computeTotals(items);

        // ===== 3) Parámetros (coinciden con tus <parameter>) =====
        Map<String, Object> p = new HashMap<>();
        // Seller
        p.put("P_SELLER_NAME", "TRISQUEL S.R.L.");
        p.put("P_SELLER_ADDRESS", "Av. Siempreviva 742");
        p.put("P_SELLER_CITY", "Rosario");
        p.put("P_SELLER_PROVINCE", "Santa Fe");
        p.put("P_SELLER_POSTAL_CODE", "2000");
        p.put("P_SELLER_CUIT", "30-71740977-5");
        p.put("P_SELLER_IVA_CONDITION", "Responsable Inscripto");
        p.put("P_SELLER_GROSS_INCOME", "CM 902-123456-7");
        p.put("P_SELLER_START_DATE", "01/01/2020");
        p.put("P_SELLER_PHONE", "+54 341 555-0000");
        p.put("P_SELLER_EMAIL", "facturacion@trisquel.ar");
        p.put("P_TRISQUEL_LOGO", loadLogo());

        // Client
        p.put("P_CLIENT_NAME", "AGRO CAMPOS S.A.");
        p.put("P_CLIENT_ADDRESS", "Ruta 33 km 18");
        p.put("P_CLIENT_CITY", "Perez");
        p.put("P_CLIENT_PROVINCE", "Santa Fe");
        p.put("P_CLIENT_POSTAL_CODE", "2121");
        p.put("P_CLIENT_CUIT", "30-12345678-9");
        p.put("P_CLIENT_IVA_CONDITION", "Responsable Inscripto");
        p.put("P_CLIENT_DOC_TYPE", "CUIT");
        p.put("P_CLIENT_DOC_NUMBER", "30-12345678-9");

        // Comprobante
        p.put("P_INVOICE_TYPE", "FACTURA A");
        p.put("P_INVOICE_TYPE_CODE", "A");
        p.put("P_POS_NUMBER", BigDecimal.valueOf(1L));
        p.put("P_INVOICE_NUMBER", "00000012");
        p.put("P_INVOICE_DATE", d(LocalDate.now()));
        p.put("P_SERVICE_DATE_FROM", d(LocalDate.now()));
        p.put("P_SERVICE_DATE_TO", d(LocalDate.now().plusDays(3)));
        p.put("P_DUE_DATE", d(LocalDate.now().plusDays(10)));

        // Moneda
        p.put("P_CURRENCY_CODE", "ARS");
        p.put("P_CURRENCY_RATE", bd("1.0000"));

        // CAE / QR / Código de barras (demo)
        p.put("P_CAE", "73123456789123");
        p.put("P_CAE_DUE_DATE", d(LocalDate.now().plusDays(7)));
        p.put("P_BARCODE", "6112345678901234567890123456789012345678");
        p.put("P_QR_IMAGE", qr("https://www.afip.gob.ar/"));

        // Totales
        p.put("P_SUBTOTAL_NETO", totals.subtotalNeto);
        p.put("P_TOTAL_IVA", totals.totalIva);
        p.put("P_TOTAL_TRIBUTOS", bd("0.00"));
        p.put("P_TOTAL_EXENTO", totals.base0);
        p.put("P_TOTAL_FINAL", totals.totalFinal);

        p.put("P_IVA_105_BASE", totals.base105);
        p.put("P_IVA_105_AMOUNT", totals.iva105);
        p.put("P_IVA_210_BASE", totals.base210);
        p.put("P_IVA_210_AMOUNT", totals.iva210);
        p.put("P_IVA_270_BASE", totals.base270);
        p.put("P_IVA_270_AMOUNT", totals.iva270);
        p.put("P_IVA_0_BASE", totals.base0);

        p.put("P_PERC_IIBB_AMOUNT", bd("0.00"));
        p.put("P_PERC_IVA_AMOUNT", bd("0.00"));
        p.put("P_PERC_GANANCIAS_AMOUNT", bd("0.00"));
        p.put("P_IMP_INTERNOS_AMOUNT", bd("0.00"));

        // ===== 4) Llenar y exportar =====
        JRMapCollectionDataSource ds = new JRMapCollectionDataSource(items);
        JasperPrint jp = JasperFillManager.fillReport(getReport(), p, ds);
        return JasperExportManager.exportReportToPdf(jp);
    }

    // ===== Helpers =====

    private static Map<String, Object> item(String description, String unitMeasure, BigDecimal quantity,
                                            BigDecimal unitPrice, BigDecimal ivaRate) {
        Map<String, Object> m = new HashMap<>();
        m.put("product_code", String.format("%05d", new Random().nextInt(100000)));
        m.put("description", description);
        m.put("unit_measure", unitMeasure);
        m.put("quantity", quantity);
        m.put("unit_price", unitPrice);
        m.put("iva_rate", ivaRate);

        BigDecimal net = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = net.multiply(ivaRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = net.add(iva).setScale(2, RoundingMode.HALF_UP);

        m.put("line_net_amount", net);
        m.put("line_iva_amount", iva);
        m.put("line_total", total);
        return m;
    }

    private static BigDecimal bd(String n) {
        return new BigDecimal(n);
    }

    private static String d(LocalDate d) {
        return String.format("%02d/%02d/%d", d.getDayOfMonth(), d.getMonthValue(), d.getYear());
    }

    private static Image qr(String text) throws WriterException {
        int size = 220;
        BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                img.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return img;
    }

    private static Totals computeTotals(List<Map<String, ?>> items) {
        BigDecimal subtotal = bd("0.00");
        BigDecimal totalIva = bd("0.00");
        BigDecimal base105 = bd("0.00"), base210 = bd("0.00"), base270 = bd("0.00"), base0 = bd("0.00");
        BigDecimal iva105 = bd("0.00"), iva210 = bd("0.00"), iva270 = bd("0.00");

        for (Map<String, ?> it : items) {
            BigDecimal net = (BigDecimal) it.get("line_net_amount");
            BigDecimal iva = (BigDecimal) it.get("line_iva_amount");
            BigDecimal rate = (BigDecimal) it.get("iva_rate");
            subtotal = subtotal.add(net);
            totalIva = totalIva.add(iva);
            if (rate.compareTo(bd("0.105")) == 0) {
                base105 = base105.add(net);
                iva105 = iva105.add(iva);
            } else if (rate.compareTo(bd("0.21")) == 0) {
                base210 = base210.add(net);
                iva210 = iva210.add(iva);
            } else if (rate.compareTo(bd("0.27")) == 0) {
                base270 = base270.add(net);
                iva270 = iva270.add(iva);
            } else if (rate.compareTo(bd("0")) == 0) {
                base0 = base0.add(net);
            }
        }
        Totals t = new Totals();
        t.subtotalNeto = subtotal.setScale(2, RoundingMode.HALF_UP);
        t.totalIva = totalIva.setScale(2, RoundingMode.HALF_UP);
        t.totalFinal = t.subtotalNeto.add(t.totalIva).setScale(2, RoundingMode.HALF_UP);
        t.base105 = base105.setScale(2, RoundingMode.HALF_UP);
        t.base210 = base210.setScale(2, RoundingMode.HALF_UP);
        t.base270 = base270.setScale(2, RoundingMode.HALF_UP);
        t.base0 = base0.setScale(2, RoundingMode.HALF_UP);
        t.iva105 = iva105.setScale(2, RoundingMode.HALF_UP);
        t.iva210 = iva210.setScale(2, RoundingMode.HALF_UP);
        t.iva270 = iva270.setScale(2, RoundingMode.HALF_UP);
        return t;
    }

    private static class Totals {
        BigDecimal subtotalNeto, totalIva, totalFinal;
        BigDecimal base105, base210, base270, base0;
        BigDecimal iva105, iva210, iva270;
    }
}

