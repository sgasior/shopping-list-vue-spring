package pl.edu.kopalniakodu;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BackendController {

    public static final String HELLO_TEXT = "Hello from Spring Boot Backend!";

    @RequestMapping(path = "/hello")
    public String sayHello() {
        return HELLO_TEXT;
    }
}
