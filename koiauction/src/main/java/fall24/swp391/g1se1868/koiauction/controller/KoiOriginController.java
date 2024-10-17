package fall24.swp391.g1se1868.koiauction.controller;

import fall24.swp391.g1se1868.koiauction.model.KoiOrigin;
import fall24.swp391.g1se1868.koiauction.model.KoiType;
import fall24.swp391.g1se1868.koiauction.model.StringResponse;
import fall24.swp391.g1se1868.koiauction.service.KoiOriginService;
import fall24.swp391.g1se1868.koiauction.service.KoiTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/koi-origin")
public class KoiOriginController {

    @Autowired
    private KoiOriginService koiOriginService;

    @GetMapping
    public List<KoiOrigin> getAllKoiTypes() {
        return koiOriginService.getAllKoiTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KoiOrigin> getKoiTypeById(@PathVariable Integer id) {
        return koiOriginService.getKoiOriginById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<KoiOrigin> createKoiType(@RequestParam String name) {
        KoiOrigin koiOrigin = new KoiOrigin(name);
        KoiOrigin savekoiOrigin = koiOriginService.saveKoiType(koiOrigin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savekoiOrigin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StringResponse> deleteKoiType(@PathVariable Integer id) {
        try {
            koiOriginService.deleteKoiType(id);
            return ResponseEntity.ok(new StringResponse("KoiOrigin with ID " + id + " has been successfully deleted."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new StringResponse("KoiOrigin with ID " + id + " not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse("An error occurred while trying to delete KoiOrigin with ID " + id));
        }
    }

}
