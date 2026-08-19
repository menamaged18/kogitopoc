package com.kogitopoc.kogitopoc.Controllers;

import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/my_process")
@SuppressWarnings({"rawtypes", "unchecked"})
public class CustomMyProcessController {

    @Autowired
    @Qualifier("my_process")
    private Process myProcess;

    // 1. Start Process
    @PostMapping
    public ResponseEntity<Map<String, Object>> startProcess(@RequestBody Map<String, Object> body) {
        Model model = (Model) myProcess.createModel();
        model.fromMap(body);

        // Explicitly pass (String) null for businessKey to eliminate overload ambiguity
        ProcessInstance instance = myProcess.createInstance((String) null, model);
        instance.start();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", instance.id());
        response.putAll(((Model) instance.variables()).toMap());

        return ResponseEntity.ok(response);
    }

    // 2. Get Pending User Tasks 
    @GetMapping("/{id}/taskssss")
    public ResponseEntity<List<Map<String, Object>>> getPendingTasks(
            @PathVariable("id") String id,
            @RequestParam("user") String user,
            @RequestParam("group") String group) {

        Optional<ProcessInstance> instanceOpt = myProcess.instances().findById(id);
        if (instanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProcessInstance instance = instanceOpt.get();
        SecurityPolicy policy = SecurityPolicy.of(IdentityProviders.of(user, Collections.singletonList(group)));

        List<WorkItem> activeWorkItems = instance.workItems(policy);

        List<Map<String, Object>> taskList = activeWorkItems.stream().map(item -> {
            Map<String, Object> task = new LinkedHashMap<>();
            task.put("id", item.getId());
            task.put("name", item.getName());
            task.put("state", item.getState());
            task.put("phase", item.getPhase());
            task.put("phaseStatus", item.getPhaseStatus());
            task.put("parameters", item.getParameters());
            task.put("results", item.getResults());
            return task;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(taskList);
    }

    // 3. Complete Process using TaskId
    // A Process Instance ID identifies the overall running workflow execution, 
    // while a Task ID identifies a specific node/step inside that execution.
    @PostMapping("/{id}/{taskName}/{taskId}")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable("id") String id,
            @PathVariable("taskName") String taskName,
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") String group,
            @RequestBody Map<String, Object> taskOutputs) {

        Optional<ProcessInstance> instanceOpt = myProcess.instances().findById(id);
        if (instanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProcessInstance instance = instanceOpt.get();
        SecurityPolicy policy = SecurityPolicy.of(IdentityProviders.of(user, Collections.singletonList(group)));

        instance.completeWorkItem(taskId, taskOutputs, policy);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", instance.id());
        response.putAll(((Model) instance.variables()).toMap());

        return ResponseEntity.ok(response);
    }

    // Complete Manager Review without requiring Task ID
    @PostMapping("/{id}/review")
    public ResponseEntity<Map<String, Object>> completeManagerReview(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> taskOutputs) { // Expects {"out_approved": true} or {"out_approved": false}

        Optional<ProcessInstance> instanceOpt = myProcess.instances().findById(id);
        if (instanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProcessInstance instance = instanceOpt.get();
        List<WorkItem> activeTasks = instance.workItems();

        // Since it's an Exclusive Gateway, activeTasks.get(0) is always 'ManagerReviewTask'
        WorkItem managerTask = activeTasks.get(0);

        // Complete the task and map out_approved -> approved
        instance.completeWorkItem(managerTask.getId(), taskOutputs);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", instance.id());
        response.putAll(((Model) instance.variables()).toMap());

        return ResponseEntity.ok(response);
    }
}