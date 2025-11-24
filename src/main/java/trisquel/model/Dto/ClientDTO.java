package trisquel.model.Dto;

import trisquel.afip.model.DTO.AfipCondicionIvaDTO;
import trisquel.afip.model.DTO.AfipTipoDocDTO;
import trisquel.model.Client;

import java.util.List;
import java.util.stream.Collectors;

public class ClientDTO {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private AfipTipoDocDTO docType;
    private Long docNumber;
    private String email;
    private AfipCondicionIvaDTO condicionIva;
    private String commercialAddress;

    public ClientDTO(Long id, String name, String address, String phoneNumber, AfipTipoDocDTO docType, Long docNumber,
                     String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.docType = docType;
        this.docNumber = docNumber;
        this.email = email;
    }

    public ClientDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AfipTipoDocDTO getDocType() {
        return docType;
    }

    public void setDocType(AfipTipoDocDTO docType) {
        this.docType = docType;
    }

    public Long getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(Long docNumber) {
        this.docNumber = docNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AfipCondicionIvaDTO getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(AfipCondicionIvaDTO condicionIva) {
        this.condicionIva = condicionIva;
    }

    public String getCommercialAddress() {
        return commercialAddress;
    }

    public void setCommercialAddress(String commercialAddress) {
        this.commercialAddress = commercialAddress;
    }

    public static ClientDTO translateToDTO(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(client.getId());
        clientDTO.setAddress(client.getAddress());
        clientDTO.setName(client.getName());
        clientDTO.setPhoneNumber(client.getPhoneNumber());
        clientDTO.setDocType(AfipTipoDocDTO.fromEnum(client.getDocType()));
        clientDTO.setDocNumber(client.getDocNumber());
        clientDTO.setEmail(client.getEmail());
        clientDTO.setCondicionIva(AfipCondicionIvaDTO.fromEnum(client.getCondicionIva()));
        clientDTO.setCommercialAddress(client.getCommercialAddress());
        return clientDTO;
    }

    public static List<ClientDTO> translateToDTOs(List<Client> clients) {
        return clients.stream().map(ClientDTO::translateToDTO).collect(Collectors.toList());
    }
}
