package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.TicketDaoImpl;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class SLATrackingService {

    private final TicketDaoImpl ticketDAO;
    private final ExecutorService executorService;

    @PostConstruct
    public void startSLAMonitor() {

        executorService.submit(() -> {

            while (true) {

                try {

                    checkSLABreach();

                    Thread.sleep(300000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkSLABreach() {

        List<Ticket> tickets =
                ticketDAO.getActiveTickets();

        LocalDateTime now =
                LocalDateTime.now();

        for (Ticket ticket : tickets) {

            if (ticket.getSla_deadline() != null
                    &&
                    now.isAfter(
                            ticket.getSla_deadline())) {

                ticket.setStatus(
                        TicketStatus.SLA_BREACHED);

                escalatePriority(ticket);

                ticketDAO.update(ticket);

                System.out.println(
                        "SLA breached for ticket: "
                                + ticket.getTicketId());
            }
        }
    }

    private void escalatePriority(
            Ticket ticket) {

        Priority priority =
                ticket.getPriority();

        switch (priority) {

            case LOW ->
                    ticket.setPriority(
                            Priority.MEDIUM);

            case MEDIUM ->
                    ticket.setPriority(
                            Priority.HIGH);

            case HIGH ->
                    ticket.setPriority(
                            Priority.HIGH);
        }
    }
}