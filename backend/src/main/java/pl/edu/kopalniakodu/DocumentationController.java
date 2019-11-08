package pl.edu.kopalniakodu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DocumentationController {

    @RequestMapping("/doc")
    public String docShow() {
        return "/doc/index";
    }

}
