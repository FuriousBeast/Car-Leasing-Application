package com.nk.cars.repo;

import com.nk.cars.entity.Lease;
import com.nk.cars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LeaseRepository.java
 *
 * @author Nandhakumar N 
 * @module com.nk.cars.repo
 * @created Nov 23, 2024
 */

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long>
{
    List<Lease> findAllByCustomer(User customer);
}
