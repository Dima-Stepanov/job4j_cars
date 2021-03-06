package ru.job4j.cars.model;

import ru.job4j.cars.model.catologmodel.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * 3. Мидл
 * 3.3. Hibernate
 * 3.3.2. Mapping
 * Модели и связи. Машины и владельцы [#4744]
 * Car модель данных описиывает автомобиль и его характеристики.
 *
 * @author Dmitry Stepanov, user Dima_Nout
 * @since 27.05.2022
 */
@Entity
@Table(name = "cars")
public class Car implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private int id;
    @Column(name = "vin", nullable = false)
    private String vin;
    @Column(name = "car_price")
    private double price;
    @Column(name = "car_mileage")
    private int mileage;
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", foreignKey = @ForeignKey(name = "CATEGORY_ID_FK"), nullable = false)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", foreignKey = @ForeignKey(name = "MODEL_ID_FK"))
    private Model model;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id", foreignKey = @ForeignKey(name = "YEAR_ID_FK"), nullable = false)
    private Year year;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_id", foreignKey = @ForeignKey(name = "BODY_ID_FK"), nullable = false)
    private Body body;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", foreignKey = @ForeignKey(name = "ENGINE_ID_FK"), nullable = false)
    private Engine engine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trans_id", foreignKey = @ForeignKey(name = "TRANS_ID_FK"), nullable = false)
    private Transmission transmission;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", foreignKey = @ForeignKey(name = "COLOR_ID_KEY"), nullable = false)
    private Color color;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "history_owner", joinColumns = {
            @JoinColumn(name = "car_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "driver_id", nullable = false, updatable = false)})
    private final Set<Driver> drivers = new HashSet<>();

    public static Car of(String vin, double price, int mileage,
                         Category category, Model model, Year year,
                         Body body, Engine engine, Transmission transmission,
                         Color color, String description) {
        Car car = new Car();
        car.vin = vin;
        car.price = price;
        car.mileage = mileage;
        car.category = category;
        car.model = model;
        car.year = year;
        car.body = body;
        car.engine = engine;
        car.transmission = transmission;
        car.color = color;
        car.description = description;
        return car;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Photo> getPhoto() {
        return photos;
    }

    public void setPhoto(List<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void addDriver(Driver driver) {
        this.drivers.add(driver);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return id == car.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Car{id=" + id + ", vin='" + vin + '\'' + ", price=" + price + ", mileage=" + mileage
                + ", description='" + description + '\'' + ", photos=" + photos + ", category=" + category
                + ", model=" + model + ", year=" + year + ", body=" + body + ", engine=" + engine
                + ", transmission=" + transmission + ", color=" + color + ", drivers=" + drivers + '}';
    }
}
