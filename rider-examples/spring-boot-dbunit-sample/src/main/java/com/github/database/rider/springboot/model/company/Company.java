package com.github.database.rider.springboot.model.company;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "company")
public class Company {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;


    public Company() {
    }

    public Company(long id) {
        this.id = id;
    }

    public Company(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company user = (Company) o;
        return Objects.equals(id, user.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
