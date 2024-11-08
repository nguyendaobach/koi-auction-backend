package fall24.swp391.g1se1868.koiauction.service;

import fall24.swp391.g1se1868.koiauction.scheduler.CloseAuctionJob;
import fall24.swp391.g1se1868.koiauction.scheduler.StartAuctionJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class AuctionSchedulerService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleStartAuction(Integer auctionId, Instant startInstant) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(StartAuctionJob.class)
                .withIdentity("startAuctionJob_" + auctionId, "auctionGroup")
                .usingJobData("auctionId", auctionId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("startAuctionTrigger_" + auctionId, "auctionGroup")
                .startAt(Date.from(startInstant))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void scheduleCloseAuction(Integer auctionId, Instant endInstant) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(CloseAuctionJob.class)
                .withIdentity("closeAuctionJob_" + auctionId, "auctionGroup")
                .usingJobData("auctionId", auctionId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("closeAuctionTrigger_" + auctionId, "auctionGroup")
                .startAt(Date.from(endInstant))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
