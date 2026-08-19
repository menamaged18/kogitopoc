package com.kogitopoc.kogitopoc.Entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_history_logs")
@Data
public class TaskHistoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;
    private String taskId;
    private String taskName;
    private String phaseStatus; // Ready, Completed
    private Double inputAmount;
    private Boolean outputApproved;
    private LocalDateTime timestamp;
}