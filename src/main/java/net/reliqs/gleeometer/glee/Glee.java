package net.reliqs.gleeometer.glee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.reliqs.gleeometer.users.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Glee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime time;
    @NotEmpty
    private String text;
    @NotNull
    private Double value;
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private User user;
}
