package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiType;
import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.service.KoiTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<KoiType> createKoiType(@RequestParam String name) {
        KoiType koiType = new KoiType(name);
        KoiType savedKoiType = koiTypeService.saveKoiType(koiType);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedKoiType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StringResponse> deleteKoiType(@PathVariable Integer id) {
        try {
            koiTypeService.deleteKoiType(id);
            return ResponseEntity.ok(new StringResponse("KoiType with ID " + id + " has been successfully deleted."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StringResponse("KoiType with ID " + id + " not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse("An error occurred while trying to delete KoiType with ID " + id));
        }
    }


}
