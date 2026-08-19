package com.kogitopoc.kogitopoc.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kogitopoc.kogitopoc.Entities.*;

public interface TaskHistoryRepository extends JpaRepository<TaskHistoryLog, Long> {}