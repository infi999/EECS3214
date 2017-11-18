P2P APP

The objective of these two assignments are to implement interaction between hosts. For the P2P app, it requires the following:

The concurrent directory server program runs waiting for a connection from a client.
The clients and the server should (in general) run on different machines.
Each client can send three different meassages to the direcory server -- "JOIN", "LEAVE", and "LIST". "JOIN" and "LEAVE" are for joining and leaving the list appropriate actions. The server needs to be concurrent because multiple clients should be able to talk to it simultaneously. "LIST" message should return all info such as IP address, listening port# of the peer etc, so peer can be contacted direcly. When the program quits, the peer programs should close the sockets.
