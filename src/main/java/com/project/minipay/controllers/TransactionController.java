package com.project.minipay.controllers;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import Enums.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.minipay.dtos.TransactionRecordDto;
import com.project.minipay.models.TransactionModel;
import com.project.minipay.repositories.TransactionRepository;
import com.project.minipay.models.UserModel;
import com.project.minipay.services.NotificationService;
import com.project.minipay.services.TransactionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.project.minipay.repositories.UserRepository;

import jakarta.validation.Valid;


@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    NotificationService notificationService;

    @GetMapping( "/transactions" )
    public ResponseEntity< List< TransactionModel > > getAllTransactions() {

        List< TransactionModel > transactionModelList = transactionRepository.findAll();
        if ( !transactionModelList.isEmpty() ) {
            for ( TransactionModel transacion : transactionModelList ) {
                UUID id = transacion.getIdTransaction();
                transacion.add( linkTo( methodOn( TransactionController.class ).getOneTransaction( id ) ).withSelfRel() );
            }
        }
        return ResponseEntity.status( HttpStatus.OK ).body( transactionModelList );
    }


    @GetMapping( "/transaction/{id}" )
    public ResponseEntity< Object > getOneTransaction( @PathVariable( value = "id" ) UUID id ) {

        Optional< TransactionModel > transaction0 = transactionRepository.findById( id );
        if ( transaction0.isEmpty() ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Transaction not found." );
        }
        transaction0.get().add( linkTo( methodOn( TransactionController.class ).getAllTransactions() ).withRel( "user List" ) );
        return ResponseEntity.status( HttpStatus.OK ).body( transaction0.get() );
    }


    @PostMapping( "/transaction" )
    @Transactional
    public ResponseEntity< String > saveTransaction( @RequestBody @Valid TransactionRecordDto transactionRecordDto )
        throws Exception {

        TransactionModel res = new TransactionModel();
        try {
            res = transactionService.doTransaction( transactionRecordDto );
        } catch ( Exception e ) {
            if ( e.getMessage().equals( "Service unavaliable" ) ) {
                return ResponseEntity.status( HttpStatus.BAD_GATEWAY ).body( transactionService.getErrorMsg( e.getMessage() ) );
            } else if ( e.getMessage().equals( "One of The users involved in the transaction Could not Be found!" ) ) {
                return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( transactionService.getErrorMsg( e.getMessage() ) );
            } else if ( e.getMessage().equals( "Business Cannot send money!" ) ) {
                return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( transactionService.getErrorMsg( e.getMessage() ) );
            } else if ( e.getMessage().equals( "not enough balance!" ) ) {
                return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( transactionService.getErrorMsg( e.getMessage() ) );
            } else if ( e.getMessage().equals( "Not Autorized!" ) ) {
                return ResponseEntity.status( HttpStatus.FORBIDDEN ).body( transactionService.getErrorMsg( e.getMessage() ) );
            }
        }
        ObjectWriter ow = new ObjectMapper().registerModule( new JavaTimeModule() ).writer().withDefaultPrettyPrinter();

        String response = ow.writeValueAsString( res );
        return ResponseEntity.status( HttpStatus.CREATED ).body( response );
    }


    @DeleteMapping( "/transaction/{id}" )
    public ResponseEntity< Object > deleteTransaction( @PathVariable( value = "id" ) UUID id ) {

        Optional< TransactionModel > transaction0 = transactionRepository.findById( id );
        if ( transaction0.isEmpty() ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Transaction not found." );
        }
        transactionRepository.delete( transaction0.get() );
        return ResponseEntity.status( HttpStatus.OK ).body( "Transaction deleted successfully." );
    }


    @PutMapping( "/user/{id}" )
    public ResponseEntity< Object > updateTransaction( @PathVariable( value = "id" ) UUID id, @RequestBody @Valid TransactionRecordDto transactionRecordDto ) {

        Optional< TransactionModel > transaction0 = transactionRepository.findById( id );
        if ( transaction0.isEmpty() ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Transaction not found." );
        }
        var transactionModel = transaction0.get();
        BeanUtils.copyProperties( transactionRecordDto, transactionModel );
        return ResponseEntity.status( HttpStatus.OK ).body( transactionRepository.save( transactionModel ) );
    }

}
