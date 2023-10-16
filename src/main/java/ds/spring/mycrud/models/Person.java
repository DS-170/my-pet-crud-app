package ds.spring.mycrud.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;;

@Entity
@Table(name = "Person1")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "name should be not empty")
    @Size(min = 2,max = 50,message = "name should be between 2 and 30 digits")
    private String name;

    @Min(value = 1, message = "age should be greater then 0")
    private int age;

    @NotBlank(message = "email should be not empty")
    @Email
    private String email;
}
