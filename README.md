# TCP_exchange

README

To run the program:
	1. Run the make command
	2. Call ./server.sh <req_code> to run server
	3. Call ./client.sh <server address> <n_port> <req_code> <msg>
		 to run client
	4. Run make clean to remove *.class
Parameters:
   <server_address>  The name/IP of the machine that the server is running on.
   <n_port> The negotiate port printed out by the sever on startup 
   <req_code>  Needs to be checked in the server. If the client does not send the exact 
	request code, then the server should close the connection to that client.
   <msg> string to be reversed 

Undergrad machines:
	ubuntu1604-006 ubuntu1604-002 ubuntu1604-008
