package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.KoiType;
import fall24.swp391.g1se1868.koiauction.repository.KoiTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KoiTypeService {

    @Autowired
    private KoiTypeRepository koiTypeRepository;

    public List<KoiType> getAllKoiTypes() {
        return koiTypeRepository.findAll();
    }

    public Optional<KoiType> getKoiTypeById(Integer id) {
        return koiTypeRepository.findById(id);
    }

    public KoiType saveKoiType(KoiType koiType) {
        return koiTypeRepository.save(koiType);
    }
    public boolean existsById(Integer id) {
        return koiTypeRepository.existsById(id);
    }


    public void deleteKoiType(Integer id) {
        KoiType koiType = koiTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("KoiType with ID " + id + " not found."));
        koiTypeRepository.delete(koiType);
    }

}
