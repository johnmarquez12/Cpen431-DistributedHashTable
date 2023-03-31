import argparse

def generate_server_list(start_port, num_servers, server_name):
    with open("servers.txt", "w") as file:
        for i in range(num_servers):
            server = f"{server_name}:{start_port + i}"
            file.write(server + "\n")

    print(f"{num_servers} servers created in server_list.txt")

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("start_port", help="starting port number", type=int)
    parser.add_argument("num_servers", help="number of servers to create", type=int)
    parser.add_argument("server_name", help="name of the server")
    args = parser.parse_args()

    generate_server_list(args.start_port, args.num_servers, args.server_name)
