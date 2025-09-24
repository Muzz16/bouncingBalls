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

	static double magnitude(Vec2 vec){
		return Math.sqrt((vec.x * vec.x) + (vec.y * vec.y));
	}

	static Vec2 subtract(Vec2 vec1, Vec2 vec2){
		return new Vec2(Math.abs(vec2.x - vec1.x),Math.abs(vec2.y - vec1.y) );
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
		balls[1] = new Ball( 2*width / 3, height * 0.9, -0.6, 0.6, 0.3,0.3);
	}

	double calcmv(double mass, double velocity){
		return (mass*(velocity*velocity))/2;
	}


	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		int index = 0;
		for (Ball b : balls) {
			for (int i = index+1; i < balls.length; i++){
				Ball b2 = balls[i];
				Vec2 ball1 = new Vec2(b.x,b.y);
				Vec2 ball2 = new Vec2(b2.x,b2.y);
				double dist = Vec2.dist(ball1,ball2);
				if (dist < (b2.radius + b.radius)){
					double b1mvx = calcmv(b.mass,Math.abs(b.vx));
					double b1mvy = calcmv(b.mass,Math.abs(b.vy));
					double b2mvx = calcmv(b2.mass,Math.abs(b2.vx));
					double b2mvy = calcmv(b2.mass,Math.abs(b2.vy));
					double dott = Math.abs(Vec2.dot(Vec2.normalize(Vec2.subtract(ball1,ball2)),Vec2.normalize(new Vec2(b.vx,b.vy))));
					double netb2x = b2mvx - b1mvx*dott;
					double netb2y = b2mvy - b1mvy*dott;
					double netb1x = b1mvx- b2mvx*dott;
					double netb1y = b1mvy - b2mvy*dott;


					b.vy += netb1x;
					b.vx += netb1y;
					b2.vy += netb2x;
					b2.vx += netb2y;
//test


					if (Vec2.dist(ball1,ball2) < (b2.radius + b.radius)) {
						Vec2 temp = Vec2.normalize(new Vec2(b.vx, b.vy));
						b.y += temp.y * b.vy * deltaT;
						b.x += temp.x * b.vx * deltaT;
						Vec2 temp1 = Vec2.normalize(new Vec2(b2.vx, b2.vy));
						b2.y += temp1.y * b2.vy * deltaT;
						b2.x += temp1.x * b2.vx * deltaT;
					}
				}
			}
			// detect collision with the border
			if (b.x < b.radius || b.x > areaWidth - b.radius) {
				b.vx *= -1; // change direction of ball
			}
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
			index++;
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