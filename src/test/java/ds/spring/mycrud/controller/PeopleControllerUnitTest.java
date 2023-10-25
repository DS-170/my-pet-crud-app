package ds.spring.mycrud.controller;

import ds.spring.mycrud.controllers.PeopleController;
import ds.spring.mycrud.dao.PersonDAO;
import ds.spring.mycrud.models.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PeopleControllerUnitTest {
    public static final List<Person> EXPECTED_LIST = List.of(new Person(1L, "name", 22, "mail@mail.ru"));
    PersonDAO personDAOmock = mock(PersonDAO.class);//нам не нужно в этом тесте работать с базой. поэтому базу будем мокать(имитировать)
    PeopleController controller = new PeopleController(personDAOmock);//прям тут и создаем. Именно по этому нам нужен RequiredArgsConstructor в PeopleController , а не @Autowired поле.

    @BeforeEach //этот метод будет выполняться перед каждым тестом
    public void before(){
        when(personDAOmock.showAll())//если будет вызываться showAll, возвращать список EXPECTED_LIST
                .thenReturn(EXPECTED_LIST);
    }

    @Test
    public void indexTest(){
        ConcurrentModel model = new ConcurrentModel();
        String template = controller.index(model);
        assertEquals("people/people", template);
        assertEquals(EXPECTED_LIST, model.getAttribute("people"));
    }

}
