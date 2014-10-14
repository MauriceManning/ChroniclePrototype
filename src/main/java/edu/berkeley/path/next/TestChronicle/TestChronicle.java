package edu.berkeley.path.next.TestChronicle;

import edu.berkeley.path.next.TestChronicle.TestConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * http://java.dzone.com/articles/ultra-fast-reliable-messaging
 *  http://binarybuffer.com/2013/04/java-chronicle-library-tutorial-1-basic-examples
 *  http://www.slideshare.net/PeterLawrey/writing-and-testing-high-frequency-trading-engines-in-java
 *
 *
 *  $ java -cp chronicle-1.6.jar com.higherfrequencytrading.chronicle.tcp.ChronicleSource /tmp/chronicle_in 8099
 *  $ java -cp chronicle-1.6.jar com.higherfrequencytrading.chronicle.tcp.ChronicleSink /tmp/chronicle_out localhost 8099
 *  $ java ChronicleConsumer
 *  $ java ChronicleProducer
 *
 *   rm -rf /tmp/ch*
 *
 */

@Configuration
@ComponentScan("edu.berkeley.path.next.TestChronicle")
public class TestChronicle {

    @Autowired
    static private RunTest runTest;

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();
        ctx.register(TestConfiguration.class);
        ctx.refresh();

        final Logger logger = LogManager.getLogger(TestChronicle.class.getName());
        logger.info("start test. ");

        RunTest runTest = ctx.getBean(RunTest.class);

        //This test passes Links thru the RB
        runTest.runChronicleTest();

    }


}
