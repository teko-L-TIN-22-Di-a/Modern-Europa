package core;

public class SleepHelper {

    public static void SleepPrecise(int frameRate, long elapsedTicks) throws InterruptedException {
        SleepPrecise((1000d / frameRate) - (elapsedTicks / 10000d));
    }

    // The following code was taken from the following link:
    // https://blat-blatnik.github.io/computerBear/making-accurate-sleep-function/
    // It tries to wait a save amount and then busy waits to precisely wait a certain amount of time.
    public static void SleepPrecise(double milliseconds) throws InterruptedException {
        if (milliseconds <= 0) return;

        var estimate = 5d;
        var mean = 5d;
        var m2 = 0d;
        var count = 1L;

        long begin;

        while (milliseconds > estimate)
        {
            begin = System.nanoTime();
            Thread.sleep(1);

            var observed = (double)(System.nanoTime() - begin) / 10000d;
            milliseconds -= observed;

            count++;
            var delta = observed - mean;
            mean += delta / count;
            m2 += delta * (observed - mean);
            var stddev = Math.sqrt(m2 / (count - 1));
            estimate = mean + stddev;
        }

        begin = System.nanoTime();
        while ((double)(System.nanoTime() - begin) / 10000d < milliseconds);
    }

}
