package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@NoArgsConstructor

public class KoiType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KoiTypeID", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "TypeName", length = 100)
    private String typeName;

    public KoiType(String typeName) {
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}