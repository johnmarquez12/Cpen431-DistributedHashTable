Name: Harrison Mitgang

Student Number: `36906949`

My architecture relies on a message passing implementation. There are three threads:

1. `UDPServer` listens for requests and hands them off to
2. `ApplicationThread` via a BlockingQueue. The application thread handles the at-most-once 
semantic as well as all the application logic. The response is then passed to the
3. `ReplyThread` where the response is sent back to the client.


### Running instructions

Run with `java -Xmx64m -jar A4.jar`. This will start the server on port `5555`.

<details> 

<summary>Java version 19.0.1</summary>

```
$ java --version
openjdk 19.0.1 2022-10-18
OpenJDK Runtime Environment (build 19.0.1+10-21)
OpenJDK 64-Bit Server VM (build 19.0.1+10-21, mixed mode, sharing)
```

</details>

### Tests

Tests were derived from A2 and exist in a [separate repository](https://bitbucket.org/hmitgang_ubc/cpen431_client/src/main/).

For the most part, my tests checked basic put/get functionality (including errors). I ran
into the most trouble with throughput and memory utilization. Thus, I relied on
two main tests, `profiler` and `getCapacity`, which helped me profile and check
max capacity of the server, respectively.
