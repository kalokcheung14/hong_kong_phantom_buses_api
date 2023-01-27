package me.kalok.busgotlost;

import me.kalok.busgotlost.model.GenericResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public GenericResponse error() {
        return new GenericResponse("Invalid");
    }
}
