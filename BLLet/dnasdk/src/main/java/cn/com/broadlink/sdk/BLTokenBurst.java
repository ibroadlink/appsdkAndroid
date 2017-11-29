package cn.com.broadlink.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zjjllj on 2017/3/28.
 */

public class BLTokenBurst {
    private static volatile BLTokenBurst mTokenBurst;

    private long token_granularity = 1000;
    private long quota;
    private long interval;

    private long step;
    private long burst;

    private Map<String, Map<String, Long>> apiTokens;

    private BLTokenBurst() {
        quota = 100;
        interval = 180;
        burst = quota * token_granularity;
        step = quota * token_granularity / interval;
        apiTokens = new HashMap<>();
    }

    public static BLTokenBurst getInstance() {
        if (mTokenBurst == null) {
            synchronized (BLTokenBurst.class) {
                if (mTokenBurst == null) {
                    mTokenBurst = new BLTokenBurst();
                }
            }
        }

        return mTokenBurst;
    }

    public Boolean queryTokenBurst(String url) {
        long tokens;
        long token;
        long lastquery;
        long now = System.currentTimeMillis();

        String host;
        int end = url.indexOf("?");
        if (end > 0) {
            host = url.substring(0, end);
        } else {
            host = url;
        }

        Map<String, Long> apiToken = apiTokens.get(host);
        if (apiToken != null) {
            tokens = apiToken.get("tokens");
            lastquery = apiToken.get("lastquery");
        } else {
            tokens = burst;
            lastquery = now;
        }

        long diff = now - lastquery;
        token = diff * step / token_granularity;
        lastquery = now;
        tokens += token;
        if (tokens > burst) {
            tokens = burst;
        }

        if (tokens >= token_granularity) {
            tokens -= token_granularity;
            this.saveTokens(host, tokens, lastquery);
            return true;
        }

        this.saveTokens(host, tokens, lastquery);
        return false;
    }

    private void saveTokens(String host, long tokens, long lastquery) {
        Map<String, Long> apiToken = new HashMap<>();
        apiToken.put("tokens", Long.valueOf(tokens));
        apiToken.put("lastquery", Long.valueOf(lastquery));

        apiTokens.put(host, apiToken);
    }
}
