Group ID: G10

Verification Code: `DC8261178B304058410748D4A107426A`

Used Run Command: `java -Xmx64m -jar A9.jar -s servers.txt -p <port number>` or `./runLocal servers.txt`

Brief Description: We use a push based epidemic protocol to maintain membership information.
These exchanges of "heartbeats" occur at regular intervals. We forward requests
to the correct node depending on the hash value. The recipient's information is
passed along with it so that the final node can send the response directly to the
requester. Caching is done at the final node.

### Exit requirements

`src/main/java/com/g10/CPEN431/A7/Application.java:154`
```java
void cmdShutdown() {
    System.exit(0);
}
```