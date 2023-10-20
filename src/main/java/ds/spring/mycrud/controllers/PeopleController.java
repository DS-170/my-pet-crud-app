package ds.spring.mycrud.controllers;

import ds.spring.mycrud.dao.PersonDAO;
import ds.spring.mycrud.models.Person;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PeopleController {

    private final PersonDAO personDAO;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/people")
    public String index(Model model) {
        model.addAttribute("people", personDAO.showAll());
        return "people/people";
    }

    @GetMapping("/people/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("person", personDAO.showOne(id));
        return "people/show";
    }

    @GetMapping("/people/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping("/people/new")
    public String createPerson(@Valid @ModelAttribute("person") Person person, BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) return "people/new";

        personDAO.addNew(person);
        return "redirect:/people";
    }

    @GetMapping("/people/{id}/edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("person", personDAO.showOne(id));
        return "people/edit";
    }

    @PatchMapping("/people/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) return "people/edit";

        personDAO.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/people/{id}")
    public String delete(@PathVariable("id") Long id) {
        personDAO.delete(id);
        return "redirect:/people";
    }
}
