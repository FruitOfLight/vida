public class CONST {

	// GUI
	public static final int windowWidth = 1000;
	public static final int windowHeight = 700;
	public static final int graphWidth = 700;
	public static final int menuHeight = 25;
	public static final int queueHeight = 75;
	public static final int graphHeight = 500;
	public static final int controlsHeight = 50;
	public static final int controlsWidth = 700;

	// Graph
	public static final int vertexSize = 10;

	// Keys
	public static final int shiftKey = 16;
	public static final int controlKey = 17;

	// Queue
	public static final double speedFactor = 1.2;

	// o kolko sa zrychli beh, pri stlaceni forward

	// Model
	// public static final int running = 2;
	// public static final int paused = 1;
	// public static final int stoped = 0;

	public static int AnonymToInt(Anonym a) {
		if (a == Anonym.anonymOff)
			return 0;
		return 1;
	}

	public static int SynchronedToInt(Synchroned a) {
		if (a == Synchroned.synchronedOff)
			return 0;
		return 1;
	}

	public static int GraphTypeToInt(GraphType g) {
		if (g == GraphType.clique)
			return 1;
		if (g == GraphType.cycle)
			return 2;
		return 0;
	}

	public static Anonym IntToAnonym(int a) {
		if (a == 0)
			return Anonym.anonymOff;
		return Anonym.anonymOn;
	}

	public static Synchroned IntToSynchroned(int a) {
		if (a == 0)
			return Synchroned.synchronedOff;
		return Synchroned.synchronedOn;
	}

	public static GraphType IntToGraphType(int a) {
		if (a == 1)
			return GraphType.clique;
		if (a == 2)
			return GraphType.cycle;
		return GraphType.none;
	}

}

enum RunState {
	stopped, paused, running
}

enum Preference {
	begin, end, special
}

enum MessageState {
	born, main, sleep, dead
}

enum Anonym {
	anonymOn, anonymOff
}

enum Synchroned {
	synchronedOn, synchronedOff
}

enum GraphType {
	none, cycle, clique
}
