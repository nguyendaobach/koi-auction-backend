package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.repository.TransactionRepository;
import fall24.swp391.g1se1868.koiauction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Integer> getUsersPerMonth() {
        Map<String, Integer> usersPerMonth = new HashMap<>();

        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();

        // Vòng lặp từ tháng 1 đến tháng hiện tại
        for (int month = 1; month <= today.getMonthValue(); month++) {
            // Tính toán thời gian bắt đầu của tháng
            LocalDateTime startOfMonth = LocalDateTime.of(today.getYear(), month, 1, 0, 0);
            Instant startInstant = startOfMonth.toInstant(ZoneOffset.UTC);

            // Tính toán thời gian kết thúc của tháng (cuối ngày cuối cùng của tháng)
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
            Instant endInstant = endOfMonth.toInstant(ZoneOffset.UTC);

            // Đếm số lượng người dùng đăng ký trong mỗi tháng
            int userCount = userRepository.countUsersByMonth(startInstant, endInstant);
            // Lưu kết quả vào map với tên tháng
            usersPerMonth.put(Month.of(month).name(), userCount);
        }
        return usersPerMonth;
    }


    public Map<String, Integer> getTransactionsPerMonth() {
        Map<String, Integer> transactionsPerMonth = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Month month : Month.values()) {
            if (month.getValue() > today.getMonthValue()) {
                break;
            }

            // Đếm số lượng giao dịch trong mỗi tháng
            int transactionCount = transactionRepository.countTransactionsByMonth(month.getValue(), today.getYear());
            transactionsPerMonth.put(month.name(), transactionCount);
        }

        return transactionsPerMonth;
    }

}
