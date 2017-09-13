/**
 * 
 */
package com.inacionery.basic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.inacionery.basic.BasicApplication;
import com.inacionery.basic.domain.Car;
import com.inacionery.basic.repository.CarRepository;
import com.inacionery.basic.service.CarService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Inácio Nery
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BasicApplication.class)
public class CarResourceIntTest {

    private static final String DEFAULT_MANUFACTURER = "AAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBB";

    private static final String DEFAULT_MODEL = "AAAAA";
    private static final String UPDATED_MODEL = "BBBBB";

    private static final LocalDate DEFAULT_YEAR = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_YEAR = LocalDate
        .now(ZoneId.systemDefault());

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCarMockMvc;

    private Car car;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CarResource carResource = new CarResource();
        ReflectionTestUtils.setField(carResource, "carService", carService);
        this.restCarMockMvc = MockMvcBuilders.standaloneSetup(carResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Car createEntity() {
        Car car = new Car();

        car.setManufacturer(DEFAULT_MANUFACTURER);
        car.setModel(DEFAULT_MODEL);
        car.setYear(DEFAULT_YEAR);

        return car;
    }

    @Before
    public void initTest() {
        car = createEntity();
    }

    @Test
    @Transactional
    public void createCar() throws Exception {
        int databaseSizeBeforeCreate = carRepository.findAll().size();

        restCarMockMvc
            .perform(
                post("/api/cars").contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(car)))
            .andExpect(status().isCreated());

        List<Car> cars = carRepository.findAll();

        assertThat(cars).hasSize(databaseSizeBeforeCreate + 1);

        Car testCar = cars.get(cars.size() - 1);

        assertThat(testCar.getManufacturer()).isEqualTo(DEFAULT_MANUFACTURER);
        assertThat(testCar.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testCar.getYear()).isEqualTo(DEFAULT_YEAR);
    }

    @Test
    @Transactional
    public void getAllCars() throws Exception {

        carRepository.saveAndFlush(car);

        restCarMockMvc.perform(get("/api/cars?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
            .andExpect(jsonPath("$.[*].manufacturer")
                .value(hasItem(DEFAULT_MANUFACTURER.toString())))
            .andExpect(jsonPath("$.[*].model")
                .value(hasItem(DEFAULT_MODEL.toString())))
            .andExpect(
                jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR.toString())));
    }

    @Test
    @Transactional
    public void getCar() throws Exception {

        carRepository.saveAndFlush(car);

        restCarMockMvc.perform(get("/api/cars/{id}", car.getId()))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(car.getId().intValue()))
            .andExpect(jsonPath("$.manufacturer")
                .value(DEFAULT_MANUFACTURER.toString()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL.toString()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCar() throws Exception {

        restCarMockMvc.perform(get("/api/cars/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCar() throws Exception {

        carService.save(car);

        int databaseSizeBeforeUpdate = carRepository.findAll().size();

        Car updatedCar = carRepository.findById(car.getId()).get();

        updatedCar.setManufacturer(UPDATED_MANUFACTURER);
        updatedCar.setModel(UPDATED_MODEL);
        updatedCar.setYear(UPDATED_YEAR);

        restCarMockMvc
            .perform(
                put("/api/cars").contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCar)))
            .andExpect(status().isOk());

        List<Car> cars = carRepository.findAll();

        assertThat(cars).hasSize(databaseSizeBeforeUpdate);

        Car testCar = cars.get(cars.size() - 1);

        assertThat(testCar.getManufacturer()).isEqualTo(UPDATED_MANUFACTURER);
        assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testCar.getYear()).isEqualTo(UPDATED_YEAR);
    }

    @Test
    @Transactional
    public void deleteCar() throws Exception {

        carService.save(car);

        int databaseSizeBeforeDelete = carRepository.findAll().size();

        restCarMockMvc.perform(delete("/api/cars/{id}", car.getId()))
            .andExpect(status().isOk());

        List<Car> cars = carRepository.findAll();

        assertThat(cars).hasSize(databaseSizeBeforeDelete - 1);
    }
}
