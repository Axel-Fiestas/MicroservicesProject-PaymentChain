package com.paymentchain.businessdomain.transaction.controller;

import com.paymentchain.businessdomain.transaction.entities.Transaction;
import com.paymentchain.businessdomain.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping()
    public List<Transaction> getAll(){
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable long id){
        return transactionRepository.findById(id).get();
    }

    @PostMapping()
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction){
        Transaction save =  transactionRepository.save(transaction);
        return ResponseEntity.ok(save);
    }

    aea

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody Transaction inputTransaction){

        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        if(optionalTransaction.isPresent()){
            Transaction newTransaction= optionalTransaction.get();

            newTransaction.setFee(inputTransaction.getFee());
            newTransaction.setAmount(inputTransaction.getAmount());
            newTransaction.setChannel(inputTransaction.getChannel());
            newTransaction.setReference(inputTransaction.getReference());
            newTransaction.setStatus(inputTransaction.getStatus());
            newTransaction.setAccountIban(inputTransaction.getAccountIban());
            newTransaction.setDescription(inputTransaction.getDescription());
            newTransaction.setDateTime(inputTransaction.getDateTime());

            transactionRepository.save(newTransaction);

            return ResponseEntity.ok("Transaction Update Sucessfully");

        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable long id){
        Transaction transactionToDelete= transactionRepository.findById(id).get();
        transactionRepository.delete(transactionToDelete);
        return ResponseEntity.ok().build();
    }

}
