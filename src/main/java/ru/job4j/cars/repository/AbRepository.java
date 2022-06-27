package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Ab;
import ru.job4j.cars.model.catologmodel.Mark;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 3. Мидл
 * 3.3. Hibernate
 * 3.3.2. Mapping
 * 3.3.3. HQL, Criteria
 * 2. Фильтры для площадок машин [#4745]
 * AbRepository слой persistence модели данны обьявлений "Ab"
 *
 * @author Dmitry Stepanov, user Dima_Nout
 * @since 28.05.2022
 */
@Repository
public class AbRepository implements IRepository<Ab> {
    private static final String HQL_AB = new StringBuilder()
            .append("select ab from Ab ab ")
            .append("join fetch ab.car c ")
            .append("join fetch c.category ca ")
            .append("join fetch c.model mo ")
            .append("join fetch mo.mark ma ")
            .append("join fetch c.year ye ")
            .append("join fetch c.body bo ")
            .append("join fetch c.engine en ")
            .append("join fetch c.transmission tr ")
            .append("join fetch c.color co").toString();

    private final SessionFactory sf;

    public AbRepository(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public boolean created(Ab type) {
        return tx(session -> {
            session.save(type);
            return true;
        }, sf);
    }

    @Override
    public boolean update(int id, Ab type) {
        return false;
    }

    @Override
    public Ab findById(int id) {
        return tx(session -> session.createQuery(HQL_AB + " where ab.id =:abId", Ab.class)
                .setParameter("abId", id).uniqueResult(), sf);
    }

    @Override
    public List<Ab> findAll() {
        return tx(session -> session.createQuery(HQL_AB, Ab.class).list(), sf);
    }

    /**
     * Показать объявления за последний день.
     *
     * @return List
     */
    @Override
    public List<Ab> getLastDay() {
        return tx(session ->
                session.createQuery(HQL_AB + " where ab.created >=: lastDay", Ab.class)
                        .setParameter("lastDay", LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT))
                        .list(), sf
        );
    }

    /**
     * Показать объявление только с фото.
     *
     * @return List
     */
    @Override
    public List<Ab> getWithPhoto() {
        return tx(session ->
                session.createQuery(HQL_AB + " where ca.photo != null", Ab.class)
                        .list(), sf
        );
    }

    /**
     * Показать объявление определенной марки автомобиля.
     *
     * @param mark Mark
     * @return List.
     */
    @Override
    public List<Ab> getWithMark(Mark mark) {
        return tx(session ->
                session.createQuery(HQL_AB + " where mo.mark =: mark", Ab.class)
                        .setParameter("mark", mark)
                        .list(), sf
        );
    }
}