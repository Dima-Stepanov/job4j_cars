package ru.job4j.cars.repository.repcatalog;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.catologmodel.Body;

import java.util.List;

/**
 * 3. Мидл
 * 3.3. Hibernate
 * 3.3.5. Контрольные вопросы
 * 2. Тестовое задание. Hibernate. [#330581]
 * BodyRepository управление справочником кузовов.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 08.06.2022
 */
@Repository
public class BodyRepository implements ICatalog<Body> {
    private final SessionFactory sf;

    public BodyRepository(SessionFactory sf) {
        this.sf = sf;
    }

    /**
     * Найти кузов по id.
     *
     * @param id int
     * @return Body
     */
    @Override
    public Body findById(int id) {
        return tx(session -> session.get(Body.class, id), sf);
    }

    /**
     * Весь справочник body.
     *
     * @return List
     */
    @Override
    public List<Body> findAll() {
        return tx(session -> session.createQuery("from Body").list(), sf);
    }
}
