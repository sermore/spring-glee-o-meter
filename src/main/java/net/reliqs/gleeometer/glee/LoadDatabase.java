package net.reliqs.gleeometer.glee;

import lombok.extern.slf4j.Slf4j;
import net.reliqs.gleeometer.users.User;
import net.reliqs.gleeometer.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Configuration
@Slf4j
public class LoadDatabase {

    private final String[] words = { "one", "two", "three", "rain", "sun", "shine", "table", "run", "child", "parrot" };

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("clientSecret:secret")
    private String clientSecret;

    @Bean
    CommandLineRunner initUsers(UserRepository repo) {

        return args -> {
            for (int i = 0; i < words.length; i++) {
                String email = words[i] + "@" + words[i] + ".com";
                User.Role role = i > 1 ? User.Role.USER : i == 0 ? User.Role.ADMIN : User.Role.USER_MANAGER;
                Double minGleePerDay = Math.random() * 7000;
                String pwd = passwordEncoder.encode("pwd");
                log.info("save {}", repo.save(new User(null, email, pwd, role, minGleePerDay, null)));
            }
        };
    }

    @Bean
    CommandLineRunner initGlee(GleeRepository repo, UserRepository userRepo) {
        return args -> {
            for (int i = 0; i < 100; i++) {
                LocalDate date = LocalDate.ofEpochDay((long) (10_000L + Math.random() * 10_000));
                LocalTime time = LocalTime.ofSecondOfDay((long) (Math.random() * 24 * 3600));
                String text = Arrays.stream(words).filter(w -> Math.random() > 0.5).collect(Collectors.joining(" "));
                Double value = Math.random() * 3600;
                User user = StreamSupport.stream(userRepo.findAll().spliterator(), false).filter(u -> Math.random() > 0.5).findFirst().get();
                log.info("save {}", repo.save(new Glee(null, date, time, text, value, user)));
            }
        };
    }

}
