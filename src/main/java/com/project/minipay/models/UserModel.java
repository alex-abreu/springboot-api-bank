package com.project.minipay.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.RepresentationModel;

import Enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "TB_USERS")
@Getter
@Setter
public class UserModel extends RepresentationModel<UserModel> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private UUID idUser;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(unique = true)
	@JsonIgnore
	private String document;

	@Column(unique = true)
	private String email;

	@JsonIgnore
	private String password;

	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	private UserType userType;

}
