package ru.job4j.cars.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.model.Ab;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.catologmodel.*;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.servcatalog.ColorService;
import ru.job4j.cars.service.AbService;
import ru.job4j.cars.service.CarService;
import ru.job4j.cars.service.servcatalog.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 3. Мидл
 * 3.3. Hibernate
 * 3.3.5. Контрольные вопросы
 * 2. Тестовое задание. Hibernate. [#330581]
 * AbController контроллер отображение видов объявлений.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 08.06.2022
 */
@Controller
public class AbController implements IController {
    private final AbService abService;
    private final CarService carService;
    private final YearService yearService;
    private final BodyService bodyService;
    private final EngineService engineService;
    private final CategoryService categoryService;
    private final TransmissionService transmissionService;
    private final ColorService colorService;
    private final MarkService markService;

    public AbController(AbService abService, CarService carService, YearService yearService,
                        BodyService bodyService, EngineService engineService, CategoryService categoryService,
                        TransmissionService transmissionService, ColorService colorService, MarkService markService) {
        this.abService = abService;
        this.carService = carService;
        this.yearService = yearService;
        this.bodyService = bodyService;
        this.engineService = engineService;
        this.categoryService = categoryService;
        this.transmissionService = transmissionService;
        this.colorService = colorService;
        this.markService = markService;
    }

    /**
     * Отображение всех объявлений.
     *
     * @param model Model
     * @return index.html
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        model.addAttribute("abAll", abService.findAllAb());
        return "ab/index";
    }

    /**
     * Метод пост сохраняет новое объявление.
     *
     * @param car   Car
     * @param file0 MultipartFile
     * @param file1 MultipartFile
     * @param file2 MultipartFile
     * @return String
     * @throws IOException Exception.
     */
    @PostMapping("/createAb")
    public String create(@ModelAttribute Car car,
                         @RequestParam("file0") MultipartFile file0,
                         @RequestParam("file1") MultipartFile file1,
                         @RequestParam("file2") MultipartFile file2,
                         HttpSession session) throws IOException {
        User user = getUserSession(session);
        car.addPhoto(Photo.of(file0.getBytes()));
        car.addPhoto(Photo.of(file1.getBytes()));
        car.addPhoto(Photo.of(file2.getBytes()));
        car.addDriver(Driver.of(user.getName(), user.getEmail()));
        carService.create(car);
        Car newCar = carService.findByIdCar(car.getId());
        abService.create(Ab.of(newCar, user));
        return "redirect:/";
    }

    /**
     * Вид отображения деталей объявления
     *
     * @param model Model.
     * @param idAb  int
     * @return String
     */
    @GetMapping("/detailAb/{idAb}")
    public String detailAb(Model model, @PathVariable("idAb") int idAb,
                           HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        Optional<Ab> ab = abService.findByIdAb(idAb);
        if (ab.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("ab", ab.get());
        return "ab/detailAb";
    }

    /**
     * Метод пост завершить объявление.
     *
     * @param idAb int
     * @return String
     */
    @PostMapping("/doneAb")
    public String doneAbPost(@RequestParam("id") int idAb) {
        Optional<Ab> ab = abService.findByIdAb(idAb);
        if (ab.isEmpty()) {
            return "redirect:/";
        }
        ab.get().setDone(LocalDateTime.now().withNano(0));
        abService.update(ab.get().getId(), ab.get());
        return "redirect:/detailAb/" + ab.get().getId();
    }

    /**
     * Открытие формы редактирования объявления
     * При этом марку и модель авто изменить нельзя.
     *
     * @param model Model
     * @param idAb  int
     * @return string
     */
    @GetMapping("/editAb")
    public String editAbGet(Model model, @RequestParam("id") int idAb,
                            HttpSession session) {
        User user = getUserSession(session);
        model.addAttribute("user", user);
        Optional<Ab> ab = abService.findByIdAb(idAb);
        if (ab.isEmpty() || user.getId() != ab.get().getUser().getId()) {
            return "redirect:/";
        }
        model.addAttribute("years", yearService.findAllYear());
        model.addAttribute("bodies", bodyService.findAllBody());
        model.addAttribute("engines", engineService.findAllEngine());
        model.addAttribute("transmissions", transmissionService.findAllTransmission());
        model.addAttribute("colors", colorService.findAllColor());
        model.addAttribute("categories", categoryService.findAllCategory());
        model.addAttribute("ab", ab.get());
        return "ab/editAb";
    }

    /**
     * Метод пост сохраняет измененное объявление.
     *
     * @param ab    Ab
     * @param file0 MultipartFile
     * @param file1 MultipartFile
     * @param file2 MultipartFile
     * @return String
     * @throws IOException Exception
     */
    @PostMapping("/editAbPost")
    public String editAbPost(@ModelAttribute Ab ab,
                             @RequestParam("file0") MultipartFile file0,
                             @RequestParam("file1") MultipartFile file1,
                             @RequestParam("file2") MultipartFile file2,
                             HttpSession session) throws IOException {
        User user = getUserSession(session);
        Car updateCar = ab.getCar();
        updateCar.addDriver(Driver.of(user.getName(), user.getEmail()));
        updateCar.addPhoto(Photo.of(file0.getBytes()));
        updateCar.addPhoto(Photo.of(file1.getBytes()));
        updateCar.addPhoto(Photo.of(file2.getBytes()));
        carService.update(updateCar.getId(), updateCar);
        ab.setDone(null);
        abService.update(ab.getId(), ab);
        return "redirect:/detailAb/" + ab.getId();
    }

    /**
     * Отображение авто по выбранной марке.
     *
     * @param markId  Int
     * @param model   Model
     * @param session HttpSession
     * @return String
     */
    @GetMapping("/fIndexMark")
    public String fIndexMark(@RequestParam("mark.id") int markId, Model model, HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        Optional<Mark> mark = markService.findByIdMark(markId);
        if (mark.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("abFilter", abService.getWithMark(mark.get()));
        return "ab/fIndex";
    }

    /**
     * Отображение авто по выбранной категории.
     *
     * @param catId   Int
     * @param model   Model
     * @param session HttpSession
     * @return String
     */
    @GetMapping("/fIndexCategory")
    public String fIndexCategory(@RequestParam("category.id") int catId, Model model, HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        Optional<Category> category = categoryService.findByIdCategory(catId);
        if (category.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("abFilter", abService.getWithCategory(category.get()));
        return "ab/fIndex";
    }

    /**
     * Отображение авто по выбранному кузову.
     *
     * @param bodyId  Int
     * @param model   Model
     * @param session HttpSession
     * @return String
     */
    @GetMapping("/fIndexBody")
    public String fIndexBody(@RequestParam("body.id") int bodyId, Model model, HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        Optional<Body> body = bodyService.findByIdBody(bodyId);
        if (body.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("abFilter", abService.getWithBody(body.get()));
        return "ab/fIndex";
    }


    /**
     * Отображение авто Активных не проданных.
     *
     * @param model   Model
     * @param session HttpSession
     * @return String
     */
    @GetMapping("/fIndexActive")
    public String fIndexActive(Model model, HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        model.addAttribute("abFilter", abService.getWithActive());
        return "ab/fIndex";
    }

    /**
     * Отображение объявления опубликованное сегодня.
     *
     * @param model   Model
     * @param session HttpSession
     * @return String
     */
    @GetMapping("/fIndexLastDay")
    public String fIndexLastDay(Model model, HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        model.addAttribute("abFilter", abService.getWithLatDay());
        return "ab/fIndex";
    }
}
