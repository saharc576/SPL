//
// Created by inbar on 01-Jan-21.
//

#ifndef BOOST_CLIENT_TASKS_H
#define BOOST_CLIENT_TASKS_H
#include "../include/connectionHandler.h"


class Tasks {

public:
    Tasks(ConnectionHandler &);
    void run();

private:
    ConnectionHandler &handler;
};


#endif //BOOST_CLIENT_TASKS_H
