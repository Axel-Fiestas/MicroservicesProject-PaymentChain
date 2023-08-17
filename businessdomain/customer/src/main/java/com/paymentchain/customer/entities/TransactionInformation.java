package com.paymentchain.customer.entities;

import lombok.Data;

import java.util.Date;
@Data
public class TransactionInformation {
    private Date dateTime;
    private double amount;
    private String description;
    private String status;
    private String channel;
}
