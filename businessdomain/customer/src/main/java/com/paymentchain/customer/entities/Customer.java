package com.paymentchain.customer.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 *
 */

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String code;
    private String name;
    private String phone;
    private String iban;
    private String address;
    private String surname;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerProduct> products;
/*    @Transient //osea que no vamos a guardarlo en la base de datos*/
    @Transient
    private List<?> transactions;
}
