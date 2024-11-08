package fall24.swp391.g1se1868.koiauction.scheduler;

import fall24.swp391.g1se1868.koiauction.service.AuctionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloseAuctionJob implements Job {

    @Autowired
    private AuctionService auctionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Integer auctionId = (Integer) context.getJobDetail().getJobDataMap().get("auctionId");
        auctionService.closeAuction(auctionService.getAuctionById(auctionId));
    }
}
