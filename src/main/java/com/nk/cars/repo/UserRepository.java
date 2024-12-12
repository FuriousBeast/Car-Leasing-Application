package com.nk.cars.repo;

import com.nk.cars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserRepository.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.repo
 * @created Nov 23, 2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    User findUserByUserId(Long userId);

    User findByEmail(String email);

    List<User> findByName(String name);
}
