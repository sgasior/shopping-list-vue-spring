package pl.edu.kopalniakodu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BackendController {

    @RequestMapping(value = "/**/{[path:[^\\.]*}")
    public String redirectApi() {
        return "forward:/";
    }
}
