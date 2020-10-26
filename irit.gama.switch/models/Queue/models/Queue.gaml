/**
* Name: Queue
* Queue demo. 
* Author: Jean-François Erdelyi
* Tags: 
*/
model Queue

global {
	float road_width <- 3.5 #m const: true;
	float road_height <- 30 #m const: true;
	float car_width <- 1.5 #m const: true;
	float car_height <- 4.0 #m const: true;
	geometry shape <- rectangle(road_width, road_height);
	float step <- 1 #s;
	
	// Cars generator rate (+1 => arithmetic error if value is 0)
	int generate_frequency <- 100 update: rnd(100, 1000) + 1;

	init {
		create queue_road;
	}

	// Generate cars
	reflex generate when: (cycle mod generate_frequency) = 0 {
		create car returns: new_car;
		
		ask (queue_road) {
			do add_car(new_car[0]);
		}
	}

}

species car {
	geometry shape <- rectangle(car_width, car_height) at_location point(road_width / 2.0, road_height);
	float time;

	aspect default {
		draw shape color: #red;
	}

}

species queue_road {
	int nb_car_max <- int(floor(road_height / car_height + 1.0));
	queue<car> cars_queue;
	geometry shape <- rectangle(road_width, road_height) at_location point(road_width / 2.0, road_height / 2.0);
	float time_to_travel <- road_height / 8.33333;

	/*reflex call_car when: length(cars) > 0 and first(cars).time + time_to_travel <= time {
		car c <- pop(cars);
		ask c {
			do die();
		}

	}*/

	action add_car (car new_car) {
		if (length(cars_queue) < nb_car_max) {
			new_car.location <- {road_width / 2.0, length(cars_queue) * car_height + 0.5 + (car_height / 2.0)};
			new_car.time <- time;
			push new_car to: cars_queue;
			//add new_car to: cars_queue;
			write "" + first(cars_queue).time;
		}

	}

	aspect default {
		draw shape color: #darkgray;
	}

}

experiment Queue type: gui {
	output {
		display main_window type: opengl {
			species queue_road;
			species car;
		}

	}

}
