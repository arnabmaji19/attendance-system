package io.github.arnabmaji19.attendancesystem.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping()
    public String home() {
        return "Attendance Management System";
    }
}
