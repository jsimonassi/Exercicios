/*
 * Classe: User
 *
 * Descrição: Classe que representa o objeto "Usuário" da aplicação
 */



package com.simonassi.exercicios;

public class User {

    private String name, photo, email, password;
    private Float weight;

    public User(String name, String photo, String email, String password, float weight) {
        this.name = name;
        this.photo = photo;
        this.email = email;
        this.password = password;
        this.weight = weight;
    }

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }
}
