public class LoadBuffer {
    int[][] buffer;
    public LoadBuffer(int size){
        buffer = new int[size][3];


    }
    public int getBusy(int row){
        if (row >= buffer.length)
            return -1;
        return buffer[row][0];
    }

    public int getA(int row){
        if (row >= buffer.length)
            return -1;
        return buffer[row][1];
    }
    public int getTimer(int row){
        if (row >= buffer.length)
            return -1;
        return buffer[row][2];
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
    public void setTimer(int row , int timer){
        buffer[row][2] = timer;
    }
    public void setA(int row , int A){
        buffer[row][1] = A;
    }


}
