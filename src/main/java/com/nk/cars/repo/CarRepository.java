package com.nk.cars.repo;

import com.nk.cars.entity.Car;
import com.nk.cars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CarOwnerRepository.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.repo
 * @created Nov 23, 2024
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long>
{
    boolean existsCarByLicensePlateNumber(String licensePlateNumber);

    List<Car> findAllByOwner(User owner);
}
