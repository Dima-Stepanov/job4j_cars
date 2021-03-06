package ru.job4j.cars.service;

import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.CarRepository;

/**
 * 3. Мидл
 * 3.3. Hibernate
 * 3.3.5. Контрольные вопросы
 * 2. Тестовое задание. Hibernate. [#330581]
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 08.06.2022
 */
@Service
public class CarService {
    private final CarRepository store;

    public CarService(CarRepository store) {
        this.store = store;
    }

    public boolean create(Car car) {
        return store.created(car);
    }

    public boolean update(int idCar, Car car) {
        return store.update(idCar, car);
    }

    public Car findByIdCar(int idCar) {
        return store.findById(idCar);
    }
}
