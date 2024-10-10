package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.KoiOrigin;
import fall24.swp391.g1se1868.koiauction.model.KoiType;
import fall24.swp391.g1se1868.koiauction.repository.KoiOriginRepository;
import fall24.swp391.g1se1868.koiauction.repository.KoiTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KoiOriginService {
    @Autowired
    private KoiOriginRepository koiTypeRepository;

    public List<KoiOrigin> getAllKoiTypes() {
        return koiTypeRepository.findAll();
    }

    public Optional<KoiOrigin> getKoiOriginById(Integer id) {
        return koiTypeRepository.findById(id);
    }

    public KoiOrigin saveKoiType(KoiOrigin koiOrigin) {
        return koiTypeRepository.save(koiOrigin);
    }
    public boolean existsById(Integer id) {
        return koiTypeRepository.existsById(id);
    }

    public void deleteKoiType(Integer id) {
        KoiOrigin koiOrigin = koiTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("KoiOrigin with ID " + id + " not found."));
        koiTypeRepository.delete(koiOrigin);
    }

}
