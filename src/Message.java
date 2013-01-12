class Message {
    int fromPort;
    int toPort;
    Edge edge;
    String content;

    public Message(int port, String content) {
        this.fromPort = port;
        this.content = content;
    }

}
