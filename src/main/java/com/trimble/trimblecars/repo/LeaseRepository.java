package com.trimble.trimblecars.repo;

import com.trimble.trimblecars.entity.Lease;
import com.trimble.trimblecars.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LeaseRepository.java
 *
 * @author Nandhakumar N 
 * @module com.trimble.trimblecars.repo
 * @created Nov 23, 2024
 */

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long>
{
    List<Lease> findAllByCustomer(User customer);
}
