package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = "/profile/meals", produces = MediaType.APPLICATION_JSON_VALUE)
public class MealUIController extends AbstractMealController {
    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createOrUpdate(
            @RequestParam Integer id,
            @RequestParam String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam Integer calories
    ) {
        Meal meal = new Meal(dateTime, description, calories);
        if (id == null) {
            super.create(meal);
        } else {
            meal.setId(id);
            super.update(meal, SecurityUtil.authUserId());
        }
    }

//    @GetMapping("/filter")
//    public List<MealTo> filter(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime
//    ) {
//        return super.getBetween(startDate, startTime, endDate, endTime);
//    }

    @Override
    @GetMapping("/filter")
    public List<MealTo> getBetween(
            @RequestParam LocalDate startDate,
            @RequestParam LocalTime startTime,
            @RequestParam LocalDate endDate,
            @RequestParam LocalTime endTime) {
        List<MealTo> list = super.getBetween(startDate, startTime, endDate, endTime);
        return list;
    }
}
