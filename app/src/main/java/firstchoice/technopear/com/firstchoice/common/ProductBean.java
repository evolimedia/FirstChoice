package firstchoice.technopear.com.firstchoice.common;

/**
 * Created by sanjay on 6/7/16.
 */
public class ProductBean {


    private String companyName;
    private String amount;
    private int box;
    private long id;
    private long acc_id;
    private int type;


    private String date;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(long acc_id) {
        this.acc_id = acc_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
