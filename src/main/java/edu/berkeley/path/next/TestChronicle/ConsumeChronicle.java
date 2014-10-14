package edu.berkeley.path.next.TestChronicle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 */

@Configuration
@ComponentScan("edu.berkeley.path.next.TestChronicle")

public class ConsumeChronicle {

    @Autowired
    static private RunTest runTest;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();
        ctx.register(TestConfiguration.class);
        ctx.refresh();

        final Logger logger = LogManager.getLogger(ConsumeChronicle.class.getName());
        logger.info("start test. ");

        RunTest runTest = ctx.getBean(RunTest.class);

        runTest.runConsumeChronicleTest();

    }


}
