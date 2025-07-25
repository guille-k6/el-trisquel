package trisquel.model;

import jakarta.persistence.*;
import trisquel.afip.model.AfipCondicionIva;
import trisquel.afip.model.AfipTipoDoc;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private AfipTipoDoc docType;
    private Long docNumber;
    private String email;
    @Column(name = "iva_condition")
    private AfipCondicionIva condicionIva;

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

    public AfipTipoDoc getDocType() {
        return docType;
    }

    public void setDocType(AfipTipoDoc docType) {
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

    public AfipCondicionIva getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(AfipCondicionIva condicionIva) {
        this.condicionIva = condicionIva;
    }

}