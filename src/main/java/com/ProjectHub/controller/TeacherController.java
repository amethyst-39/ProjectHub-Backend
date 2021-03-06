package com.ProjectHub.controller;

import com.ProjectHub.entities.Project;
import com.ProjectHub.model.*;
import com.ProjectHub.repository.ProjectRepository;
import com.ProjectHub.service.ProjectProfileService;
import com.ProjectHub.service.TeacherProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.ProjectHub.util.Constants.BEARER_AUTH;

/**
 * Created by Avinash Vijayvargiya on 10-10-2021.
 */
@RestController
@SecurityRequirement(name = BEARER_AUTH)
@PreAuthorize("hasRole('ROLE_TEACHER')")
@RequestMapping("/api")
public class TeacherController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectProfileService projectProfileService;

    @Autowired
    TeacherProfileService teacherProfileService;

    /**
     * the endpoint is used get all the projects in list view and detailed project view with project id
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @GetMapping("/teacherProject")
    public ResponseEntity<List<Project>> getAllProjects(@RequestParam(required = false) Long id) {
        try {
            List<Project> tutorials = new ArrayList<>();

            if (id == null)
                tutorials.addAll(projectRepository.findAll());
            else
                tutorials.addAll(projectRepository.getProjectByProjectId(id));

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    @GetMapping("/teacherProjectDetails/{projectID}")
    public ResponseEntity<ProjectDetails> getProjectDetails(@PathVariable Long projectID) {
        try {
            ProjectDetails project = projectProfileService.getProjectDetails(projectID);
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    @GetMapping("/teacherProfile/{employeeID}")
    public ResponseEntity<TeacherProfileModel> getTeacherProfile(@PathVariable String employeeID) {
        try {
            TeacherProfileModel teacher = teacherProfileService.getTeacherProfileByUsername(employeeID);
            return new ResponseEntity<>(teacher, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/teacherOngoing/{employeeID}")
    public ResponseEntity<List<TeacherOngoingProjectModel>> getTeacherOngoing(@PathVariable String employeeID) {
        try {
            List<TeacherOngoingProjectModel> teacherOngoing = projectProfileService.getTeacherOngoingCardDetails(employeeID);
            return new ResponseEntity<>(teacherOngoing, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addNewProject")
    public ResponseEntity<HttpStatus> addNewProject(@RequestBody NewProjectModel newProjectModel) {
        try {
            projectProfileService.insertProject(newProjectModel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateDeadline")
    public ResponseEntity<HttpStatus> updateDeadline(@RequestBody ProjectDeadlineModel projectDeadlineModel) {
        System.out.println("inside updateDeadline");
        System.out.println(projectDeadlineModel);
        try {
            projectProfileService.updateDeadline(projectDeadlineModel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("we are in exception");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/closeProject/{projectID}")
    public ResponseEntity<HttpStatus> closeProject(@PathVariable Long projectID) {
        try {
            projectProfileService.closeProject(projectID);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
