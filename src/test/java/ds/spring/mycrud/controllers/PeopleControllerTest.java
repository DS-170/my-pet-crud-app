package ds.spring.mycrud.controllers;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ds.spring.mycrud.MycrudApplication.class)
//тут твой main класс
@ActiveProfiles({"test"})//эту аннотацию можно не указывать в нашем случае
public class PeopleControllerTest {

    @Autowired
    PeopleController controller;

    @Test
    @DisplayName("Проверка, метода home")
    void run() {
        String result = controller.home();
        assertEquals("home", result);
    }

    @Test
    @DisplayName("Проверка, метода show")
    void showReturnPerson() {
        //тут вроде работает, но при условие, что в базе уже создавали пёрсона и у него ID = 1.
        //стоит ли в таких случаях создавать в тесте пёрсона и повлияет ли это на реальную ДБ? (
        // При каждом тесте будет добавлятся в ДБ новй Пёрсон?)
        //и еще, тут в модель я никаких атрибутов не добавляю, а получаю, как она поняла что там есть person?
        Long id = 1L;
        Model model = new ExtendedModelMap();
        String result = controller.show(id, model);
        assertNotNull(model.getAttribute("person"));
        assertEquals("people/show", result);
    }

    @Test
    @DisplayName("Проверка метода index")
    void indexReturnList() {
        String result = controller.index(new ExtendedModelMap());
        assertEquals("people/people", result);
    }

    @Test
    @DisplayName("Проверка метода newPerson")
    void newPersonReturnNew() {
        Person person = new Person(2L, "name", 33, "test@test.com");
        assertNotNull(person);
        String result = controller.newPerson(person);
        assertEquals("people/new", result);
    }

    @Test
    @DisplayName("Проверка метода createPerson_valid")
    void createPersonRedirect() {
        Person validPerson = new Person(2L, "John Doe", 25, "john@example.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(validPerson, "person");

        String result = controller.createPerson(validPerson, bindingResult, new ExtendedModelMap());

        assertEquals("redirect:/people", result);
    }

    @Test
    @DisplayName("Проверка метода createPerson_invalid")
    void createPersonReturnNew() {
        Person invalidPerson = new Person();
        BindingResult bindingResult = new BeanPropertyBindingResult(invalidPerson, "person");
        bindingResult.addError(new ObjectError("person", "invalidPerson"));

        String result = controller.createPerson(invalidPerson, bindingResult, new ExtendedModelMap());

        assertEquals("new", result);
    }

    @Test
    @DisplayName("Проверка метода edit")
    void editReturn() {
        Long id = 1L;
        String result = controller.edit(new ExtendedModelMap(), id);
        assertEquals("people/edit", result);
    }

    @Test
    @DisplayName("Проверка метода update_valid")
    void updateValid() {
        Person validPerson = new Person(1L, "name", 33, "t@t.com");
        BindingResult bindingResult = new BeanPropertyBindingResult(validPerson, "validPerson");

        String result = controller.update(validPerson, bindingResult, 1L); //тут тоже идет проверка ID в существующей базе.
        assertEquals("redirect:/people", result);
    }

    @Test
    @DisplayName("Проверка метода update_invalid")
    void updateInvalid() {
        Person invalidPerson = new Person();
        BindingResult bindingResult = new BeanPropertyBindingResult(invalidPerson, "validPerson");
        bindingResult.addError(new ObjectError("person", "InvalidPerson"));

        String result = controller.update(invalidPerson, bindingResult, 2L);
        assertEquals("edit", result);
    }

    @Test
    @DisplayName("Проверка метода delete")
    void deleteRedirect() {
        String result = controller.delete(2l);
        assertEquals("redirect:/people", result);
    }
}
