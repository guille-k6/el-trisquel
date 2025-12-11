package trisquel.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import trisquel.afip.AfipQRBuilder;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceReportService {

    private volatile JasperReport cachedReport;
    private volatile Image cachedLogo;
    private final AfipQRBuilder afipQRBuilder;
    private final Resource invoiceReportResource;
    private final Resource invoiceLogoResource;

    public InvoiceReportService(AfipQRBuilder afipQRBuilder,
                                @Value("${report.invoice.path}") Resource invoiceReportResource,
                                @Value("${report.logo.path}") Resource invoiceLogoResource) {
        this.afipQRBuilder = afipQRBuilder;
        this.invoiceReportResource = invoiceReportResource;
        this.invoiceLogoResource = invoiceLogoResource;
    }

    /**
     * Compila el JRXML 1 sola vez y lo cachea
     */
    private JasperReport getReport() throws Exception {
        if (cachedReport == null) {
            synchronized (this) {
                if (cachedReport == null) {
                    try (InputStream in = invoiceReportResource.getInputStream()) {
                        cachedReport = JasperCompileManager.compileReport(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("No se pudo cargar el template del reporte", e);
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
                    try (InputStream in = invoiceLogoResource.getInputStream()) {
                        cachedLogo = ImageIO.read(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("No se pudo cargar el logo", e);
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
            itemParameters.put("iva_rate", BigDecimal.valueOf(item.getIva().getPercentage() / 100));
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
        p.put("P_POS_NUMBER", addZeroes(invoice.getSellPoint().toString(), 5));
        p.put("P_INVOICE_NUMBER", addZeroes(invoice.getNumero().toString(), 8));
        p.put("P_INVOICE_DATE", d(invoice.getDate()));
        p.put("P_SELL_CONDITION", invoice.getSellCondition().getName());
        // Moneda
        p.put("P_CURRENCY_CODE", invoice.getMoneda().getCode());
        p.put("P_CURRENCY_RATE", "1");
        p.put("P_CAE", invoice.getCae());
        p.put("P_CAE_DUE_DATE", d(invoice.getVtoCae()));
        String qrUrl = afipQRBuilder.buildQrUrl(invoice, orgInfo);
        BufferedImage qrImage = generateQrImage(qrUrl, 220);
        p.put("P_QR_IMAGE", qrImage);
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

    private static BigDecimal bd(String n) {
        return new BigDecimal(n);
    }

    private static String d(LocalDate d) {
        return String.format("%02d/%02d/%d", d.getDayOfMonth(), d.getMonthValue(), d.getYear());
    }

    private String addZeroes(String posNumber, int precision) {
        int zeroesNeeded = precision - posNumber.length();
        for (int i = 0; i < zeroesNeeded; i++) {
            posNumber = "0" + posNumber;
        }
        return posNumber;
    }

    private BufferedImage generateQrImage(String text, int size) {
        try {
            com.google.zxing.qrcode.QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, size, size);
            return com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            throw new RuntimeException("Error generando imagen QR", e);
        }
    }
}

