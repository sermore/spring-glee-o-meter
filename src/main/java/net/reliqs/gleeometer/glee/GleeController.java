package net.reliqs.gleeometer.glee;

import net.reliqs.gleeometer.errors.EntityNotFoundException;
import net.reliqs.gleeometer.users.User;
import net.reliqs.gleeometer.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/glee")
@Validated
class GleeController {

    private final GleeRepository repository;

    private final UserRepository userRepository;

    GleeController(GleeRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    Page<Glee> all(Pageable pageable, OAuth2Authentication authentication) {
        String auth = (String) authentication.getUserAuthentication().getPrincipal();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.equals(User.Role.USER.name())) {
            User user = userRepository.findByEmail(auth).orElseThrow(() -> new EntityNotFoundException(User.class, "email", auth));
            return repository.findAllByUser(user, pageable);
        }
        return repository.findAll(pageable);
    }

    @GetMapping("/search")
    Page<Glee> search(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(value = "toDate", required = false) LocalDate toDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            @RequestParam(value = "fromTime", required = false) LocalTime fromTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            @RequestParam(value = "toTime", required = false) LocalTime toTime,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "value", required = false) Double cal,
            @RequestParam(value = "userId", required = false) Long userId,
            Pageable pageable, OAuth2Authentication authentication) {
        String auth = (String) authentication.getUserAuthentication().getPrincipal();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.equals(User.Role.USER.name())) {
            User user = userRepository.findByEmail(auth).orElseThrow(() -> new EntityNotFoundException(User.class, "email", auth));
            userId = user.getId();
            return repository.filter(fromDate, toDate, fromTime, toTime, text, cal, userId, pageable);
        }
        return repository.filter(fromDate, toDate, fromTime, toTime, text, cal, userId, pageable);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority('ADMIN') || (returnObject.user == @userRepository.findByEmail(authentication.principal).get())")
    Glee one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, "id", id.toString()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || (@gleeRepository.findById(#id).orElse(new net.reliqs.gleeometer.glee.Glee()).user == @userRepository.findByEmail(authentication.principal).get())")
    void update(@PathVariable Long id, @Valid @RequestBody Glee res) {
        if (repository.existsById(id)) {
            repository.save(res);
        } else {
            throw new EntityNotFoundException(User.class, "id", id.toString());
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') || (#res != null && #res.user.id == @userRepository.findByEmail(authentication.principal).get().id)")
    Glee create(@Valid @RequestBody Glee res) {
        return repository.save(res);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || (@gleeRepository.findById(#id).orElse(new net.reliqs.gleeometer.glee.Glee()).user == @userRepository.findByEmail(authentication.principal).get())")
    void delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new EntityNotFoundException(User.class, "id", id.toString());
        }
    }

}
