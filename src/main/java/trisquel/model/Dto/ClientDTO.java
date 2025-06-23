package trisquel.model.Dto;

import trisquel.model.Client;

public class ClientDTO {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;

    public ClientDTO(Long id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
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

    public static ClientDTO translateToDTO(Client client) {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(client.getId());
        clientDTO.setAddress(client.getAddress());
        clientDTO.setName(client.getName());
        clientDTO.setPhoneNumber(client.getPhoneNumber());
        return clientDTO;
    }
}
