package com.kogitopoc.kogitopoc.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "process_history_logs")
@Data
public class ProcessHistoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;
    private String processId;
    private Double amount;
    private Boolean approved;
    private String status; // e.g., STARTED, COMPLETED
    private LocalDateTime timestamp;
}