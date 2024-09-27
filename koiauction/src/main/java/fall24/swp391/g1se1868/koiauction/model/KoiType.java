package fall24.swp391.g1se1868.koiauction.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "[KoiType]")
public class KoiType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KoiTypeID")
    private Integer koiTypeId;

    @Column(name = "TypeName", length = 100)
    private String typeName;

    // Getters và Setters
    public Integer getKoiTypeId() {
        return koiTypeId;
    }

    public void setKoiTypeId(Integer koiTypeId) {
        this.koiTypeId = koiTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
