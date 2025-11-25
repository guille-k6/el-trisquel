package trisquel.afip.config;

public class AfipURLs {
    public static final String WsaaURL = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";
    public static final String fecaeUrl = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    public static final String ultCompAuthUrl = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx?op=FECompUltimoAutorizado";
    // Soap actions
    public static final String fecaeSolicitarAction = "http://ar.gov.afip.dif.FEV1/FECAESolicitar";
    public static final String FECompUltimoAutorizadoAction = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";

}
