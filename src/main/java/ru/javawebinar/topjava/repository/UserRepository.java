package ru.javawebinar.topjava.repository;


import org.springframework.validation.annotation.Validated;
import ru.javawebinar.topjava.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.List;

@Validated(Default.class)
public interface UserRepository {
    // null if not found, when updated
    User save(@NotNull User user);

    // false if not found
    boolean delete(@Min(0) int id);

    // null if not found
    User get(@Min(0) @NotNull int id);

    // null if not found
    User getByEmail(@Email @NotEmpty String email);

    List<User> getAll();

    default User getWithMeals(int id) {
        throw new UnsupportedOperationException();
    }
}