package edu.berkeley.path.next.TestChronicle;

import edu.berkeley.path.next.TestDisruptor.LinkDataRaw;
import edu.berkeley.path.next.TestDisruptor.LinkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.higherfrequencytrading.chronicle.Chronicle;
import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;

@Configuration
@ComponentScan("edu.berkeley.path.next.TestChronicle")

public class RunTest  {

    protected long NUMBER_OF_LINKS;

    protected Environment environment;
    protected LinkManager linkMgr;
    protected Chronicle chr;

    private static int STRING_SIZE_OVERHEAD = 4;


    @Bean
    public void runChronicleTest() throws Exception {
        final Logger logger = LogManager.getLogger(RunTest.class.getName());
        logger.info("start runChronicleTest. ");

        // create one Link and wrap it in an event so it is ready to publish to Reactor
        LinkDataRaw link = linkMgr.getLink();
        System.out.println("link info: " + link.getSpeedLimit());
        System.out.println("link size: " + sizeof(link));
        byte[] msgText = getTheBytes(link);
        System.out.println("link msg size: " + sizeof(msgText));

        boolean sentinal = false;
        System.out.println("Boolean size: " + sizeof(sentinal));


        ByteBuffer bb = ByteBuffer.allocate(601);
        bb.put(msgText);

        IndexedChronicle chronicle =   new IndexedChronicle("/tmp/chronicle_in");
        Excerpt excerpt =   chronicle.createExcerpt();

        long start = System.currentTimeMillis();

        for (int i = 1; i < NUMBER_OF_LINKS + 1; i++) {

            if (i == NUMBER_OF_LINKS) {
                sentinal = true;
            }
            excerpt.startExcerpt(648);  //601
            excerpt.writeBoolean(sentinal);
            excerpt.write(msgText);
            excerpt.finish();
            //System.out.println("runChronicleTest excerpt " + i + "  sentinal: " + sentinal);

        }
        long elapsed = System.currentTimeMillis()-start;
        System.out.println("runChronicleTest Elapsed time: " + elapsed + "ms");

    }


    @Bean
    public void runConsumeChronicleTest() throws Exception {
        final Logger logger = LogManager.getLogger(RunTest.class.getName());
        logger.info("start runConsumeChronicleTest. ");

        IndexedChronicle chronicle =
                new IndexedChronicle("/tmp/chronicle_out");

        Excerpt excerpt = chronicle.createExcerpt();
        ByteBuffer bb = ByteBuffer.allocate(601);
        byte[] msgText = new byte[601];


        long start = System.currentTimeMillis();
        while ( true) {
            if (excerpt.nextIndex()) {
                boolean chk = excerpt.readBoolean();
                excerpt.read(msgText);
//                System.out.println(" newLink raw: " + msgText.toString() );
//                LinkDataRaw newLink = (LinkDataRaw) deserialize(msgText);
//                System.out.println(" newLink: " + newLink.getLanes());

                if (chk) {
                    break;
                }

            } else {
                LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(1));
            }
        }

        chronicle.close();

        long elapsed = System.currentTimeMillis()-start;
        System.out.println("runConsumeChronicleTest Elapsed time: " + elapsed + "ms");

    }







    @Bean
    public void runChronicleTestOld() throws Exception {
        final Logger logger = LogManager.getLogger(RunTest.class.getName());
        logger.info("start runTest. ");


        try {
            String tempPath = System.getProperty("java.io.tmpdir");
            String basePrefix = tempPath + File.separator + "chronicle";
            System.out.println("base prefix: " + basePrefix);
            Chronicle chr = new IndexedChronicle(basePrefix);

            writeToChronicle(chr, "Some text");
            writeToChronicle(chr, "more text");
            writeToChronicle(chr, "and a little bit more");

            dumpChronicle(chr);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public static void writeToChronicle(Chronicle chr, String someString) {
        final Excerpt excerpt = chr.createExcerpt();
        excerpt.startExcerpt(someString.length() + STRING_SIZE_OVERHEAD);

        excerpt.writeBytes(someString);
        excerpt.finish();
    }

    public static void dumpChronicle(Chronicle chr) {
        final Excerpt excerpt = chr.createExcerpt();

        while (excerpt.nextIndex()) {
            System.out.println("Read string from chronicle: " + excerpt.readByteString());
        }
    }

    public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }

    public static byte[] getTheBytes(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();
        System.out.println("runDisruptorForLinks objectOutputStream: " + objectOutputStream.toString() );

        return byteOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
