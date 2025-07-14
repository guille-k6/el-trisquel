package trisquel.model.Dto;

import trisquel.model.Client;

public class ClientDTO {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private Long docType;
    private Long docNumber;
    private String email;

    public ClientDTO(Long id, String name, String address, String phoneNumber, Long docType, Long docNumber, String email) {
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

    public Long getDocType() {
        return docType;
    }

    public void setDocType(Long docType) {
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

    public static ClientDTO translateToDTO(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(client.getId());
        clientDTO.setAddress(client.getAddress());
        clientDTO.setName(client.getName());
        clientDTO.setPhoneNumber(client.getPhoneNumber());
        clientDTO.setDocType(client.getDocType());
        clientDTO.setDocNumber(client.getDocNumber());
        clientDTO.setEmail(client.getEmail());
        return clientDTO;
    }
}
