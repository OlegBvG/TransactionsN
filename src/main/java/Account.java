public class Account
{
    private String accNumber;
    private long money;
    private boolean isLocked;

    public Account(String accNumber, long money, boolean isLocked) {
        this.accNumber = accNumber;
        this.money = money;
        this.isLocked = isLocked;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void addMoney(long money) {
        this.money += money;
    }

    public void debetMoney(long money) {
        this.money -= money;
    }


    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

}
