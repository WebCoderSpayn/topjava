package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;

@Controller
@RequestMapping("/meals")
public class JspMealController {
    @Autowired
    private MealService mealService;

    private final int userId = SecurityUtil.authUserId();

    @GetMapping
    public String getMeals(Model model, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        int userId = SecurityUtil.authUserId();
        List<Meal> mealList;
        if (!parameterMap.isEmpty()) {
            LocalDate startDate = parseLocalDate(parameterMap.get("startDate")[0]);
            LocalDate endDate = parseLocalDate(parameterMap.get("endDate")[0]);
            mealList = mealService.getBetweenInclusive(startDate, endDate, userId);
        } else {
            mealList = mealService.getAll(userId);
        }
        model.addAttribute("meals", MealsUtil.getTos(mealList, MealsUtil.DEFAULT_CALORIES_PER_DAY));
        return "meals";
    }

    @GetMapping("/delete/{id}")
    public String delMeal(@PathVariable String id) {
        int mealId = Integer.parseInt(id);
        mealService.delete(mealId, userId);
        return "redirect:/meals";
    }

    @GetMapping("/update/{id}")
    public String updateMeal(Model model, @PathVariable String id) {
        int mealId = Integer.parseInt(id);
        model.addAttribute("param.action", "update");
        model.addAttribute("meal", mealService.get(mealId, userId));
        return "mealForm";
    }

    @GetMapping("/create")
    public String addMeal(Model model) {
        model.addAttribute("param.action", "create");
        model.addAttribute("meal", new Meal());
        return "mealForm";
    }

    @PostMapping
    public String saveMeal(HttpServletRequest request) {
        if (request.getParameter("id").isEmpty()) {
            Meal meal = new Meal();
            meal.setDescription(request.getParameter("description"));
            meal.setCalories(Integer.parseInt(request.getParameter("calories")));
            meal.setDateTime(LocalDateTime.parse(request.getParameter("dateTime")));
            mealService.create(meal, userId);
        } else {
            Meal meal = mealService.get(Integer.parseInt(request.getParameter("id")), userId);
            meal.setDescription(request.getParameter("description"));
            meal.setCalories(Integer.parseInt(request.getParameter("calories")));
            meal.setDateTime(LocalDateTime.parse(request.getParameter("dateTime")));
            mealService.update(meal, userId);
        }
        return "redirect:/meals";
    }
}
