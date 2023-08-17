package com.paymentchain.customer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity

public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long transactionId;
    @Transient
    private Date dateTime;
    @Transient
    private double amount;
    @Transient
    private String description;
    @Transient
    private String status;
    @Transient
    private String channel;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,targetEntity = Customer.class)
    @JoinColumn(name = "customerId",nullable = true)
    private Customer customer;

}
