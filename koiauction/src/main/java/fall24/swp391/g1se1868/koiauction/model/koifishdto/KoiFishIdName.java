package fall24.swp391.g1se1868.koiauction.model.koifishdto;

public class KoiFishIdName { //koi active

    private Integer id;
    private String name;

    public KoiFishIdName(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
