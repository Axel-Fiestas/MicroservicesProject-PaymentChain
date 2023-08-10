package com.paymentchain.businessdomain.transaction.controller;

import com.paymentchain.businessdomain.transaction.entities.Transaction;
import com.paymentchain.businessdomain.transaction.repository.TransactionRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

/*        //The amount will not zero
        if(transaction.getAmount()==0)return ResponseEntity.badRequest().build();
        //If the fee is more than zero
        if(transaction.getFee()>0)transaction.setAmount(transaction.getAmount()-transaction.getFee());
        //if the transaction date is later than the current date then will be PENDIENTE, if not then LIQUIDADO
        if(transaction.getDateTime().after(new Date())){
            transaction.setStatus(Transaction.Status.PENDIENTE);
        }else{
            transaction.setStatus(Transaction.Status.LIQUIDADA);
        }*/

        try{
            transactionsValidations(transaction);
            Transaction save =  transactionRepository.save(transaction);
            return ResponseEntity.ok(save);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Invalidad input: "+e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody Transaction inputTransaction){

        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);

        transactionsValidations(inputTransaction);

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

    public void transactionsValidations(Transaction transactionToAnalyze){
        //The amount will not zero
        if(transactionToAnalyze.getAmount()==0.0)
            throw new IllegalArgumentException("Amount cannot be zero");
        //If the fee is more than zero
        if(transactionToAnalyze.getFee()>0)transactionToAnalyze.setAmount(transactionToAnalyze.getAmount()-transactionToAnalyze.getFee());
        //if the transaction date is later than the current date then will be PENDIENTE, if not then LIQUIDADO
        if(transactionToAnalyze.getDateTime().after(new Date())){
            transactionToAnalyze.setStatus(Transaction.Status.PENDIENTE);
        }else{
            transactionToAnalyze.setStatus(Transaction.Status.LIQUIDADA);
        }
    }

}
