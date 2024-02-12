public class ReservationStation {

    Object[][] RS;
    public ReservationStation(int size){
        RS = new Object[size][9];
        for (int i = 0 ; i < RS.length ; i++){
            RS[i][0] = 0;
            RS[i][1] = "";
            RS[i][2] = 0.0f;
            RS[i][3] = 0.0f;
            RS[i][4] = "";
            RS[i][5] = "";
            RS[i][6] = 0.0f;
            RS[i][7] = 0;
            RS[i][8] = "";
        }
    }


    public int getBusy(int row){
        if (row >= RS.length)
            return -1;
       return (int) RS[row][0];
    }

    public String getOP(int row){
        if (row >= RS.length)
            return "";
        return (String) RS[row][1];
    }

    public float getVJ(int row){
        if (row >= RS.length)
            return -1;
        return (float) RS[row][2];
    }
    public float getVK(int row){
        if (row >= RS.length)
            return -1;
        return (float) RS[row][3];
    }
    public String getQJ(int row){
        if (row >= RS.length)
            return "";
        return (String) RS[row][4];
    }
    public String getQK(int row){
        if (row >= RS.length)
            return null;
        return (String) RS[row][5];
    }
    public float getA(int row){
        if (row >= RS.length)
            return -1;
        return (float) RS[row][6];
    }
    public int getTimer(int row){
        if (row >= RS.length)
            return -1;
        return (int) RS[row][7];
    }
    public String getCurrentStation(int row){
        if (row >= RS.length)
            return null;
        return (String) RS[row][8];
    }

    public void setBusy(int row , int busy){
        RS[row][0] = busy;
    }
    public void setOP(int row , String OP){
        RS[row][1] = OP;
    }
    public void setVJ(int row , float VJ){
        RS[row][2] = VJ;
    }
    public void setVK(int row , float VK){
        RS[row][3] = VK;
    }
    public void setQJ(int row , String QJ){
        RS[row][4] = QJ;
    }
    public void setQK(int row , String QK){
        RS[row][5] = QK;
    }
    public void setA(int row , float A){
        RS[row][6] = A;
    }
    public void setTimer(int row , int timer){
        RS[row][7] = timer;
    }

    public void setCurrentStation(int row , String currStation){
        RS[row][8] = currStation;
    }

    public int getFirstAvailableRow(){
        for (int i = 0 ; i < RS.length ; i++)
            if (getBusy(i) == 0)
                return i;
        return -1;
    }

}
