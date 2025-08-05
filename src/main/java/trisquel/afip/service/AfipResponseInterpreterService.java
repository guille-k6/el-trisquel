package trisquel.afip.service;

import org.springframework.stereotype.Service;
import trisquel.model.InvoiceQueue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AfipResponseInterpreterService {
    public static class CaeResponse {
        private String estado;
        private String reproceso;
        private String cae;
        private LocalDate fechaVencimientoCae;
        private List<String> errores = new ArrayList<>();
        private List<String> observaciones = new ArrayList<>();

        public CaeResponse() {
            this.errores = new ArrayList<>();
            this.observaciones = new ArrayList<>();
        }

        // Getters y setters
        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getCae() {
            return cae;
        }

        public void setCae(String cae) {
            this.cae = cae;
        }

        public LocalDate getFechaVencimientoCae() {
            return fechaVencimientoCae;
        }

        public void setFechaVencimientoCae(LocalDate fechaVencimientoCae) {
            this.fechaVencimientoCae = fechaVencimientoCae;
        }

        public List<String> getErrores() {
            return errores;
        }

        public void setErrores(List<String> errores) {
            this.errores = errores;
        }

        public List<String> getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(List<String> observaciones) {
            this.observaciones = observaciones;
        }

        public boolean isExitoso() {
            return "A".equals(estado) && cae != null && !cae.isEmpty();
        }

        public boolean needsReprocess() {
            return !"N".equals(reproceso);
        }

        public String getReproceso() {
            return reproceso;
        }

        public void setReproceso(String reproceso) {
            this.reproceso = reproceso;
        }

        @Override
        public String toString() {
            return String.format("CaeResponse{estado='%s', reproceso='%s' cae='%s', fechaVencimiento=%s, errores=%d, observaciones=%d}", estado, reproceso, cae, fechaVencimientoCae, errores.size(), observaciones.size());
        }

        public void completeInvoiceQueue(InvoiceQueue invoiceQueue) {
            invoiceQueue.setAfipStatus(estado);
            invoiceQueue.setAfipReprocess(reproceso);
            invoiceQueue.setAfipCae(cae);
            invoiceQueue.setAfipDueDateCae(fechaVencimientoCae);
            invoiceQueue.setErrors(buildStringFromList(this.errores));
            invoiceQueue.setObservations(buildStringFromList(this.observaciones));
        }

        public String buildStringFromList(List<String> list) {
            if (list == null) {
                return "";
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i));
                if (i < list.size() - 1) {
                    stringBuilder.append(" - ");
                }
            }
            return stringBuilder.toString();
        }
    }

    // Excepción personalizada para errores de parseo
    public static class AfipParseException extends Exception {
        public AfipParseException(String mensaje) {
            super(mensaje);
        }

        public AfipParseException(String mensaje, Throwable causa) {
            super(mensaje, causa);
        }
    }

    public CaeResponse parseFecaeFromResponse(String responseBody) {
        String parsedResponse = responseBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        CaeResponse caeResponse = new CaeResponse();

        // Buscar el bloque principal de respuesta
        Pattern fecaeResponsePattern = Pattern.compile("<FECAESolicitarResponse(.*?)</FECAESolicitarResponse>", Pattern.DOTALL);
        Matcher responseMatcher = fecaeResponsePattern.matcher(parsedResponse);
        if (!responseMatcher.find()) {
            caeResponse.setEstado("R");
            caeResponse.getErrores().add("Error en FECAE: No se encontró el bloque <FECAESolicitarResponse>");
            return caeResponse;
        }

        // Buscar el resultado dentro de FECAESolicitarResult
        Pattern resultPattern = Pattern.compile("<FECAESolicitarResult>(.*?)</FECAESolicitarResult>", Pattern.DOTALL);
        Matcher resultMatcher = resultPattern.matcher(parsedResponse);
        if (!resultMatcher.find()) {
            caeResponse.setEstado("R");
            caeResponse.getErrores().add("Error en FECAE: No se encontró el bloque <FECAESolicitarResult>");
            return caeResponse;
        }

        String resultBlock = resultMatcher.group(1);

        // Parsear CAE
        Pattern caePattern = Pattern.compile("<CAE>(.*?)</CAE>");
        Matcher caeMatcher = caePattern.matcher(resultBlock);
        if (caeMatcher.find()) {
            caeResponse.setCae(caeMatcher.group(1));
        }

        // Parsear fecha de vencimiento del CAE
        Pattern caeFchVtoPattern = Pattern.compile("<CAEFchVto>(.*?)</CAEFchVto>");
        Matcher caeFchVtoMatcher = caeFchVtoPattern.matcher(resultBlock);
        if (caeFchVtoMatcher.find()) {
            String dateStr = caeFchVtoMatcher.group(1);
            if (dateStr.length() == 8) {
                try {
                    LocalDate fechaVto = LocalDate.parse(dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8));
                    caeResponse.setFechaVencimientoCae(fechaVto);
                } catch (Exception e) {
                    caeResponse.getObservaciones().add("Error al parsear fecha de vencimiento CAE: " + dateStr);
                }
            }
        }

        // Parsear resultado/estado
        Pattern resultadoPattern = Pattern.compile("<Resultado>(.*?)</Resultado>");
        Matcher resultadoMatcher = resultadoPattern.matcher(resultBlock);
        if (resultadoMatcher.find()) {
            caeResponse.setEstado(resultadoMatcher.group(1));
        }
        Pattern reprocesoPattern = Pattern.compile("<Reproceso>(.*?)</Reproceso>");
        Matcher reprocesoMatcher = reprocesoPattern.matcher(resultBlock);
        if (reprocesoMatcher.find()) {
            caeResponse.setReproceso(reprocesoMatcher.group(1));
        }

        // Parsear errores
        Pattern errorsBlockPattern = Pattern.compile("<Errors>(.*?)</Errors>", Pattern.DOTALL);
        Matcher errorsBlockMatcher = errorsBlockPattern.matcher(resultBlock);
        if (errorsBlockMatcher.find()) {
            String errorsBlock = errorsBlockMatcher.group(1);

            Pattern errPattern = Pattern.compile("<Err>(.*?)</Err>", Pattern.DOTALL);
            Matcher errMatcher = errPattern.matcher(errorsBlock);

            while (errMatcher.find()) {
                String errorBlock = errMatcher.group(1);

                Pattern codePattern = Pattern.compile("<Code>(.*?)</Code>");
                Pattern msgPattern = Pattern.compile("<Msg>(.*?)</Msg>");

                Matcher codeMatcher = codePattern.matcher(errorBlock);
                Matcher msgMatcher = msgPattern.matcher(errorBlock);

                String errorCode = codeMatcher.find() ? codeMatcher.group(1) : "";
                String errorMsg = msgMatcher.find() ? msgMatcher.group(1).trim() : "";

                String fullError = "Código: " + errorCode + " - " + errorMsg;
                caeResponse.getErrores().add(fullError);
            }
        }

        // Parsear observaciones
        Pattern obsBlockPattern = Pattern.compile("<Observaciones>(.*?)</Observaciones>", Pattern.DOTALL);
        Matcher obsBlockMatcher = obsBlockPattern.matcher(resultBlock);
        if (obsBlockMatcher.find()) {
            String obsBlock = obsBlockMatcher.group(1);

            Pattern obsPattern = Pattern.compile("<Obs>(.*?)</Obs>", Pattern.DOTALL);
            Matcher obsMatcher = obsPattern.matcher(obsBlock);

            while (obsMatcher.find()) {
                String obsBlock2 = obsMatcher.group(1);

                Pattern codePattern = Pattern.compile("<Code>(.*?)</Code>");
                Pattern msgPattern = Pattern.compile("<Msg>(.*?)</Msg>");

                Matcher codeMatcher = codePattern.matcher(obsBlock2);
                Matcher msgMatcher = msgPattern.matcher(obsBlock2);

                String obsCode = codeMatcher.find() ? codeMatcher.group(1) : "";
                String obsMsg = msgMatcher.find() ? msgMatcher.group(1).trim() : "";

                String fullObs = "Código: " + obsCode + " - " + obsMsg;
                caeResponse.getObservaciones().add(fullObs);
            }
        }

        // Si no se estableció estado y hay CAE, asumir aprobado
        if (caeResponse.getEstado() == null && caeResponse.getCae() != null && !caeResponse.getCae().isEmpty()) {
            caeResponse.setEstado("A");
        }

        // Si no hay estado definido y hay errores, marcar como rechazado
        if (caeResponse.getEstado() == null && !caeResponse.getErrores().isEmpty()) {
            caeResponse.setEstado("R");
        }

        return caeResponse;
    }

    private CaeResponse parseFecaeFailedResponse(String errorBody) {
        String parsedError = errorBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        CaeResponse caeResponse = new CaeResponse();
        caeResponse.setEstado("R"); // Rechazado por defecto

        // Buscar errores en el formato estándar de SOAP fault
        Pattern soapFaultPattern = Pattern.compile("<faultstring(.*?)</faultstring>", Pattern.DOTALL);
        Matcher soapFaultMatcher = soapFaultPattern.matcher(parsedError);

        if (soapFaultMatcher.find()) {
            String errorMessage = soapFaultMatcher.group(1).replaceAll("<[^>]*>", "").trim();
            caeResponse.getErrores().add("SOAP Fault: " + errorMessage);
            return caeResponse;
        }

        // Buscar errores en la estructura específica de FECAE
        Pattern errorsBlockPattern = Pattern.compile("<Errors>(.*?)</Errors>", Pattern.DOTALL);
        Matcher errorsBlockMatcher = errorsBlockPattern.matcher(parsedError);

        if (errorsBlockMatcher.find()) {
            String errorsBlock = errorsBlockMatcher.group(1);

            Pattern errPattern = Pattern.compile("<Err>(.*?)</Err>", Pattern.DOTALL);
            Matcher errMatcher = errPattern.matcher(errorsBlock);

            while (errMatcher.find()) {
                String errorBlock = errMatcher.group(1);

                Pattern codePattern = Pattern.compile("<Code>(.*?)</Code>");
                Pattern msgPattern = Pattern.compile("<Msg>(.*?)</Msg>");

                Matcher codeMatcher = codePattern.matcher(errorBlock);
                Matcher msgMatcher = msgPattern.matcher(errorBlock);

                String errorCode = codeMatcher.find() ? codeMatcher.group(1) : "";
                String errorMsg = msgMatcher.find() ? msgMatcher.group(1).trim() : "";

                String fullError = "Código: " + errorCode + " - " + errorMsg;
                caeResponse.getErrores().add(fullError);
            }
        } else {
            // Si no se encuentra estructura conocida, agregar el error completo
            caeResponse.getErrores().add("Error no estructurado: " + parsedError);
        }

        return caeResponse;
    }

    private CaeResponse parseFecaeConsultaFromResponse(String responseBody) {
        String parsedResponse = responseBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        CaeResponse caeResponse = new CaeResponse();

        Pattern consultaResponsePattern = Pattern.compile("<FECAEConsultarResponse(.*?)</FECAEConsultarResponse>", Pattern.DOTALL);
        Matcher responseMatcher = consultaResponsePattern.matcher(parsedResponse);
        if (!responseMatcher.find()) {
            caeResponse.setEstado("R");
            caeResponse.getErrores().add("Error en FECAE Consulta: No se encontró el bloque <FECAEConsultarResponse>");
            return caeResponse;
        }

        // Parsear CAE
        Pattern caePattern = Pattern.compile("<CAE>(.*?)</CAE>");
        Matcher caeMatcher = caePattern.matcher(parsedResponse);
        if (caeMatcher.find()) {
            caeResponse.setCae(caeMatcher.group(1));
        }

        // Parsear fecha de vencimiento del CAE
        Pattern caeFchVtoPattern = Pattern.compile("<CAEFchVto>(.*?)</CAEFchVto>");
        Matcher caeFchVtoMatcher = caeFchVtoPattern.matcher(parsedResponse);
        if (caeFchVtoMatcher.find()) {
            String dateStr = caeFchVtoMatcher.group(1);
            if (dateStr.length() == 8) {
                try {
                    LocalDate fechaVto = LocalDate.parse(dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8));
                    caeResponse.setFechaVencimientoCae(fechaVto);
                } catch (Exception e) {
                    caeResponse.getObservaciones().add("Error al parsear fecha de vencimiento CAE: " + dateStr);
                }
            }
        }

        // Parsear estado (en consultas puede venir como EmisionTipo)
        Pattern estadoPattern = Pattern.compile("<EmisionTipo>(.*?)</EmisionTipo>");
        Matcher estadoMatcher = estadoPattern.matcher(parsedResponse);
        if (estadoMatcher.find()) {
            caeResponse.setEstado(estadoMatcher.group(1));
        }

        // Si no se encontró EmisionTipo, buscar otros posibles campos de estado
        if (caeResponse.getEstado() == null) {
            Pattern resultadoPattern = Pattern.compile("<Resultado>(.*?)</Resultado>");
            Matcher resultadoMatcher = resultadoPattern.matcher(parsedResponse);
            if (resultadoMatcher.find()) {
                caeResponse.setEstado(resultadoMatcher.group(1));
            }
        }

        // Parsear observaciones si existen
        Pattern obsPattern = Pattern.compile("<Obs>(.*?)</Obs>", Pattern.DOTALL);
        Matcher obsMatcher = obsPattern.matcher(parsedResponse);
        while (obsMatcher.find()) {
            String obs = obsMatcher.group(1).replaceAll("<[^>]*>", "").trim();
            if (!obs.isEmpty()) {
                caeResponse.getObservaciones().add(obs);
            }
        }

        // Si hay CAE y no hay estado definido, asumir aprobado
        if (caeResponse.getEstado() == null && caeResponse.getCae() != null && !caeResponse.getCae().isEmpty()) {
            caeResponse.setEstado("A");
        }

        return caeResponse;
    }

    public static Long getNumberFECompUltimoAutorizado(String responseBody) {
        String parsedResponse = responseBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        String nroCbteString = null;
        // Buscar el bloque principal de respuesta
        Pattern nroPatter = Pattern.compile("<CbteNro>(.*?)</CbteNro>", Pattern.DOTALL);
        Matcher responseMatcher = nroPatter.matcher(parsedResponse);
        if (responseMatcher.find()) {
            nroCbteString = responseMatcher.group(1);
        }
        if (nroCbteString == null) {
            throw new RuntimeException("No se encontró el último comprobante autorizado.");
        }
        return Long.parseLong(nroCbteString);
    }
}
