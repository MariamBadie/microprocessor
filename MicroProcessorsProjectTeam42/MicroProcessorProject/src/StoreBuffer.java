public class StoreBuffer {
        Object[][] buffer;
        public StoreBuffer(int size){
            buffer = new Object[size][5];
            for (int i = 0 ; i < buffer.length ; i++){
                buffer[i][0] = 0;
                buffer[i][1] = 0;
                buffer[i][2] = 0.0f;
                buffer[i][3] = "";
                buffer[i][4] = 0;
            }
        }
    public int getBusy(int row){
        if (row >= buffer.length)
            return -1;
        return (int) buffer[row][0];
    }

    public int getA(int row){
        if (row >= buffer.length)
            return -1;
        return (int)  buffer[row][1];
    }
    public float getV(int row){
        if (row >= buffer.length)
            return -1;
        return (float) buffer[row][2];
    }

    public String getQ(int row){
        if (row >= buffer.length)
            return "";
        return (String) buffer[row][3];
    }

    public int getTimer(int row){
        if (row >= buffer.length)
            return -1;
        return (int) buffer[row][4];
    }

    public int getFirstAvailableRow(){
        for (int i = 0 ; i < buffer.length ; i++)
            if (getBusy(i) == 0)
                return i;
        return -1;
    }
        public void setBusy(int row , int busy){
            buffer[row][0] = busy;
        }
        public void setA(int row , int A){
            buffer[row][1] = A;
        }
    public void setV(int row , float V){
        buffer[row][2] = V;
    }
    public void setQ(int row , String Q){
        buffer[row][3] = Q;
    }
    public void setTimer(int row , int timer){
            buffer[row][4] = timer;
    }

}
