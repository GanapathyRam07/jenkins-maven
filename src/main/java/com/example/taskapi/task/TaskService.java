package com.example.taskapi.task;
import com.example.taskapi.common.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository repository;
    public TaskService(TaskRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().map(TaskResponse::from).toList();
    }
    @Transactional(readOnly = true)
    public TaskResponse findById(long id) { return TaskResponse.from(getEntity(id)); }
    public TaskResponse create(TaskRequest r) {
        return TaskResponse.from(repository.save(new TaskEntity(r.title().trim(), r.description(), r.status())));
    }
    public TaskResponse update(long id, TaskRequest r) {
        TaskEntity e = getEntity(id); e.update(r.title().trim(), r.description(), r.status());
        return TaskResponse.from(repository.save(e));
    }
    public void delete(long id) { repository.delete(getEntity(id)); }
    private TaskEntity getEntity(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " was not found"));
    }
}
