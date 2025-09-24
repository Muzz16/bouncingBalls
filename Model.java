package bouncing_balls;

/**
 * The physics model.
 *
 * This class is where you should implement your bouncing balls model.
 *
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 *
 */

class Vec2 {

	Vec2(double x, double y){
		this.x = x;
		this.y = y;
	}

	double x;
	double y;

	static double dot(Vec2 vec1, Vec2 vec2){
		return (vec1.y * vec2.y) + (vec1.x* vec2.x);
	}

	static Vec2 normalize(Vec2 vec){
		double mag = magnitude(vec);
		double normx = vec.x/mag;
		double normy = vec.y/mag;
		return new Vec2(normx, normy);
	}

	 void normalize(){
		double mag = magnitude(new Vec2(x,y));
		this.x = x/mag;
		this.y = y/mag;
	}

	static double magnitude(Vec2 vec){
		return Math.sqrt((vec.x * vec.x) + (vec.y * vec.y));
	}

	static Vec2 subtract(Vec2 vec1, Vec2 vec2){
		return new Vec2(vec2.x - vec1.x, vec2.y - vec1.y);
	}


	static double dist(Vec2 vec1, Vec2 vec2){
		double distance;
		double ycalc = Math.abs(vec2.y - vec1.y);
		double xcalc = Math.abs(vec2.x - vec1.x);
		ycalc = ycalc*ycalc;
		xcalc = xcalc*xcalc;
		distance = Math.sqrt(ycalc + xcalc);
		return distance;
	}
}
class Model {

	double areaWidth, areaHeight;

	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2, 0.2);
		balls[1] = new Ball( 2*width / 3, height * 0.9, -0.6, 0.6, 0.3,0.2);
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		for (int index = 0; index < balls.length; index++) {
			for (int i = index + 1; i < balls.length; i++) {
				Ball b2 = balls[i];
				Ball b = balls[index];
				Vec2 ball1Pos = new Vec2(b.x, b.y);
				Vec2 ball2Pos = new Vec2(b2.x, b2.y);
				double dist = Vec2.dist(ball2Pos, ball1Pos); // distance between the balls

				if (dist <= (b2.radius + b.radius)) { // if we are colliding
					double overlap = ((b2.radius + b.radius) - dist); // how much the balls are overlapping
					// gives us a direction vector between them
					Vec2 normalVector = Vec2.normalize(Vec2.subtract(ball2Pos, ball1Pos));

					// offset ball to prevent it being inside the other ball

					// divide by 2 because both balls will move
					b.x += overlap * normalVector.x * 0.5f;
					b.y += overlap * normalVector.y * 0.5f;

					b2.x -= overlap * normalVector.x * 0.5f;
					b2.y -= overlap * normalVector.y * 0.5f;


					Vec2 ball1V = new Vec2(b.vx, b.vy);
					Vec2 ball2V = new Vec2(b2.vx, b2.vy);

					// how much they move towards each other
					Vec2 relativeVelocity = Vec2.subtract(ball2V, ball1V); // can be seen as total velocity

					// how much velocity is in direction of other ball
					double velocityFactor = Vec2.dot(normalVector, relativeVelocity);
					if (velocityFactor < 0) { // if they are moving towards each other

						// let use get the ratio
						double b1InverseMass = 1.0 / b.mass;
						double b2InverseMass = 1.0 / b2.mass;

						double velocity = -2.0 * velocityFactor / (b1InverseMass + b2InverseMass);

						// add velocity along normalvector with ratio of mass
						b.vx += velocity * normalVector.x * b1InverseMass;
						b.vy += velocity * normalVector.y * b1InverseMass;

						b2.vx -= velocity * normalVector.x * b2InverseMass;
						b2.vy -= velocity * normalVector.y * b2InverseMass;
					}

				}
			}
		}

		for (Ball b : balls) {
			// detect collision with the border
			if (b.x < b.radius) {
				b.vx *= -1; // change direction of ball
				b.x = b.radius;
			} else if( b.x > areaWidth - b.radius){
				b.vx *= -1;
				b.x = areaWidth - b.radius;
			}

			// y axis wall check
			if (b.y < b.radius) {
				b.vy *= -1;
				b.y = b.radius;
			}
			else if (b.y > areaHeight - b.radius) {
				b.vy *= -1;
			}

			b.vy -= deltaT * 9.82;

			// compute new position according to the speed of the ball
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;
		}
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {

		Ball(double x, double y, double vx, double vy, double r, double mass) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.mass = mass;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius, mass;
	}
}