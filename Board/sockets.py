import socket

# Configure the server socket
host = '0.0.0.0'  # Use '0.0.0.0' to listen on all available interfaces
port = 5555  # Choose a port number

# Create the server socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((host, port))
server_socket.listen(1)  # Listen for one incoming connection

print("Waiting for connection...")

# Accept the incoming connection
client_socket, addr = server_socket.accept()
print("Connected to:", addr)

# Receive and process the data
while True:
    data = client_socket.recv(1024)  # Adjust the buffer size as per your data requirements
    if not data:
        break
    # Process the received data
    print("Received data:", data.decode())

# Close the sockets
client_socket.close()
server_socket.close()
