package bank.controller;
import java.time.Clock;
import java.time.Duration;
import java.time.YearMonth;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import bank.dto.InterestSummary;

/**
 * 
 * "0InterestScheduler" as depicted in my use case diagram, is a timer that pays the previous months interest
 * once the month ends. eliminates the need to manually pay it with a button
 * 
 * it keeps up to date with regular checks at hourly intervals so interest is never paid twice for the same month(specified in Interest Bearing)
 * 
 */
public class InterestScheduler {

 private final InterestController interest;
 private final Clock clock;
 private final AtomicInteger checks = new AtomicInteger();
 private ScheduledExecutorService executor;
 private Consumer<InterestSummary> onPaid = summary -> { };

 public InterestScheduler(InterestController interest, Clock clock) {
    this.interest = interest;
    this.clock = clock;
 }
   



 public void onInterestPaid(Consumer<InterestSummary> listener) {
    this.onPaid = listener;
 }





 public InterestSummary runIfDue() {
  YearMonth lastMonth = YearMonth.now(clock).minusMonths(1);
  InterestSummary summary = interest.payFor(lastMonth);
  if (summary.accountsPaid() > 0) {
   onPaid.accept(summary);
  }

    return summary;
  }




    public synchronized void start(Duration period) {
        if (executor != null) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "interest-scheduler");
            thread.setDaemon(true);   // never keeps the program open
            return thread;
        });
        executor.scheduleWithFixedDelay(this::safeCheck, 0, period.toMillis(), TimeUnit.MILLISECONDS);
    }




    public synchronized void stop() {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor = null;
    }



    public int checksRun() {
        return checks.get();
    }


    private void safeCheck() {
     try {
          runIfDue();
    } catch (RuntimeException e) {
     System.err.println("Interest scheduler: check failed: " + e.getMessage());
     } finally {
       checks.incrementAndGet();
     }
}









}
