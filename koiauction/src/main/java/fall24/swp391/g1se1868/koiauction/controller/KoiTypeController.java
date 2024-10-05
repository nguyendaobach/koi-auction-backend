package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiType;
import fall24.swp391.g1se1868.koiauction.service.KoiTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/koi-types")
public class KoiTypeController {

    @Autowired
    private KoiTypeService koiTypeService;

    @GetMapping
    public List<KoiType> getAllKoiTypes() {
        return koiTypeService.getAllKoiTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KoiType> getKoiTypeById(@PathVariable Integer id) {
        return koiTypeService.getKoiTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public KoiType createKoiType(@RequestBody KoiType koiType) {
        return koiTypeService.saveKoiType(koiType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKoiType(@PathVariable Integer id) {
        koiTypeService.deleteKoiType(id);
        return ResponseEntity.noContent().build();
    }
}
