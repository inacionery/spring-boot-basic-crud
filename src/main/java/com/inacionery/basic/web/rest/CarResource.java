/**
 * 
 */
package com.inacionery.basic.web.rest;

import com.inacionery.basic.domain.Car;
import com.inacionery.basic.service.CarService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Inácio Nery
 */
@RestController
@RequestMapping("/api")
public class CarResource {

    @Autowired
    private CarService carService;

    @PostMapping("/cars")
    public ResponseEntity<Car> createCar(@RequestBody Car car)
        throws URISyntaxException {

        if (car.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        Car result = carService.save(car);

        return ResponseEntity.created(new URI("/api/cars/" + result.getId()))
            .body(result);
    }

    @PutMapping("/cars")
    public ResponseEntity<Car> updateCar(@RequestBody Car car)
        throws URISyntaxException {

        if (car.getId() == null) {
            return createCar(car);
        }

        Car result = carService.save(car);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/cars")
    public List<Car> getAllCars() {
        return carService.findAll();
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<Car> getCar(@PathVariable Long id) {

        Optional<Car> car = carService.findById(id);

        return car.map(result -> ResponseEntity.ok(result))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {

        carService.delete(id);

        return ResponseEntity.ok().build();
    }
}
