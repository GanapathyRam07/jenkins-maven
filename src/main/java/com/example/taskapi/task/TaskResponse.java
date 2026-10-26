package com.example.taskapi.task;
import java.time.Instant;
public record TaskResponse(Long id, String title, String description, TaskStatus status,
                           Instant createdAt, Instant updatedAt) {
    static TaskResponse from(TaskEntity e) {
        return new TaskResponse(e.getId(), e.getTitle(), e.getDescription(), e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
