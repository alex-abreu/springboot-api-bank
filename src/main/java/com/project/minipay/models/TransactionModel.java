package com.project.minipay.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_TRANSACTION")
@Getter
@Setter
public class TransactionModel extends RepresentationModel<TransactionModel> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private UUID idTransaction;

	@ManyToOne
	private UserModel sender;

	@ManyToOne
	private UserModel receiver;

	private BigDecimal amount;

	private LocalDateTime timeStamp;
}
