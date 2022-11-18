package eu.kijora.todoapp.controller;

import eu.kijora.todoapp.logic.ProjectService;
import eu.kijora.todoapp.model.Project;
import eu.kijora.todoapp.model.ProjectStep;
import eu.kijora.todoapp.model.dto.ProjectWriteModel;
import io.micrometer.core.annotation.Timed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
        //it's looking in templates for views to render
    String showProject(Model model) {
        ProjectWriteModel projectWriteModel = new ProjectWriteModel();
        projectWriteModel.setDescription("test input");
        model.addAttribute("project", projectWriteModel);
        return "projects";
    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current){ //How does it know it's THE projectWriteModel?
        current.getSteps().add(new ProjectStep());
        return "projects";
    }

    @PostMapping("/{id}")
    @Timed(value = "project.create.group", histogram = true, percentiles = {0.5, 0.95, 0.99})
    String createGroup(@ModelAttribute("project") ProjectWriteModel current, //why current is not used?
                       Model model,
                       @PathVariable int id,
                       @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline){
        try {
            service.createGroup(deadline, id);
            model.addAttribute("message", "Group added");

        }
        catch (IllegalArgumentException | IllegalStateException e){
            model.addAttribute("message", "Error when creating the group");
        }
        return "projects";
    }

    @PostMapping
    String addProject(@ModelAttribute("project") @Valid ProjectWriteModel current,
                      BindingResult bindingResult,
                      Model model){
        if(bindingResult.hasErrors()){
            return "projects";
        }
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("projects", getProjects());
        model.addAttribute("message", "Project added");
        return "projects";

    }

    @ModelAttribute("projects") //how does it actually work? Injecting model to each view?
    List<Project> getProjects(){
        return service.readAll();
    }
}
