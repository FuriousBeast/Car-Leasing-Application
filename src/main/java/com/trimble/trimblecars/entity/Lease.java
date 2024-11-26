package com.trimble.trimblecars.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Lease.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.entity
 * @created Nov 23, 2024
 */
@Entity
@Data
public class Lease
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    //Automatically sets the state to ACTIVE
    public void setStartDate(LocalDateTime startDate)
    {
        this.startDate = startDate;
        this.state = State.ACTIVE;
    }

    //Automatically sets the state to ENDED
    public void setEndDate(LocalDateTime endDate)
    {
        this.endDate = endDate;
        this.state = State.ENDED;
    }

    private State state;

    @ManyToOne
    @JsonBackReference("user-leases")
    private User customer;

    @JsonProperty("customerDetails")
    private String getCustomerDetails()
    {
        return customer !=null ? customer.getUsername() : null;
    }

    @ManyToOne
    @JsonBackReference("car-lease")
    private Car car;

    @JsonProperty("carDetails")
    private String getCarDetails()
    {
        return car!=null ? car.getMake() + " " + car.getModel() : null;
    }

    @Override
    public String toString() {
        return "Lease{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", state=" + state +
                ", customer=" + getCustomerDetails() +
                ", car=" + getCarDetails() +
                '}';
    }
}
