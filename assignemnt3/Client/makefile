CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: bin/BGRSclient

@echo 'Building target: BGRSclient'
@echo 'Invoking: C++ Linker'
bin/BGRSclient: bin/connectionHandler.o bin/Messages.o bin/Tasks.o bin/BGRSclient.o
	@echo 'Building target: BGRSclient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/Messages.o bin/Tasks.o bin/BGRSclient.o $(LDFLAGS)
	@echo 'Finished building target: BGRSclient'
	@echo ' '

bin/BGRSclient.o: src/BGRSclient.cpp
	g++ $(CFLAGS) -o bin/BGRSclient.o src/BGRSclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/Messages.o: src/Messages.cpp
	g++ $(CFLAGS) -o bin/Messages.o src/Messages.cpp

bin/Tasks.o: src/Tasks.cpp
	g++ $(CFLAGS) -o bin/Tasks.o src/Tasks.cpp


.PHONY: clean
clean:
	rm -f bin/*
