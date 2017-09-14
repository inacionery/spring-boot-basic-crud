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
import com.inacionery.basic.domain.Person;
import com.inacionery.basic.repository.PersonRepository;
import com.inacionery.basic.service.PersonService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.junit.After;
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
public class PersonResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate
        .ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate
        .now(ZoneId.systemDefault());

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPersonMockMvc;

    private Person person;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);

        PersonResource personResource = new PersonResource();

        ReflectionTestUtils.setField(personResource, "personService",
            personService);

        this.restPersonMockMvc = MockMvcBuilders.standaloneSetup(personResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    public static Person createEntity() {
        Person person = new Person();

        person.setName(DEFAULT_NAME);
        person.setBirthDate(DEFAULT_BIRTH_DATE);
        person.setRegistrationNumber(DEFAULT_REGISTRATION_NUMBER);

        return person;
    }

    @Before
    public void initTest() {
        person = createEntity();
    }

    @After
    public void shutDown() {
        personRepository.delete(person);

        personRepository.flush();
    }

    @Test
    @Transactional
    public void createPerson() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();

        restPersonMockMvc
            .perform(
                post("/api/persons").contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(person)))
            .andExpect(status().isCreated());

        List<Person> persons = personRepository.findAll();

        assertThat(persons).hasSize(databaseSizeBeforeCreate + 1);

        person = persons.get(persons.size() - 1);

        assertThat(person.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(person.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(person.getRegistrationNumber())
            .isEqualTo(DEFAULT_REGISTRATION_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPersons() throws Exception {
        person = personRepository.saveAndFlush(person);

        restPersonMockMvc.perform(get("/api/persons?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(
                jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(
                jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].birthDate")
                .value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].registrationNumber")
                .value(hasItem(DEFAULT_REGISTRATION_NUMBER.toString())));
    }

    @Test
    @Transactional
    public void getPerson() throws Exception {
        person = personRepository.saveAndFlush(person);

        restPersonMockMvc.perform(get("/api/persons/{id}", person.getId()))
            .andExpect(status().isOk())
            .andExpect(
                content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(person.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(
                jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.registrationNumber")
                .value(DEFAULT_REGISTRATION_NUMBER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPerson() throws Exception {
        restPersonMockMvc.perform(get("/api/persons/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePerson() throws Exception {
        person = personService.save(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        Person updatedPerson = personRepository.findById(person.getId()).get();

        updatedPerson.setName(UPDATED_NAME);
        updatedPerson.setBirthDate(UPDATED_BIRTH_DATE);
        updatedPerson.setRegistrationNumber(UPDATED_REGISTRATION_NUMBER);

        restPersonMockMvc
            .perform(
                put("/api/persons").contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPerson)))
            .andExpect(status().isOk());

        List<Person> persons = personRepository.findAll();

        assertThat(persons).hasSize(databaseSizeBeforeUpdate);

        Person testPerson = persons.get(persons.size() - 1);

        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testPerson.getRegistrationNumber())
            .isEqualTo(UPDATED_REGISTRATION_NUMBER);
    }

    @Test
    @Transactional
    public void deletePerson() throws Exception {
        personService.save(person);

        int databaseSizeBeforeDelete = personRepository.findAll().size();

        restPersonMockMvc.perform(delete("/api/persons/{id}", person.getId()))
            .andExpect(status().isOk());

        List<Person> persons = personRepository.findAll();

        assertThat(persons).hasSize(databaseSizeBeforeDelete - 1);
    }
}
