package io.github.arnabmaji19.attendancesystem.restcontroller;

import io.github.arnabmaji19.attendancesystem.entity.Course;
import io.github.arnabmaji19.attendancesystem.entity.User;
import io.github.arnabmaji19.attendancesystem.model.*;
import io.github.arnabmaji19.attendancesystem.service.CourseService;
import io.github.arnabmaji19.attendancesystem.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @RequestBody CourseDetails courseDetails) {

        logger.info("Title: " + courseDetails.getTitle());

        // Retrieve currently logged in user
        var userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        var user = userService.findByUsername(userDetails.getUsername());

        Course course = new Course(courseDetails.getTitle(), user);
        courseService.save(course);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/enrollments/")
    public ResponseEntity<?> enroll(@PathVariable int courseId,
                                    @Valid @RequestBody StudentUsernameRequest studentUsernameRequest) {
        Course course = courseService.findById(courseId);
        if (course == null)
            return ResponseEntity.badRequest().body(new ResponseMessage("Course not found."));

        User user = userService.findByUsername(studentUsernameRequest.getUsername());
        if (user == null)
            return ResponseEntity.badRequest().body(new ResponseMessage("Username not found"));

        course.enrollStudent(user);
        courseService.save(course);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}/enrollments/")
    public ResponseEntity<?> getEnrollments(@PathVariable int courseId) {
        Course course = courseService.findById(courseId);
        if (course == null)
            return ResponseEntity.badRequest().body(new ResponseMessage("Course not found."));

        List<String> list = course
                .getEnrolledStudents()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new StudentUsernameList(list));
    }

}
