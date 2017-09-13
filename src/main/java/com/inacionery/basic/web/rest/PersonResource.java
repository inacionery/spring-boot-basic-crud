/**
 * 
 */
package com.inacionery.basic.web.rest;

import com.inacionery.basic.domain.Person;
import com.inacionery.basic.service.PersonService;

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
public class PersonResource {

    @Autowired
    private PersonService personService;

    @PostMapping("/persons")
    public ResponseEntity<Person> createPerson(@RequestBody Person person)
        throws URISyntaxException {

        if (person.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        Person result = personService.save(person);

        return ResponseEntity.created(new URI("/api/persons/" + result.getId()))
            .body(result);
    }

    @PutMapping("/persons")
    public ResponseEntity<Person> updatePerson(@RequestBody Person person)
        throws URISyntaxException {

        if (person.getId() == null) {
            return createPerson(person);
        }

        Person result = personService.save(person);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/persons")
    public List<Person> getAllPersons() {
        return personService.findAll();
    }

    @GetMapping("/persons/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable Long id) {

        Optional<Person> person = personService.findById(id);

        return person.map(result -> ResponseEntity.ok(result))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/persons/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {

        personService.delete(id);

        return ResponseEntity.ok().build();
    }
}
