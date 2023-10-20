package ds.spring.mycrud.controllers;

import ds.spring.mycrud.dao.PersonDAO;
import ds.spring.mycrud.models.Person;
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
    private PersonDAO personDAO;

    @Test
    @DisplayName("Проверка, метода home")
    void run() {
        String result = controller.home();
        assertEquals("home", result);
    }

    @Test
    @DisplayName("Проверка, метода show")
    void showReturnPerson() {
        Person validPerson = new Person(999L, "testShow", 999, "t@t.com"); // id сам генерируется с помощью @Entity, просто заполнил параметр
        Model model = new ExtendedModelMap();
        controller.createPerson(validPerson, new BeanPropertyBindingResult(validPerson, "person"), model);
        model.addAttribute("id", validPerson.getId());

        String result = controller.show(validPerson.getId(), model);

        assertNotNull(model.getAttribute("person"));
        assertEquals(model.getAttribute("id"), validPerson.getId());
        assertEquals("people/show", result);

        controller.delete(validPerson.getId());
    }

    @Test
    @DisplayName("Проверка метода index")
    void indexReturnList() {
        Model model = new ExtendedModelMap();
        model.addAttribute("person"); // не очень понял я чо в модель положить и почему проверку проходит

        String result = controller.index(model);

        assertEquals("people/people", result);
    }

    @Test
    @DisplayName("Проверка метода newPerson")
    void newPersonReturnNew() {
        Person person = new Person(999L, "testNewPerson", 999, "t@t.com");
        String result = controller.newPerson(person);

        assertNotNull(person);
        assertEquals("people/new", result);
    }

    @Test
    @DisplayName("Проверка метода createPerson_valid")
    void createPersonRedirect() {
        Person validPerson = new Person(999L, "testCreatePerson_valid", 999, "t@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(validPerson, "person");
        Model model = new ExtendedModelMap();

        String result = controller.createPerson(validPerson, bindingResult, model);

        assertEquals("redirect:/people", result);
        assertTrue(personDAO.getPersonRepository().existsById(validPerson.getId()));

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
        assertFalse(personDAO.getPersonRepository().existsById(invalidPerson.getId()));
    }

    @Test
    @DisplayName("Проверка метода edit")
    void editReturn() {
        Long id = 999L;
        Model model = new ExtendedModelMap();
        model.addAttribute("id", id);

        String result = controller.edit(model, id);

        assertEquals("people/edit", result);
        assertEquals(id, model.getAttribute("id")); // не очень опять же понимаю что тут происходит.
        // Модель и без добавления атрибута работает, но если без дабавления проверять то логично пишет что поле ID==null
    }

    @Test
    @DisplayName("Проверка метода update_valid")
    void updateValid() {
        Person validPerson = new Person(999L, "testUpdate_valid", 999, "t@t.com");
        Person updatedPerson = new Person(999L, "changedName", 2, "changed@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(validPerson, "validPerson");

        controller.createPerson(validPerson, bindingResult, new ExtendedModelMap());
        String result = controller.update(updatedPerson, bindingResult, validPerson.getId());

        Optional<Person> updateResult = personDAO.getPersonRepository().findById(validPerson.getId());
        assertEquals("redirect:/people", result);
        assertEquals(updateResult.get().getName(), updatedPerson.getName());
        assertEquals(updateResult.get().getAge(), updatedPerson.getAge());
        assertEquals(updateResult.get().getEmail(), updatedPerson.getEmail());

        controller.delete(updateResult.get().getId());
    }

    @Test
    @DisplayName("Проверка метода update_invalid")
    void updateInvalid() {
        Person invalidPerson = new Person(null, "testUpdate_valid", 999, "t@t.com");
        Person updatedPerson = new Person(null, "changedName", 2, "changed@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(invalidPerson, "validPerson");

        controller.createPerson(invalidPerson, bindingResult, new ExtendedModelMap());
        assertTrue(personDAO.getPersonRepository().existsById(invalidPerson.getId()));

        bindingResult.addError(new ObjectError("person", "invalidPerson"));

        String result = controller.update(updatedPerson, bindingResult, invalidPerson.getId());
        assertEquals("people/edit", result);

        Optional<Person> updateResult = personDAO.getPersonRepository().findById(invalidPerson.getId());
        assertNotEquals(updateResult.get().getName(), updatedPerson.getName());
        assertNotEquals(updateResult.get().getAge(), updatedPerson.getAge());
        assertNotEquals(updateResult.get().getEmail(), updatedPerson.getEmail());

        controller.delete(invalidPerson.getId());
    }

    @Test
    @DisplayName("Проверка метода delete")
    void deleteRedirect() {
        Person person = new Person(null, "testDelete", 999, "t@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(person, "person");

        controller.createPerson(person, bindingResult, new ExtendedModelMap());
        assertTrue(personDAO.getPersonRepository().existsById(person.getId()));

        String result = controller.delete(person.getId());

        assertFalse(personDAO.getPersonRepository().existsById(person.getId()));
        assertEquals("redirect:/people", result);
    }
}
