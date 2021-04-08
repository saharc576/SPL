package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok e;
    @BeforeEach
    void setUp() {
        e = new Ewok(1);
    }


    @Test
    void acquire() {
        assertTrue(e.available);
        e.acquire();
        assertFalse(e.available);
    }

    @Test
    void release() {
//        assertTrue(e.available);
        e.acquire();
        assertFalse(e.available);
        e.release();
        assertTrue(e.available);
    }
}