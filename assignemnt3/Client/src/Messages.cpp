//
// Created by inbar on 01-Jan-21.
//

#include "../include/Messages.h"

#include <utility>
#include <iostream>


using namespace std;

//==========CONSTRUCTOR
Messages::Messages(string txt) : message(move(txt)) {

}

//==========DESTRUCTOR
Messages::~Messages() = default;

//==========MSG FROM CLIENT

MessageFromClient::MessageFromClient(string text) :
        Messages(std::move(text)), type(), userName(), password(), courseNumStr(), opcode(0), logoutMsg(false) {

}

string MessageFromClient::process() {

    string opInBytes;
    short courseNumber = 0;

    setMessageDataClient();

    // change the opcode to bytes
    opInBytes.append(1, ((char) ((opcode >> 8) & 0xFF)));
    opInBytes.append(1, (char) (opcode & 0xFF));

    //create the processed message by cases

    if (opcode == 4 || opcode == 11)
        message = opInBytes;
    else if (opcode == 1 || opcode == 2 || opcode == 3)
        message = opInBytes + userName + '\0' + password;
    else if (opcode == 5 || opcode == 6 || opcode == 7 || opcode == 9 || opcode == 10) {
        // change the courseNumber to short, and than to bytes
        try {
            courseNumber = boost::lexical_cast<short>(courseNumStr);
        } catch (boost::bad_lexical_cast &) {}

        courseNumStr = ""; // nullify before append
        courseNumStr.append(1, ((char) ((courseNumber >> 8) & 0xFF)));
        courseNumStr.append(1, (char) (courseNumber & 0xFF));
        message = opInBytes + courseNumStr;
    } else // op == 8
        message = opInBytes + userName;

    // raise flag
    if (opcode == 4)
        logoutMsg = true;

    return message;
}

short MessageFromClient::changeTypeToOpcode(const string &_type) {
    short op = 0;

    if (_type == "ADMINREG")
        op = 1;
    else if (_type == "STUDENTREG")
        op = 2;
    else if (_type == "LOGIN")
        op = 3;
    else if (_type == "LOGOUT")
        op = 4;
    else if (_type == "COURSEREG")
        op = 5;
    else if (_type == "KDAMCHECK")
        op = 6;
    else if (_type == "COURSESTAT")
        op = 7;
    else if (_type == "STUDENTSTAT")
        op = 8;
    else if (_type == "ISREGISTERED")
        op = 9;
    else if (_type == "UNREGISTER")
        op = 10;
    else if (_type == "MYCOURSES")
        op = 11;

    return op;
}

bool MessageFromClient::isLogoutMsg() const {
    return logoutMsg;
}

void MessageFromClient::setMessageDataClient() {
    int counter = 0;
    for (char &c : message) {
        if (c != ' ' && counter == 0)
            type += c;
        else if (c == ' ' && counter == 0) {
            counter++;
            opcode = changeTypeToOpcode(type);
        } else if (c != ' ' && counter >= 1) {
            // expecting username
            if ((opcode == 1 || opcode == 2 || opcode == 3) && counter == 1)
                userName += c;
                // expecting password
            else if ((opcode == 1 || opcode == 2 || opcode == 3) && counter == 2)
                password += c;
            else if (opcode == 5 || opcode == 6 || opcode == 7 || opcode == 9 || opcode == 10)
                courseNumStr += c;
            else if (opcode == 8)
                userName += c;
        } else if (c == ' ')
            counter++;
        else
            break;
    }

    // it was not initialized
    if (opcode == 0)
        opcode = changeTypeToOpcode(type);
}



//==========MSG FROM SERVER

MessageFromServer::MessageFromServer(string text) :
        Messages(move(text)), optional(), opcode(), logoutApproved(), type() {

}

string MessageFromServer::process() {
    print();
    if (logoutApproved == "yes")  // it was a successful logout
        return "terminate";

    else if (logoutApproved == "no")
        return "continue";

    return "";
}


void MessageFromServer::print() {
    setMessageDataServer();

    if (type == "ERROR") {
        cout << type << " " << opcode << endl;
        return;
    }
    if (optional.empty())
        cout << type << " " << opcode << endl;
    else
        cout << type << " " << opcode << '\n' << optional << endl;

}

void MessageFromServer::setMessageDataServer() {
    int counter = 0;
    string typeAndOpcode;

    for (char &c : message) {
        if (c != ' ' && counter == 0)
            type += c;
        else if (c == ' ' && counter == 0)
            counter++;
        else if (c != ' ' && counter == 1) {
            if (c == '\n')
                counter++;
            else
                opcode += c;
        } else
            optional += c;

    }


    typeAndOpcode = type + " " + opcode;


    if (typeAndOpcode == "ACK 4")
        logoutApproved = "yes";
    else if (typeAndOpcode == "ERROR 4")
        logoutApproved = "no";
}
