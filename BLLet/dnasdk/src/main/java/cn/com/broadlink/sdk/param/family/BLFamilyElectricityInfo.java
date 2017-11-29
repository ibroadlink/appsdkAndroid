package cn.com.broadlink.sdk.param.family;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLFamilyElectricityInfo {

    private String billingaddr;
    private String pvetime;
    private double peakprice;
    private double valleyprice;

    public String getBillingaddr() {
        return billingaddr;
    }

    public void setBillingaddr(String billingaddr) {
        this.billingaddr = billingaddr;
    }

    public String getPvetime() {
        return pvetime;
    }

    public void setPvetime(String pvetime) {
        this.pvetime = pvetime;
    }

    public double getPeakprice() {
        return peakprice;
    }

    public void setPeakprice(double peakprice) {
        this.peakprice = peakprice;
    }

    public double getValleyprice() {
        return valleyprice;
    }

    public void setValleyprice(double valleyprice) {
        this.valleyprice = valleyprice;
    }
}
