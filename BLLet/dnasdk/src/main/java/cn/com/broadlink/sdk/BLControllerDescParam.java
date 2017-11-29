package cn.com.broadlink.sdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLControllerDescParam {
    private String scriptfile;
    private String sub_scriptfile;
    private String command;
    private String cookie;
    private String account_id;
    private int ltimeout;
    private int rtimeout;
    private int netmode;
    private int sendcount;

    public String getScriptfile() {
        return scriptfile;
    }

    public void setScriptfile(String scriptfile) {
        this.scriptfile = scriptfile;
    }


    public String getSub_scriptfile() {
        return sub_scriptfile;
    }

    public void setSub_scriptfile(String sub_scriptfile) {
        this.sub_scriptfile = sub_scriptfile;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public int getLtimeout() {
        return ltimeout;
    }

    public void setLtimeout(int ltimeout) {
        this.ltimeout = ltimeout;
    }

    public int getRtimeout() {
        return rtimeout;
    }

    public void setRtimeout(int rtimeout) {
        this.rtimeout = rtimeout;
    }

    public int getNetmode() {
        return netmode;
    }

    public void setNetmode(int netmode) {
        this.netmode = netmode;
    }

    public int getSendcount() {
        return sendcount;
    }

    public void setSendcount(int sendcount) {
        this.sendcount = sendcount;
    }

    public String toJSONStr(){
        /** 组织Desc **/
        JSONObject jDesc = new JSONObject();
        try {
            String scriptfile = getScriptfile();
            String sub_scriptfile = getSub_scriptfile();
            String command = getCommand();
            String cookie = getCookie();
            String account_id = getAccount_id();
            int ltimeout = getLtimeout();
            int rtimeout = getRtimeout();
            int netmode = getNetmode();
            int sendcount = getSendcount();

            if (scriptfile != null) {
                jDesc.put("scriptfile", scriptfile);
            }

            if (sub_scriptfile != null) {
                jDesc.put("sub_scriptfile", scriptfile);
            }

            if (command != null) {
                jDesc.put("command", command);
            }

            if (cookie != null) {
                jDesc.put("cookie", cookie);
            }

            if (account_id != null) {
                jDesc.put("account_id", account_id);
            }

            if (ltimeout != 0) {
                jDesc.put("ltimeout", ltimeout);
            }

            if (rtimeout != 0) {
                jDesc.put("rtimeout", rtimeout);
            }

            if (netmode == 0 || netmode == 1) {
                jDesc.put("netmode", netmode);
            }

            if (sendcount > 0) {
                jDesc.put("sendcount", sendcount);
            }

        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return jDesc.toString();
    }

}
