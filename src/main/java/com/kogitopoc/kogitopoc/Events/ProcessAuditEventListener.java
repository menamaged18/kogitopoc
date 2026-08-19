package com.kogitopoc.kogitopoc.Events;

import com.kogitopoc.kogitopoc.Entities.ProcessHistoryLog;
import com.kogitopoc.kogitopoc.Repositories.ProcessHistoryRepository;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ProcessAuditEventListener extends DefaultKogitoProcessEventListener {

    @Autowired
    private ProcessHistoryRepository processRepo;

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        saveLog(event, "STARTED");
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        saveLog(event, "COMPLETED");
    }

    private void saveLog(ProcessEvent event, String status) {
        KogitoProcessInstance instance = (KogitoProcessInstance) event.getProcessInstance();
        Map<String, Object> variables = instance.getVariables();

        ProcessHistoryLog log = new ProcessHistoryLog();
        log.setProcessInstanceId(instance.getId());
        log.setProcessId(instance.getProcessId());
        log.setAmount(extractDouble(variables.get("amount")));
        log.setApproved((Boolean) variables.get("approved"));
        log.setStatus(status);
        log.setTimestamp(LocalDateTime.now());

        processRepo.save(log);
    }

    private Double extractDouble(Object rawValue) {
        if (rawValue instanceof Number number) {
            return number.doubleValue();
        }
        return null;
    }
}