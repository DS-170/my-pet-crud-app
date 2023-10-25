package ds.spring.mycrud.controllers;

import ds.spring.mycrud.models.Person;
import ds.spring.mycrud.repositories.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ds.spring.mycrud.MycrudApplication.class)
//тут твой main класс
@ActiveProfiles({"test"})//эту аннотацию можно не указывать в нашем случае
public class PeopleControllerTest {

    @Autowired
    private PeopleController controller;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @DisplayName("Проверка, метода home")
    void run() {
        String result = controller.home();
        assertEquals("home", result);
    }

    @Test
    @DisplayName("Проверка, метода show")
    void showReturnPerson() {
        Person validPerson = new Person(null, "testShow", 999, "t@t.com");
        Model model = new ExtendedModelMap();

        personRepository.save(validPerson);

        Long id = validPerson.getId();

        String result = controller.show(id, model);

        assertNotNull(model.getAttribute("person"));
        assertEquals(id, ((Person) model.getAttribute("person")).getId());
        assertEquals("people/show", result);

        personRepository.deleteById(id);
    }

    @Test
    @DisplayName("Проверка метода index")
    void indexReturnList() {
        Model model = new ExtendedModelMap();
        model.addAttribute("person");

        String result = controller.index(model);

        assertEquals("people/people", result);
    }

    @Test
    @DisplayName("Проверка метода newPerson")
    void newPersonReturnNew() {
        Person person = new Person(null, "testNewPerson", 999, "t@t.com");
        String result = controller.newPerson(person);

        assertEquals("people/new", result);
    }

    @Test
    @DisplayName("Проверка метода createPerson_valid")
    void createPersonRedirect() {
        Person validPerson = new Person(null, "testCreatePerson_valid", 999, "t@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(validPerson, "person");
        Model model = new ExtendedModelMap();

        String result = controller.createPerson(validPerson, bindingResult, model);

        assertEquals("redirect:/people", result);
        assertTrue(personRepository.existsById(validPerson.getId()));

        controller.delete(validPerson.getId());
    }

    @Test
    @DisplayName("Проверка метода createPerson_invalid")
    void createPersonReturnNew() {
        Person invalidPerson = new Person(999L, "testCreatePerson_invalid", 999, "t@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(invalidPerson, "person");
        bindingResult.addError(new ObjectError("person", "invalidPerson"));
        Model model = new ExtendedModelMap();

        String result = controller.createPerson(invalidPerson, bindingResult, model);

        assertEquals("people/new", result);
        assertFalse(personRepository.existsById(invalidPerson.getId()));
    }

    @Test
    @DisplayName("Проверка метода edit")
    void editReturn() {
        Person person = new Person(null, "testEdit", 999, "t@t.com");
        Model model = new ExtendedModelMap();

        personRepository.save(person);
        Long id = person.getId();

        String result = controller.edit(model, id);

        assertEquals("people/edit", result);
        assertEquals(id, ((Person) model.getAttribute("person")).getId());

        personRepository.deleteById(id);
    }

    @Test
    @DisplayName("Проверка метода update_valid")
    void updateValid() {
        Person validPerson = new Person(null, "testUpdate_valid", 999, "t@t.com");
        personRepository.save(validPerson);

        Person updatedPerson = new Person(null, "changedName", 2, "changed@t.com");

        BindingResult bindingResult = new BeanPropertyBindingResult(validPerson, "validPerson");

        String result = controller.update(updatedPerson, bindingResult, validPerson.getId());

        assertEquals("redirect:/people", result);

        Optional<Person> updateResult = personRepository.findById(validPerson.getId());
        assertTrue(updateResult.isPresent());
        assertEquals(updatedPerson.getName(), updateResult.get().getName());
        assertEquals(updatedPerson.getAge(), updateResult.get().getAge());
        assertEquals(updatedPerson.getEmail(), updateResult.get().getEmail());

        personRepository.deleteById(updateResult.get().getId());
    }

    @Test
    @DisplayName("Проверка метода update_invalid")
    void updateInvalid() {
        Person invalidPerson = new Person(null, "testUpdate_invalid", 999, "t@t.com");
        personRepository.save(invalidPerson);

        Person updatedPerson = new Person(null, "changedName", 2, "changed@t.com");

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidPerson, "invalidPerson");
        bindingResult.addError(new ObjectError("person", "invalidPerson_error"));

        String result = controller.update(updatedPerson, bindingResult, invalidPerson.getId());

        assertEquals("people/edit", result);

        Optional<Person> afterUpdate = personRepository.findById(invalidPerson.getId());
        assertTrue(afterUpdate.isPresent());
        assertEquals(invalidPerson.getName(), afterUpdate.get().getName());
        assertEquals(invalidPerson.getAge(), afterUpdate.get().getAge());
        assertEquals(invalidPerson.getEmail(), afterUpdate.get().getEmail());

        personRepository.deleteById(invalidPerson.getId());
    }

    @Test
    @DisplayName("Проверка метода delete")
    void deleteRedirect() {
        Person person = new Person(null, "testDelete", 999, "t@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(person, "person");

        controller.createPerson(person, bindingResult, new ExtendedModelMap());
        assertTrue(personRepository.existsById(person.getId()));

        String result = controller.delete(person.getId());

        assertFalse(personRepository.existsById(person.getId()));
        assertEquals("redirect:/people", result);
    }
}
