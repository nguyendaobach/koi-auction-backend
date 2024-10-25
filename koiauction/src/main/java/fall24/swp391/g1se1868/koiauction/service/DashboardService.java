package fall24.swp391.g1se1868.koiauction.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    AuctionService auctionService;
    @Autowired
    UserService userService;
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
