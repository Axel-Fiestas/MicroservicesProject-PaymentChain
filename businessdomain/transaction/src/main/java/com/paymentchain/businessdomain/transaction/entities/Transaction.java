package com.paymentchain.businessdomain.transaction.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String reference;
    private String accountIban;
    private Date DateTime;
    private double amount;
    private double fee;
    private String description;
    private String status;
    private String channel;
}
