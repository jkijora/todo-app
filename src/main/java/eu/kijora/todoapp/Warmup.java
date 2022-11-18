package eu.kijora.todoapp;

import eu.kijora.todoapp.model.Task;
import eu.kijora.todoapp.model.TaskGroup;
import eu.kijora.todoapp.model.TaskGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class Warmup implements ApplicationListener<ContextRefreshedEvent> {

    public static final Logger logger = LoggerFactory.getLogger(Warmup.class);

    private final TaskGroupRepository taskGroupRepository;

    public Warmup(TaskGroupRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //something what we do on ContextRefreshedEvent
        logger.info("App warmup after context refreshed");
        final String description = "ApplicationContextEvent";
        if(!taskGroupRepository.existsByDescription(description)){
            logger.info("No required group found. Adding it.");
            var group = new TaskGroup();
            group.setDescription(description);
            group.setTasks(Set.of(
                    new Task("ContextClosedEvent", null, group),
                    new Task("ContextRefreshedEvent", null, group),
                    new Task("ContextStoppedEvent", null, group),
                    new Task("ContextStartedEvent", null, group)
            ));

            taskGroupRepository.save(group);
        }

    }
}
