package com.project.minipay.repositories;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.minipay.models.TransactionModel;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, UUID>{

}
