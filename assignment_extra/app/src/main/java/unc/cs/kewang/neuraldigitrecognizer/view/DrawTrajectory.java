package unc.cs.kewang.neuraldigitrecognizer.view;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kewang on 4/17/17.
 */

public class DrawTrajectory {
    static class TrajectoryPoint {
        float x;
        float y;

        private TrajectoryPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Trajectory {
        private List<TrajectoryPoint> points = new ArrayList<>();
        private int color;

        private Trajectory() {
        }

        private void addPoint(TrajectoryPoint pt) {
            points.add(pt);
        }

        public int getTrajectoryPointsNumber() {
            return points.size();
        }

        public TrajectoryPoint getTrajectoryPoint(int index) {
            return points.get(index);
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }

    private Trajectory mCurrentTrajectory;
    private List<Trajectory> mTrajectories = new ArrayList<>();

    public DrawTrajectory() {
    }

    public void startTrajectory(float x, float y) {
        mCurrentTrajectory = new Trajectory();
        mCurrentTrajectory.addPoint(new TrajectoryPoint(x, y));
        mTrajectories.add(mCurrentTrajectory);
    }

    public void endTrajectory() {
        mCurrentTrajectory = null;
    }

    public void addPoint(float x, float y) {
        if (mCurrentTrajectory != null) {
            mCurrentTrajectory.addPoint(new TrajectoryPoint(x, y));
        }

    }

    public int getTotalTrajectoryNumber() {
        return mTrajectories.size();
    }

    public Trajectory getTrajectory(int index) {
        return mTrajectories.get(index);
    }

    public void setTrajectoryColor(int color) {
        if (mCurrentTrajectory != null) {
            mCurrentTrajectory.setColor(color);
        }
    }

    public void setTrajectoryColor(int red, int green, int blue) {
        int color = 0xFF000000;
        color |= (red << 16);
        color |= (green << 8);
        color |= blue;
        if (mCurrentTrajectory != null) {
            mCurrentTrajectory.setColor(color);
        }
    }

    public void clear() {
        mTrajectories.clear();
    }
}
