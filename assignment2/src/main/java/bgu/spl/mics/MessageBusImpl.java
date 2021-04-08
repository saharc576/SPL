package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.ResourcesManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    /**
     * key = microService name, value = queue of messages
     */
    private Map<String, LinkedBlockingQueue<Message>> messagesByNameOfMS;

    /**
     * key = queue of messages types, value = queue of microServices names
     * the value also uses to determine the next in line in {@code roundRobin} method
     */
    private Map<Class<? extends Message>, LinkedBlockingQueue<String>> subscriptionsByMsgType;

    /**
     * stores the future object corresponds to the event that was received in {@code sendEvent}
     */
    private Map<Message, Future<?>> futureMap;

    private Semaphore semaphore;
    private ResourcesManager mResManger;
    private static int mNumOfEwoks;


    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        messagesByNameOfMS = new ConcurrentHashMap<>();
        subscriptionsByMsgType = new ConcurrentHashMap<>();
        futureMap = new ConcurrentHashMap<>();
        mResManger = ResourcesManager.getInstance(mNumOfEwoks);
        semaphore = new Semaphore(1);
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    public static MessageBusImpl getInstance(int numOfEwoks) {
        MessageBusImpl.mNumOfEwoks = numOfEwoks;
        return SingletonHolder.instance;
    }


    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

        LinkedBlockingQueue<String> names;

        // only one thread can use this type of Event
        synchronized (type) {

            // someone has subscribed to this type already
            if (subscriptionsByMsgType.containsKey(type)) {
                names = subscriptionsByMsgType.get(type);
                names.add(m.getName());
            } else {
                names = new LinkedBlockingQueue<>();
                names.add(m.getName());
                subscriptionsByMsgType.put(type, names);
            }

            type.notifyAll();
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

        LinkedBlockingQueue<String> names;

        // only one thread can use this type of Broadcast
        synchronized (type) {

            // someone has subscribed to this type already
            if (subscriptionsByMsgType.containsKey(type)) {
                names = subscriptionsByMsgType.get(type);
                names.add(m.getName());
            } else {
                names = new LinkedBlockingQueue<>();
                names.add(m.getName());
                subscriptionsByMsgType.put(type, names);
            }

            type.notifyAll();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {

        Future<T> future;

        // to avoid completion of an event which hasn't been fully created - allowing only one thread enter
        // locked with: sendEvent + unregister
        try {
            semaphore.acquire();

            synchronized (e) {
                if (futureMap.containsKey(e)) {
                    future = (Future<T>) futureMap.get(e);
                    future.resolve(result);
                }
                e.notifyAll();
            }
            // else, the event has never been sent, thus couldn't have solved

        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } finally {
            semaphore.release();
        }
    }


    @Override
    public void sendBroadcast(Broadcast b) {

        // someone subscribed to this type of message
        if (subscriptionsByMsgType.containsKey(b.getClass())) {

            LinkedBlockingQueue<String> names = subscriptionsByMsgType.get(b.getClass());

            // add this broadcast to all those who subscribed
            for (String name : names) {
                LinkedBlockingQueue<Message> currMessages = messagesByNameOfMS.get(name);
                try {
                    // add this msg to Q and notify that it was added
                    currMessages.put(b);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // else, no microService has subscribed to this type of broadcast, do nothing
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        Future<T> future = new Future<>();

        // to avoid completion of an event who hasn't been fully created - allowing only one thread enter
        // locked with: complete + unregister
        try {
            semaphore.acquire();

            synchronized (e) {
                // someone subscribed to this type of message
                if (subscriptionsByMsgType.containsKey(e.getClass())) {
                    // get the next in line - round robin wise
                    String name = subscriptionsByMsgType.get(e.getClass()).poll();
                    // someone indeed subscribed to this event
                    if (name != null) {
                        subscriptionsByMsgType.get(e.getClass()).put(name);
                        LinkedBlockingQueue<Message> currMessages = messagesByNameOfMS.get(name);

                        try {
                            // add this msg to Q and notify (put notifies) that it was added
                            currMessages.put(e);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        // save the future object for later use in complete
                        futureMap.put(e, future);
                        e.notifyAll();
                        return future;
                    }
                } else // no one subscribed, must notify to those who are waiting on this lock that we are done with it, return null afterwards
                    e.notifyAll();
            }

        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } finally {
            semaphore.release();
        }

        // no microService has subscribed to this type of events
        return null;
    }


    @Override
    public void register(MicroService m) {
        messagesByNameOfMS.putIfAbsent(m.getName(), new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {

        // to avoid completion of an event who hasn't been fully created - allowing only one thread enter
        // locked with: sendEvent + complete
        try {
            semaphore.acquire();
            messagesByNameOfMS.remove(m.getName());

            for (Class<? extends Message> type : subscriptionsByMsgType.keySet()) {
                LinkedBlockingQueue<String> names = subscriptionsByMsgType.get(type);
                names.remove(m.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException, IllegalStateException {

        LinkedBlockingQueue<Message> messages = messagesByNameOfMS.get(m.getName());
        if (messages == null)
            throw new IllegalStateException(m.getName() + " didn't register");
        return messages.take();
    }

}
