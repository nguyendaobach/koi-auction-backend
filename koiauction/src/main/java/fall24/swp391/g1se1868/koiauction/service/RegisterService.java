package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.model.Register;
import fall24.swp391.g1se1868.koiauction.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepository registerRepository;

    public Integer generateOTP() {
        return (int) (Math.random() * 900000) + 100000;
    }

    public Register saveOTP(String email, Integer otp) {
        Register existingRegister = registerRepository.findByEmail(email);

        if (existingRegister != null) {
            registerRepository.delete(existingRegister);
        }
        Register register = new Register();
        register.setOtp(otp);
        register.setEmail(email);
        long expirationTime = Calendar.getInstance().getTimeInMillis() + 5 * 60 * 1000;
        register.setExpirationTime(expirationTime);
        return registerRepository.save(register);
    }


    public boolean isOTPExpired(Long expirationTime) {
        return Calendar.getInstance().getTimeInMillis() > expirationTime;
    }

    public Register getOTPByEmail(String email) {
        return registerRepository.findByEmail(email);
    }
}
