package ds.spring.mycrud.dao;

import ds.spring.mycrud.models.Person;
import ds.spring.mycrud.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonDAO {
    private final PersonRepository personRepository;

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public List<Person> showAll() {
        return personRepository.findAll();
    }

    public Person showOne(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public void addNew(Person person) {
        personRepository.save(person);
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, @NotNull Person updatedPerson) {
        personRepository.findById(id).get().setName(updatedPerson.getName());
        personRepository.findById(id).get().setAge(updatedPerson.getAge());
        personRepository.findById(id).get().setEmail(updatedPerson.getEmail());
    }
}
