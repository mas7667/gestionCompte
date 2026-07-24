package com.gestioncompte.gestion_compte.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @NotBlank
    private String nomComplet;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String motDePasseHash;

    public Client(){

    }

    public Client(String nomComplet, String email, String motDePasseHash){
        this.nomComplet =  nomComplet;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
    }

    public long getId(){
        return id;
    }

    public String getNomComplet(){
        return nomComplet;
    }
    public void setNomComplet( String nomComplet){
        this.nomComplet = nomComplet;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email =  email;
    }

    public String getMotDePasseHash(){
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash){
        this.motDePasseHash =  motDePasseHash;
    }
}
