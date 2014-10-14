package edu.berkeley.path.next.TestChronicle;


import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import edu.berkeley.path.next.TestChronicle.RunTest;
import edu.berkeley.path.next.TestDisruptor.LinkManager;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * Created by mauricemanning on 10/13/14.
 */
public class TestConfiguration {

    private int NUMBER_OF_LINKS = 1000000;


    @Bean
    public RunTest runTest() {
        RunTest rt =  new RunTest();
        try {
            rt.chr = new IndexedChronicle("/tmp/chronicle");
            rt.NUMBER_OF_LINKS = NUMBER_OF_LINKS;
            rt.linkMgr = new LinkManager();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rt;
    }

}
