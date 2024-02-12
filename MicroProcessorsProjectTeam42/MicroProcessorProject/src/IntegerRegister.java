public class IntegerRegister extends Register {
    private String name;
    private float value;

    private String Qi;
    public IntegerRegister(String name) {
        super(name);
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public String getQi() {
        return Qi;
    }

    public void setQi(String qi) {
        Qi = qi;
    }

    public void setValue(int value) {
        this.value = value;
    }


}