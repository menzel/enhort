package de.thm.run;


import de.thm.backgroundModel.RandomBackgroundModel;
import de.thm.calc.IntersectMultithread;
import de.thm.positionData.Sites;
import de.thm.positionData.WebData;
import de.thm.stat.ResultCollector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@RestController
public class InputController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Input greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Input(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping("/input")
    public String input(@RequestParam(value="data") String data){
        WebData input = new WebData(data);

        Sites bg = new RandomBackgroundModel(input.getPositionCount());

        IntersectMultithread multi = new IntersectMultithread(Server.getIntervals(), input, bg);

        String result = ResultCollector.getInstance().toString().replaceAll("\n", "<br />");

        ResultCollector.getInstance().clear();

        return result;
    }
}
