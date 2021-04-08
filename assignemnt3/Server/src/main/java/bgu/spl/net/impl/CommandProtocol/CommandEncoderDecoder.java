package bgu.spl.net.impl.CommandProtocol;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.CommandProtocol.CommandMessages.*;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class CommandEncoderDecoder implements MessageEncoderDecoder<Serializable> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private String userName = null;
    private String password;
    private short opCode = 0;
    private short courseNumber = 0;
    private final ByteBuffer typeBuffer = ByteBuffer.allocate(2);
    private final ByteBuffer courseNumBuffer = ByteBuffer.allocate(3);
    private byte[] objectBytes = null;

    /**
     * decodes the next byte, until it decodes the entire message
     *
     * @param nextByte the next byte to consider for the currently decoded
     *                 message
     * @return the decoded command if done OR null, if message is not fully decoded yet
     */
    public Serializable decodeNextByte(byte nextByte) {

        if (objectBytes == null) { //indicates that we are still reading the length

            // checking if this is logout or myCourses - if so, we are done
            if (opCode == 4)
                return new LOGOUT();
            if (opCode == 11)
                return new MYCOURSES();

            auxDecodeForOpcode(nextByte);
        }

        // message is not done, checking whether the next phrase is Course Number
        else if (opCode == 5 || opCode == 6 || opCode == 7 || opCode == 9 || opCode == 10)
            return commandDeciderForCourse(nextByte);

            // message is not done, checking whether the next phrase is Student user name (STUDENTSTAT)
        else if (opCode == 8) {
            if (nextByte != '\0')
                pushByte(nextByte);

                // we finished reading the userName
            else
                return new STUDENTSTAT(popString());
        }

        // message is not done, checking whether the next phrase is user name + password
        else if (opCode == 1 || opCode == 2 || opCode == 3)
            return commandDeciderForUser(nextByte);

        return null;
    }

    private void auxDecodeForOpcode(byte nextByte) {
        typeBuffer.put(nextByte);

        if (!typeBuffer.hasRemaining()) { // we read 2 bytes and therefore can take the length
            typeBuffer.flip();
            objectBytes = typeBuffer.array(); // placing the first two bytes in objectBytes
            opCode = bytesToShort(objectBytes);
            if (opCode == 4 || opCode == 11)
                objectBytes = null;

            typeBuffer.clear(); // clearing the data
        }
    }

    private void auxDecodeForCourseNum(byte nextByte) {
        courseNumBuffer.put(nextByte);

        if (!courseNumBuffer.hasRemaining()) { //we read 2 bytes and therefore can take the length
            courseNumBuffer.flip();
            objectBytes = courseNumBuffer.array(); // placing the first two bytes in objectBytes
            courseNumber = bytesToShort(objectBytes);
            courseNumBuffer.clear(); // clearing the data
        }
    }

    /**
     * aux function for opcode {5,6,7,9,10}
     * @param nextByte
     * @return the decoded command
     */
    private Serializable commandDeciderForCourse(byte nextByte) {
        auxDecodeForCourseNum(nextByte);

        // we finished reading, must decide which command to return
        if (courseNumber != 0) {

            if (opCode == 5)
                return new COURSEREG(courseNumber);

            if (opCode == 6)
                return new KDAMCHECK(courseNumber);

            if (opCode == 7)
                return new COURSESTAT(courseNumber);

            if (opCode == 9)
                return new ISREGISTERED(courseNumber);

            // opcode = 10
            return new UNREGISTER(courseNumber);
        }
        return null;
    }

    /**
     * aux function for opcode {1,2,3}
     * @param nextByte
     * @return the decoded command
     */
    private Serializable commandDeciderForUser(byte nextByte) {

        // parsing the userName
        if (nextByte != '\0' && userName == null)
            pushByte(nextByte);

            // finished parsing userName
        else if (nextByte == '\0' && userName == null)
            userName = popString();

            // parsing password
        else if (nextByte != '\0')
            pushByte(nextByte);

            // finished parsing both password and userName
        else {
            password = popString();

            // checking which command to return
            if (opCode == 1)
                return new ADMINREG(userName, password);

            if (opCode == 2)
                return new STUDENTREG(userName, password);

            if (opCode == 3)
                return new LOGIN(userName, password);
        }
        return null;
    }

    /**
     * encodes the message by using two aux functions
     * {@code serializeACK} and {@code serializeERROR}
     * @param message the message to encode
     * @return the encoded message
     */
    public byte[] encode(Serializable message) {
        byte[] output;
        if (message.getClass() == ERROR.class)
            output = serializeERROR((ERROR) message);
        else
            output = serializeACK((ACK) message);
        return output;
    }

    /**
     * serialize an ERROR command to byte array
     *
     * @param error the command
     * @return the serialized ERROR command
     */
    private byte[] serializeERROR(ERROR error) {
        String outputString = "ERROR ";
        short opcode = error.getMessageOpCode();
        outputString += opcode + "\0";
        return (outputString).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * serialize an ACK command to byte array
     *
     * @param ack the command
     * @return the serialized ACK command
     */
    private byte[] serializeACK(ACK ack) {
        String outputString = "ACK ";
        short opcode = ack.getMessageOpCode();
        String optional = ack.getOptional();
        outputString += opcode + "\n" + optional;
        return (outputString).getBytes(StandardCharsets.UTF_8);
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    /**
     * aux function to clear all members after finishing
     * reading a message
     */
    public void clear() {
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
        userName = null;
        password = "";
        opCode = 0;
        courseNumber = 0;
        objectBytes = null;
        typeBuffer.flip();
        typeBuffer.clear();
        courseNumBuffer.flip();
        courseNumBuffer.clear();
    }
}
