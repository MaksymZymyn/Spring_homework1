package homework1.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(DefaultErrorController.class);

    @RequestMapping("/error")
    public String handleError() {
        // Logging error
        logger.error("An error occurred, redirecting to index.html");

        // Returning index.html page
        return "forward:/index.html";
    }
}
