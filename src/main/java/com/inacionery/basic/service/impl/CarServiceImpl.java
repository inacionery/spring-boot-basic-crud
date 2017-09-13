/**
 * 
 */
package com.inacionery.basic.service.impl;

import com.inacionery.basic.domain.Car;
import com.inacionery.basic.repository.CarRepository;
import com.inacionery.basic.service.CarService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Inácio Nery
 */
@Service
@Transactional
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Transactional(readOnly = true)
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    public void delete(Long id) {
        carRepository.deleteById(id);
    }
}
