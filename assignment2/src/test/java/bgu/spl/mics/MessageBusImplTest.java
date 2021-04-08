package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageBusImplTest {
    private boolean isItABroadcast;
    private MSreceiver broadcastReceiver;
    private MSreceiver eventReceiver1;
    private MSreceiver eventReceiver2;
    private MSsender broadcastSender;
    private MSsender eventSender;

    private MessageBusImpl messageBus;

    @BeforeEach
    public void setUp() {

        // build each of the microServices and initialize them
        messageBus = MessageBusImpl.getInstance();
        broadcastReceiver = new MSreceiver("broadcastReceiver", true);
        broadcastSender = new MSsender("broadcastSender", new String[]{"broadcast"});
        eventReceiver1 = new MSreceiver("eventReceiver1", false);
        eventReceiver2 = new MSreceiver("eventReceiver2", false);
        eventSender = new MSsender("eventSender", new String[]{"event"});
        broadcastSender.initialize();
        broadcastReceiver.initialize();
        eventSender.initialize();
        eventReceiver1.initialize();
        eventReceiver2.initialize();

    }

    /**
     * this method {@code tearDown} was added since we needed to clear the info of
     * messageBus after each test.
     * before, it "remembered" the previous results and failed tests with no reason
     */
    @AfterEach
    void tearDown() {
        messageBus.unregister(broadcastReceiver);
        messageBus.unregister(eventReceiver1);
        messageBus.unregister(eventReceiver2);
        messageBus.unregister(broadcastSender);
        messageBus.unregister(eventSender);

    }

    @Test
    void complete() {
        TempEvent ev = new TempEvent("eventSender1");
        String result = "success";

        // subscribing to the type of event we are going to receive
        messageBus.subscribeEvent(TempEvent.class, eventReceiver1);

        // the future object that we will check if solved
        Future<String> future = messageBus.sendEvent(ev);

        // update the future object related to this event with this result
        messageBus.complete(ev, result);

        Object futureResult = future.get(300, TimeUnit.MILLISECONDS);
        if (futureResult == null)
            fail("the result was not updated by the message bus");

        //check if the original result is the same as futureResult
        assertEquals(result, futureResult);
    }

    @Test
    void sendBroadcast() {
        Message message = null;
        TempBroadcast b = new TempBroadcast("broadcastSender1");

        // assuming the subscribeBroadcast method works fine in microService
        broadcastReceiver.subscribeBroadcast(TempBroadcast.class, c -> {
        });
        // this is the method we check
        messageBus.sendBroadcast(b);

        // also assuming awaitMessage works fine
        try {
            message = messageBus.awaitMessage(broadcastReceiver);
        } catch (InterruptedException e) {
            fail("the message wasn't inserted to broadcastReceiver's queue");
        }
        // check if the broadcast that was sent (by microService method) equals to the one that was received
        assertEquals(b, message);

    }

    @Test
    void sendEvent() {
        Message message = null;
        TempEvent ev = new TempEvent("eventSender2");

        // assuming the subscribeEvent method works fine in microService
        eventReceiver2.subscribeEvent(TempEvent.class, c -> {
        });
        // this is the method we check
        messageBus.sendEvent(ev);

        // also assuming awaitMessage works fine
        try {
            message = messageBus.awaitMessage(eventReceiver2);
        } catch (InterruptedException e) {
            fail("the message wasn't inserted to eventReceiver's queue");
        }
        // check if the event that sent equals to the one that was received
        assertEquals(ev, message);

    }


    @Test
    void awaitMessage() {
        // assuming micro service works fine

        Message message = null;
        TempBroadcast b = new TempBroadcast("broadcastSender2");

        broadcastReceiver.subscribeBroadcast(TempBroadcast.class, c -> {
        });
        broadcastSender.sendBroadcast(b);

        // here we will check the method
        try {
            message = messageBus.awaitMessage(broadcastReceiver);

        } catch (InterruptedException e) {
            e.printStackTrace();
            //due to the assumption above, an exception is not supposed to be thrown
        }
        assertEquals(b, message);
    }


    // ====================================== Anonymous Classes ======================================

    // ================== Temp Messages ==================

    static class TempEvent implements Event<String> {

        private String senderName;

        public TempEvent(String senderName) {
            this.senderName = senderName;
        }

        public String getSenderName() {
            return senderName;
        }


    }

    static class TempBroadcast implements Broadcast {

        private String senderId;

        public TempBroadcast(String senderId) {
            this.senderId = senderId;
        }

        public String getSenderId() {
            return senderId;
        }
    }

    // ================== Temp MicroServices ==================

    public class MSreceiver extends MicroService {

        public MSreceiver(String name, boolean broadcast) {
            super(name);

            isItABroadcast = broadcast;
        }

        @Override
        protected void initialize() {
            messageBus.register(this);
        }
    }

    public class MSsender extends MicroService {

        public MSsender(String name, String[] args) {
            super(name);

            if (args.length != 1 || !args[0].matches("broadcast|event")) {
                throw new IllegalArgumentException("the message: " + args[0] + " is not allowed");
            }
            isItABroadcast = args[0].equals("broadcast");

        }

        @Override
        protected void initialize() {
            messageBus.register(this);
        }
    }
}



