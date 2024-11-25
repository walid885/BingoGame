package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Urn {
    private List<Integer> balls;
    private Random random;

    public Urn() {
        balls = new ArrayList<>();
        random = new Random();
        resetUrn();
    }

    public void resetUrn() {
        balls.clear();
        for (int i = 0; i < 10; i++) {
            balls.add(i);
        }
    }

    public int drawBall() {
        if (balls.isEmpty()) {
            return -1;
        }
        int index = random.nextInt(balls.size());
        return balls.remove(index);
    }

    public boolean isEmpty() {
        return balls.isEmpty();
    }
}