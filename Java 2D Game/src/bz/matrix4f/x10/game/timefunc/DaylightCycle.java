package bz.matrix4f.x10.game.timefunc;

import bz.matrix4f.x10.game.networking.packet.Packet0lGameTime;
import bz.matrix4f.x10.game.networking.server.Server;

public class DaylightCycle {

    public static enum Time {DAY, NIGHT};

    public static final int DAY_LENGTH = 2400;
    public static final int DAY_ALPHA = 0;
    public static final int NIGHT_ALPHA = 167;
    private int time;
    private int ticksSinceLastPacketSend = 0;
    private int alpha;
    private Time dayOrNight;
    private Server server;

    public DaylightCycle(Server server) {
        this.server = server;
    }

    public void tick() {
        time += 1;
        time %= DAY_LENGTH;
        getAlpha(time);

        if(ticksSinceLastPacketSend <= 0) {
            ticksSinceLastPacketSend = 2;

            Packet0lGameTime packet = new Packet0lGameTime(alpha);
            server.sendPacketToAllClients(packet);
        } else {
            ticksSinceLastPacketSend--;
        }
    }

    /**
     * Calculates the alpha value for the lighting
     * layer based upon the in-game time.
     *<br>
     * Values (0) to (DAY_LENGTH / 2 - 1) are reserved for day. (0-1199)<br>
     * Values (1200) to (DAY_LENGTH - 1) are reserved for night. (1200-2399)<br><br>
     *
     * @param time The in-game time
     * @return The alpha value
     */
    private int getAlpha(int time) {
        int alpha = 255;
        if(time >= 0 && time < DAY_LENGTH / 2) {
            dayOrNight = Time.DAY;
            double percentage = ((double) time) / (DAY_LENGTH / 2);
            alpha = (int) (percentage * (NIGHT_ALPHA - DAY_ALPHA)) + DAY_ALPHA;
        } else if(time >= DAY_LENGTH / 2 && time <= DAY_LENGTH) {
            dayOrNight = Time.NIGHT;
            double percentage = ((double) time - (DAY_LENGTH / 2)) / (DAY_LENGTH / 2);
            alpha = NIGHT_ALPHA - (int) (percentage * (NIGHT_ALPHA - DAY_ALPHA));
        }
        this.alpha = alpha;
        return alpha;
    }

    public static int getDayLength() {
        return DAY_LENGTH;
    }

    public Time getDayOrNight() {
        return dayOrNight;
    }

    public static int getNightAlpha() {
        return NIGHT_ALPHA;
    }

    public static int getDayAlpha() {
        return DAY_ALPHA;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
