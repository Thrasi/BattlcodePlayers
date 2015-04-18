package T103;

public class RobotException extends RuntimeException {

	private static final long serialVersionUID = 8030966858499625882L;
	
	/**
	 * Constructor with message.
	 * @param message message
	 */
	public RobotException(String message) {
		super(message);
	}

}
