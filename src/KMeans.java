import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * K-means algorithm implementation
 */
public class KMeans {
    private boolean done = false;
    private final Cluster[] clusters = new Cluster[3]; // Each cluster conforms appropriate time unit
    private final String[] bitCollection; // Bits parsed in collection of 0s and 1s
    private final HashMap<Integer, Integer> dist = new HashMap<>(); // Length of bit line -> number of appearances
    private ArrayList<Integer> keys; // Lengths of 0s and 1s lines, which appears in the bit code

    /**
     * Fills bitCollection, clusters, dist and keys
     * @param bitStream
     */
    public KMeans(String bitStream) {
        /* Filling bitCollection */
        String[] ones = bitStream.split("0+");
        String[] zeros = bitStream.split("1+");

        if (zeros.length == 0) {
            bitCollection = new String[1];
            bitCollection[0] = ones[0];
        } else {
            bitCollection = new String[ones.length + zeros.length - 1];
            for (int i = 0; i < ones.length - 1; i++) {
                bitCollection[2 * i] = ones[i];
                bitCollection[2 * i + 1] = zeros[i + 1];
            }
            bitCollection[bitCollection.length - 1] = ones[ones.length - 1];
        }

        /* Filling dist */
        for (String s : bitCollection) {
            if (!dist.containsKey(s.length())) {
                dist.put(s.length(), 1);
            }
            else dist.put(s.length(), dist.get(s.length()) + 1);
        }

        /* Init keys */
        keys = new ArrayList<>(dist.keySet());

        /* If keys.size() is 1 assume that key[0] is a dot */
        if (keys.size() == 1) {
            initClusters(keys.get(0));
            done = true;
        }
        else if (keys.size() == 2) {
            Collections.sort(keys);
            initClusters(keys.get(0));
        }
        else {
            Collections.sort(keys);
            initClusters();
        }
    }

    /**
     * Init clusters by opening values
     */
    private void initClusters() {
        clusters[0] = new Cluster(keys.get(0));
        clusters[2] = new Cluster(keys.get(keys.size() - 1));
        clusters[1] = new Cluster(
                (clusters[0].location + clusters[2].location) / 2);
    }

    /**
     * Init clusters by point
     */
    private void initClusters(int p) {
        clusters[0] = new Cluster(p);
        clusters[0].addPoint(p);
        clusters[1] = new Cluster(p * 3);
        clusters[2] = new Cluster(p * 7);
    }

    /**
     * Assigns point to the closest cluster
     */
    private void makeAssignment() {
        clear();
        for (Integer i: keys) {
            Cluster bestCluster = new Cluster();
            float closest = Float.MAX_VALUE;

            /* Searching for the closest cluster */
            for (Cluster c: clusters) {
                float d = c.getDistance(i);
                if (d < closest) {
                    closest = d;
                    bestCluster = c;
                }
            }

            /* Pushing all points w/ the same distance */
            for(int j = 0; j < dist.get(i); j++) {
                bestCluster.addPoint(i);
            }
        }
    }

    /**
     * Runs the k-means algorithm
     */
    public void run() {
        if (!done) {
            makeAssignment();
            while (!done) {
                update();
                makeAssignment();
                if (!wasChanged()) done = true;
            }
        }
    }

    /**
     * Clears curPoints for all clusters
     */
    private void clear() {
        for (Cluster c : clusters) c.clearPoints();
    }

    /**
     * Update Cluster.location for all clusters
     */
    private void update() {
        for (Cluster c : clusters) c.update();
    }

    /**
     * Checks for the changes
     * @return
     */
    private boolean wasChanged() {
        for (Cluster c: clusters)
            if (c.wasChanged()) return true;

        return false;
    }

    /**
     * Finds num of cluster by point
     * If no clusters returns -1
     */
    int findClusterByPoint(Integer p) {
        Cluster[] sorted = clusters.clone();
        Arrays.sort(sorted);
        for (int i = 0; i < 3; i++)
            if (sorted[i].currentPoints.contains(p))
                return i;

        return -1;
    }

    /* ------------------------------- CLUSTER CLASS ------------------------------- */

    /**
     * The Cluster class provides data structures and methods
     * for clusters in the KMeans algorithm
     */
    private static class Cluster implements Comparable<Cluster> {
        private float location;
        private float centroid;
        private ArrayList<Integer> currentPoints = new ArrayList<>();
        private ArrayList<Integer> previousPoints = new ArrayList<>();

        /**
         * Constructors
         */
        private Cluster(float loc) {
            location = loc;
        }

        private Cluster() {
            location = -1;
        }

        /**
         * Methods for claiming currentPoints and calculating centroid.
         */
        private void addPoint(int i) {
            currentPoints.add(i);
        }

        private boolean wasChanged() {
            if (previousPoints.size() != currentPoints.size()) return true;
            else return !currentPoints.equals(previousPoints);
        }

        private void clearPoints() {
            previousPoints = (ArrayList<Integer>) currentPoints.clone();
            currentPoints.clear();
        }

        /**
         * After new points have been assigned to this cluster, this method
         * calculates the new centroid of the cluster and moves the cluster
         * to that location.
         */
        private void update() {
            float sum = 0;
            for (Integer p: currentPoints) {
                sum += p;
            }
            centroid = sum / currentPoints.size();
            setLocation(centroid);
        }

        /*
         * Getters and Setters.
         */
        private float getLocation() { return location; }

        private float getDistance(int point) { return Math.abs(location - point); }

        private void setLocation(float loc) { location = loc; }

        /**
         * Overriding comparing function for the ability to sort the clusters
         * @param t
         * @return
         */
        @Override
        public int compareTo(Cluster t) {
            if (this.getLocation() > t.getLocation()) return 1;
            else if (this.getLocation() < t.getLocation()) return -1;
            else return 0;
        }
    }
}