package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
public class KoiOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CountryID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "Country", length = 100)
    private String country;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}