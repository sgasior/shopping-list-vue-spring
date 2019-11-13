package pl.edu.kopalniakodu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DocumentationController {

    @RequestMapping("/doc/owner")
    public String docOwnerShow() {
        return "/doc/owner";
    }

    @RequestMapping("/doc/task")
    public String docTaskShow() {
        return "/doc/task";
    }

}
