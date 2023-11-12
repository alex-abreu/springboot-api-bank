package com.project.minipay.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.minipay.dtos.UserDto;
import com.project.minipay.models.UserModel;
import com.project.minipay.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class UserController {


	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/users")
	public ResponseEntity<List<UserModel>> getAllUsers(){
		List<UserModel> userModelList = userRepository.findAll();
		if(!userModelList.isEmpty()) {
			for(UserModel user : userModelList) {
				UUID id = user.getIdUser();
				user.add(linkTo(methodOn(UserController.class).getOneUser(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(userModelList);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<Object> getOneUser(@PathVariable(value="id") UUID id){
		Optional<UserModel> user0 = userRepository.findById(id);
		if(user0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found.");
		}
		user0.get().add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("user List"));
		return ResponseEntity.status(HttpStatus.OK).body(user0.get());
	}
	
	@PostMapping("/user")
	public ResponseEntity<UserModel> saveUser(@RequestBody @Valid UserDto userRecordDto) {
		var userModel = new UserModel();
		BeanUtils.copyProperties(userRecordDto, userModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(userModel));
	}
	
	@DeleteMapping("/user/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable(value="id") UUID id) {
		Optional<UserModel> user0 = userRepository.findById(id);
		if(user0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		userRepository.delete(user0.get());
		return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
	}
	
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable(value="id") UUID id,
													  @RequestBody @Valid UserDto userDto) {
		Optional<UserModel> user0 = userRepository.findById(id);
		if(user0.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		var userModel = user0.get();
		BeanUtils.copyProperties(userDto, userModel);
		return ResponseEntity.status(HttpStatus.OK).body(userRepository.save(userModel));
	}

}
