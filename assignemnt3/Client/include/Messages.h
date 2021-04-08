//
// Created by inbar on 01-Jan-21.
//

#ifndef BOOST_CLIENT_MESSAGES_H
#define BOOST_CLIENT_MESSAGES_H

#include <string>
#include <vector>
#include <boost/lexical_cast.hpp>

using namespace std;

class Messages {
        public:

        Messages(string txt);
        virtual ~Messages();
        virtual string process () = 0;


protected:
    string message;

};

////==================================================

class MessageFromClient : public Messages {
public:
    MessageFromClient(string text);
    string process();
    short changeTypeToOpcode(const string&);
    bool isLogoutMsg() const;
protected:
    void setMessageDataClient();


private:
        string type;
        string userName;
        string password;
        string courseNumStr;
        short opcode;
        bool logoutMsg;
};

////==================================================

class MessageFromServer : public Messages {
public:
    MessageFromServer(string text);
    string process();
    void print ();

protected:
    void setMessageDataServer();

private:
    string optional;
    string opcode;
    string logoutApproved;
    string type;


};


#endif //BOOST_CLIENT_MESSAGES_H

