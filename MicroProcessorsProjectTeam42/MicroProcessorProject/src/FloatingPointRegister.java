public class FloatingPointRegister extends Register {
    private String name;
    private float value;

    private String Qi;
    public FloatingPointRegister(String name) {
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

    public void setValue(float value) {
        this.value = value;
    }


}