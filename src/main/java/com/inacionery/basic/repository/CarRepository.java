/**
 * 
 */
package com.inacionery.basic.repository;

import com.inacionery.basic.domain.Car;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Inácio Nery
 */
public interface CarRepository extends JpaRepository<Car, Long> {

}
