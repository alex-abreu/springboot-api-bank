package com.project.minipay.services;


import Enums.UserType;
import com.project.minipay.dtos.TransactionRecordDto;
import com.project.minipay.models.TransactionModel;
import com.project.minipay.models.UserModel;
import com.project.minipay.repositories.TransactionRepository;
import com.project.minipay.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    NotificationService notificationService;

    public void updateBalance( UserModel sender, UserModel receiver, BigDecimal amount ) {

        sender.setBalance( sender.getBalance().subtract( amount ) );
        userRepository.save( sender );

        receiver.setBalance( receiver.getBalance().add( amount ) );
        userRepository.save( receiver );
    }


    public void validateTransaction( Optional< UserModel > sender, Optional< UserModel > receiver, BigDecimal amount )
        throws Exception {

        if ( sender.isEmpty() || receiver.isEmpty() ) {
            throw new Exception( "One of The users involved in the transaction Could not Be found!" );
        } else if ( !UserType.Common.equals( sender.get().getUserType() ) ) {
            throw new Exception( "Business Cannot send money!" );
        } else if ( sender.get().getBalance().compareTo( amount ) < 0 ) {
            throw new Exception( "not enough balance!" );
        } else if ( !authorizeTransaction( sender.get(), amount ) ) {
            throw new Exception( "Not Autorized!" );
        }

    }


    private boolean authorizeTransaction( UserModel sender, BigDecimal amount ) {

        ResponseEntity< Map > response = restTemplate.getForEntity( "https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class );
        String msg = (String) response.getBody().get( "message" );
        if ( response.getStatusCode() == HttpStatus.OK && "Autorizado".equalsIgnoreCase( msg ) )
            return true;

        return false;
    }


    @Transactional
    public TransactionModel doTransaction( TransactionRecordDto transactionRecordDto )
        throws Exception {

        Optional< UserModel > sender = userRepository.findById( transactionRecordDto.sender() );
        Optional< UserModel > receiver = userRepository.findById( transactionRecordDto.receiver() );

        validateTransaction( sender, receiver, transactionRecordDto.amount() );
        updateBalance( sender.get(), receiver.get(), transactionRecordDto.amount() );

        var transactionModel = new TransactionModel();
        BeanUtils.copyProperties( transactionRecordDto, transactionModel );
        transactionModel.setSender( sender.get() );
        transactionModel.setReceiver( receiver.get() );
        transactionModel.setTimeStamp( LocalDateTime.now() );
        TransactionModel res = transactionRepository.save( transactionModel );

        notificationService.sendNotification( sender.get(), "Money has been sent to " + receiver.get().getFirstname() + " " + receiver.get().getLastname() );
        notificationService.sendNotification(
            receiver.get(),
            "Money  received from: " + sender.get().getFirstname() + " " + sender.get().getLastname() + " amount = " + transactionRecordDto.amount() );

        return res;
    }


    public String getErrorMsg( String msg ) {

        return "{\n\"error\": \"" + msg + "\" \n}";
    }
}
