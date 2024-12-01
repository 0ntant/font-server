package app.backendservice.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
@RequestMapping("/font")
public class FontWebController 
{
    @GetMapping("/form")
    public String fontForm()
    {   
        log.info("GET /font/form");
        return "fontForm";
    }
}
