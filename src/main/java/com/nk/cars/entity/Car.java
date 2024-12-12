package com.nk.cars.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nk.cars.utils.CommonUtils;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Car.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.entity
 * @created Nov 23, 2024
 */

@Entity
@Data
public class Car
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;

    private String model;

    private Integer year;

    @Column(unique = true, nullable = false)
    @JsonProperty("carnumber")
    private String licensePlateNumber;

    private Status status = Status.IDLE;

    @JsonBackReference("car-owner")
    @ManyToOne
    private User owner;

    @JsonProperty("ownerName")
    private String getOwnerName()
    {
        return owner !=null ? owner.getUsername() : null;
    }

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonBackReference("car-lease")
    private List<Lease> leases;

    @JsonProperty("leaseIds")
    private List<Long> getLeaseIds()
    {
        return CommonUtils.hasElement(leases) ? leases.stream().map(Lease::getId).toList() : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", licensePlateNumber='" + licensePlateNumber + '\'' +
                ", status=" + status +
                ", ownerName=" + getOwnerName() +
                ", leases=" + getLeaseIds() +
                '}';
    }
}
