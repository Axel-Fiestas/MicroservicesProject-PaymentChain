package com.paymentchain.customer.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    private String accountIban;
    @Transient
    private double balance;
    @ManyToOne(fetch = FetchType.LAZY,targetEntity = Customer.class)
    @JoinColumn(name = "customerId",nullable = true)
    private Customer customer;

}
