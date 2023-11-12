package com.project.minipay.repositories;


import java.util.UUID;

import com.project.minipay.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID>{

}
