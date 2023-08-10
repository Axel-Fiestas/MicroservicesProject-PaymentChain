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

    public enum Status{

        PENDIENTE("01"),
        LIQUIDADA("02"),
        RECHAZADA("03"),
        CANCELADA("04");

        private final String codeStatus;

        Status(String codeStatus){
            this.codeStatus=codeStatus;
        }
        private String getCodeStatus(){
            return codeStatus;
        }

        private String getStatusByCode(String codeStatus){
            switch (codeStatus){
                case "01":
                    return Status.PENDIENTE.name();
                case "02":
                    return Status.LIQUIDADA.name();
                case "03":
                    return Status.RECHAZADA.name();
                case "04":
                    return Status.CANCELADA.name();
                default:
                    return "00";
            }
        }
    }

    enum Channel{
        WEB,CAJERO,OFICINA;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String reference;
    private String accountIban;
    private Date DateTime;
    private double amount;
    private double fee;
    private String description;
    private Status status;
    private Channel channel;
}
