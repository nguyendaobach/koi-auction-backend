package fall24.swp391.g1se1868.koiauction.model;
import jakarta.persistence.*;

@Entity
@Table(name = "System")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // id là kiểu Integer

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double value;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

