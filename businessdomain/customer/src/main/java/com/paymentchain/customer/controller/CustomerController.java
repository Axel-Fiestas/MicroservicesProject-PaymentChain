package com.paymentchain.customer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.entities.CustomerTransaction;
import com.paymentchain.customer.repository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.persistence.Transient;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public CustomerController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    //webClient requires HttpClient library to work properly
    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE,true)
            .option(EpollChannelOption.TCP_KEEPIDLE,300)
            .option(EpollChannelOption.TCP_KEEPINTVL,60)
            //Response timeout: The maximun time to wait for a response
            .responseTimeout(Duration.ofSeconds(1))
            //Read and Write Timeout: A read timeout occurs when no data was read within a certain
            //period of time, while the write timeout when a write operation cannot finisih at a specific time
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000,TimeUnit.MILLISECONDS));
            })
            ;



    @GetMapping()
    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable long id){
        return customerRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable long id, @RequestBody Customer input){
        Customer find = customerRepository.findById(id).get();

        if(find!=null){
            find.setCode(input.getCode());
            find.setName(input.getName());
            find.setIban(input.getIban());
            find.setPhone(input.getPhone());
            find.setSurname(input.getSurname());
        }
        Customer save = customerRepository.save(find);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input){
        input.getProducts().forEach(element-> element.setCustomer(input));
        input.getTransactions().forEach(element->element.setCustomer(input));
        Customer save = customerRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        Optional<Customer> findById= customerRepository.findById(id);
        if(findById.get()!=null){
            customerRepository.delete(findById.get());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/full")
    public Customer getByCode(@RequestParam String code){
        Customer customer = customerRepository.findByCode(code);
        List<CustomerProduct> products = customer.getProducts();
        products.forEach(x->{
            String productName = getProductName(x.getProductId());
            x.setProductName(productName);
        });

        return customer;
    }

    @GetMapping("/transactionsInformation")
    public Customer getAllTransactionsByAccountIban(@RequestParam String accountIban){
        Customer customer = customerRepository.findByAccount(accountIban);

        List<CustomerTransaction> transactions = customer.getTransactions();
        transactions.forEach(transaction->{
            List<Object>information = getUnitTransactionInformation(transaction.getTransactionId());
            //transaction.setDateTime((Date) information.get(0));
/*            transaction.setAmount((Double)information.get(1));
            transaction.setDescription((String) information.get(2));
            transaction.setStatus((String)information.get(3));
            transaction.setStatus((String) information.get(4));*/
            transaction.setAmount(200);

        });

        return customer;

    }

    private String getProductName(long id){

        WebClient build= webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8083/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url","http://localhost:8083/product"))
                .build();

        JsonNode block= build.method(HttpMethod.GET).uri("/"+id)
                .retrieve().bodyToMono(JsonNode.class).block();

        String name=block.get("name").asText();

        return name;
    }

    //Cuando se llame al cliente se obtienen todas las transacciones del cliente
    //1ero obtener la información de cada transacción unitaria
    private List<Object> getUnitTransactionInformation(long id){
        WebClient build= webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8082/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url","http://localhost:8082/transaction"))
                .build();

        JsonNode block = build.method(HttpMethod.GET).uri("/"+id)
                .retrieve().bodyToMono(JsonNode.class).block();

        String datetime = block.get("dateTime").asText();
        Double amount =block.get("amount").asDouble();
        String description = block.get("description").asText();
        String status = block.get("status").asText();
        String channel = block.get("channel").asText();

        List<Object> transactionInformation= new ArrayList<>();
        transactionInformation.add(datetime);
        transactionInformation.add(amount);
        transactionInformation.add(description);
        transactionInformation.add(status);
        transactionInformation.add(channel);

        return transactionInformation;
    }



}
