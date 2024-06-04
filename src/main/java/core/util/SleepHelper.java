package core.util;

public class SleepHelper {

    public static void SleepPrecise(int frameRate, long elapsedTicks) {
        SleepPrecise((1000d / frameRate) - (elapsedTicks / 1000000d));
    }

    // The following code was taken from the following link:
    // https://blat-blatnik.github.io/computerBear/making-accurate-sleep-function/
    // It tries to wait a save amount and then busy waits to precisely wait a certain amount of time.
    public static void SleepPrecise(double milliseconds) {
        if (milliseconds <= 0)  return;

        var estimate = 5d;
        var mean = 5d;
        var m2 = 0d;
        var count = 1L;

        long begin;

        while (milliseconds > estimate)
        {
            begin = System.nanoTime();

            try {
                Thread.yield();
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            var observed = (double)(System.nanoTime() - begin) / 1000000d;
            milliseconds -= observed;

            count++;
            var delta = observed - mean;
            mean += delta / count;
            m2 += delta * (observed - mean);
            var stddev = Math.sqrt(m2 / (count - 1));
            estimate = mean + stddev;
        }

        begin = System.nanoTime();
        while ((double)(System.nanoTime() - begin) / 1000000d < milliseconds);
    }

}
