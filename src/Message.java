

class Message{
	String from;
	String to;
	int fromPort;
	int toPort;
	String content;
	public Message(String from, String to, String content){
		this.from = from;
		this.to = to;
		this.content = content;
		fromPort = Wrapper.random.nextInt(12);
		toPort = Wrapper.random.nextInt(12);
	}
	
}