package teropa.globetrotter.client.common;

public enum Direction {
	UP, RIGHT, DOWN, LEFT;

	public Direction nextClockwise() {
		switch (this) {
		case UP: return RIGHT;
		case RIGHT: return DOWN;
		case DOWN: return LEFT;
		case LEFT: return UP;
		}
		return null;
	}
	
}