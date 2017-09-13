/**
 * 
 */
package com.inacionery.basic.service;

import com.inacionery.basic.domain.Car;

import java.util.List;
import java.util.Optional;

/**
 * @author Inácio Nery
 */
public interface CarService {

    public Car save(Car car);

    public List<Car> findAll();

    public Optional<Car> findById(Long id);

    public void delete(Long id);

}
