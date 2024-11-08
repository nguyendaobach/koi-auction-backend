package fall24.swp391.g1se1868.koiauction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuctionService auctionService;

    public Map<String, Object> getUserData(Integer day, Integer month, Integer year) {
        Map<String, Object> userData = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        // Trường hợp nhập năm
        if (year != null && month == null && day == null) {
            for (int m = 1; m <= 12; m++) {
                int newUserCount = userService.getNewUsers(null, m, year);
                userData.put("newUserCount_month_" + m, newUserCount);

                // Lấy số lượng người dùng theo trạng thái và vai trò cho tháng hiện tại
                userData.put("userStatusCounts_month_" + m, userService.getUserCountsByStatus(null, m, year));
                userData.put("userRoleCounts_month_" + m, userService.getUserCountsByRole(null, m, year));
            }
        }
        // Trường hợp nhập tháng và năm
        else if (month != null && year != null && day == null) {
            for (int d = 1; d <= 30; d++) {
                int newUserCount = userService.getNewUsers(d, month, year);
                userData.put("newUserCount_day_" + d, newUserCount);
            }
            userData.put("userStatusCounts", userService.getUserCountsByStatus(null, month, year));
            userData.put("userRoleCounts", userService.getUserCountsByRole(null, month, year));
        }
        // Trường hợp nhập ngày, tháng, năm
        else if (day != null && month != null && year != null) {
            int newUserCount = userService.getNewUsers(day, month, year);
            userData.put("newUserCount", newUserCount);
            userData.put("userStatusCounts", userService.getUserCountsByStatus(day, month, year));
            userData.put("userRoleCounts", userService.getUserCountsByRole(day, month, year));
        }
        // Nếu không có tham số nào
        else {
            int newUserCount = userService.getNewUsers(null, null, null);
            userData.put("newUserCount", newUserCount);
            userData.put("userStatusCounts", userService.getUserCountsByStatus(null, null, null));
            userData.put("userRoleCounts", userService.getUserCountsByRole(null, null, null));
        }

        return userData;
    }
    public Map<String, Object> getTransactionData(Integer day, Integer month, Integer year) {
        Map<String, Object> transactionData = new HashMap<>();

        Long transactionCount;
        Long topUpCount;
        Long depositCount;
        Long withdrawCount;
        Long paymentCount;
        Long completedCount;
        Long pendingCount;

        // Xác định loại truy vấn dựa trên tham số đầu vào
        if (day != null && month != null && year != null) { // Trường hợp nhập ngày, tháng và năm
            transactionCount = transactionService.getTransactionCount(day, month, year);
        } else if (month != null && year != null) { // Trường hợp nhập tháng và năm
            transactionCount = transactionService.getTransactionCount(null, month, year);
        } else if (year != null) { // Trường hợp chỉ nhập năm
            transactionCount = transactionService.getTransactionCount(null, null, year);
        } else { // Trường hợp không có tham số nào được nhập (tất cả)
            transactionCount = transactionService.getTransactionCount(null, null, null);
        }

        // Cập nhật dữ liệu cho từng loại giao dịch
        String[] transactionTypes = {"Top-up", "Deposit", "Withdraw", "Payment"};
        for (String type : transactionTypes) {
            if (day != null && month != null && year != null) {
                transactionData.put("transactionCount_" + type + "_day_" + day + "_month_" + month + "_year_" + year,
                        transactionService.getTransactionCountByType(type, day, month, year));
            } else if (month != null && year != null) {
                transactionData.put("transactionCount_" + type + "_month_" + month + "_year_" + year,
                        transactionService.getTransactionCountByType(type, null, month, year));
            } else if (year != null) {
                transactionData.put("transactionCount_" + type + "_year_" + year,
                        transactionService.getTransactionCountByType(type, null, null, year));
            }
        }

        // Cập nhật dữ liệu cho trạng thái
        String[] statuses = {"Completed", "Pending"};
        for (String status : statuses) {
            if (day != null && month != null && year != null) {
                transactionData.put("transactionCount_" + status + "_day_" + day + "_month_" + month + "_year_" + year,
                        transactionService.getTransactionCountByStatus(status, day, month, year));
            } else if (month != null && year != null) {
                transactionData.put("transactionCount_" + status + "_month_" + month + "_year_" + year,
                        transactionService.getTransactionCountByStatus(status, null, month, year));
            } else if (year != null) {
                transactionData.put("transactionCount_" + status + "_year_" + year,
                        transactionService.getTransactionCountByStatus(status, null, null, year));
            }
        }

        transactionData.put("transactionCount_total", transactionCount);

        return transactionData;
    }

    public Map<String, Object> getAuctionData(Integer day, Integer month, Integer year) {
        Map<String, Object> auctionData = new HashMap<>();
        Long auctionCount = auctionService.getCountAuction(day, month, year);
        auctionData.put("auctionCount", auctionCount);
        return auctionData;
    }
    public Map<String, Object> getDashboard(Integer day, Integer month, Integer year) {
        Map<String, Object> dashboard = new HashMap<>();

        // Lấy doanh thu dựa trên ngày, tháng, năm
        Long revenue = auctionService.getRevenue(day, month, year);
        dashboard.put("revenue", revenue);

        // Lấy số lượng phiên đấu giá dựa trên ngày, tháng, năm
        Long auctionCount = auctionService.getCountAuction(day, month, year);
        dashboard.put("auctionCount", auctionCount);

        // Lấy số lượng phiên đấu giá có trạng thái Finished dựa trên ngày, tháng, năm
        Long finishedAuctionCount = auctionService.getCountAuctionWithFinishedStatus(day, month, year);
        dashboard.put("finishedAuctionCount", finishedAuctionCount);

        // Bạn có thể thêm các thông tin khác từ UserService nếu cần
        // Ví dụ: số lượng người dùng hoặc các thông tin khác
        Integer userCount = userService.getNewUsers(day,month,year);
        dashboard.put("newUserCount", userCount);

        return dashboard;
    }
}
