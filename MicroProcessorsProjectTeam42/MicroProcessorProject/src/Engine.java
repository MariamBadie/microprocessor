import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class Engine {

    int daddLatency = 3;
    int dsubLatency = 3;
    int muldLatency = 3;
    int divdLatency = 3;

    int subdLatency = 3;

    int adddLatency = 3;
    int addiLatency = 1;
    int subiLatency = 1;
    int bnezLatency = 1;
    int loadLatency = 3;
    int ldd = 3;
    int storeLatency = 3;

    int sdd = 3;
    int addReservationStationSize = 3;
    int mulReservationStationSize = 2;
    int loadBufferSize = 3;
    int storeBufferSize = 3;

    boolean isBranch = false;

    Hashtable<Integer, Register> integerRegisters;
    Hashtable<Integer, Register> floatingPointRegister;

    ReservationStation addReservationStation;
    ReservationStation mulReservationStation;
    LoadBuffer loadBuffer;
    StoreBuffer storeBuffer;

    ArrayList<String> queue;
    ArrayList<String> queueStations = new ArrayList<>();

    ArrayList<String> instructions;

    int ptr;

    static int clock = 1;

    ArrayList<String> writeBackQueue = new ArrayList<>();
    ArrayList<Float> writeBackQueueValues = new ArrayList();


    String Issued = "";


    float[] cache = new float[1024];

    PrintingSimulationGUI gui;

    public Engine() throws Exception{
                gui = new PrintingSimulationGUI();

                 daddLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER DADD LATENCY"));
                 dsubLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER DSUB LATENCY"));
                 divdLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER DIV.D LATENCY"));
                  muldLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER MUL.D LATENCY"));
                  loadLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER LD LATENCY"));
                  storeLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER SD LATENCY"));
                  adddLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER ADD.D LATENCY"));
                  subdLatency = Integer.parseInt(gui.getUserInput("PLEASE ENTER SUB.D LATENCY"));
                  ldd = Integer.parseInt(gui.getUserInput("PLEASE ENTER L.D LATENCY"));
                  sdd = Integer.parseInt(gui.getUserInput("PLEASE ENTER S.D LATENCY"));
                  loadBufferSize = Integer.parseInt(gui.getUserInput("PLEASE ENTER LOAD BUFFER Size"));
                  storeBufferSize = Integer.parseInt(gui.getUserInput("PLEASE ENTER STORE BUFFER SIZE"));
                  addReservationStationSize = Integer.parseInt(gui.getUserInput("PLEASE ENTER ADD RESERVATION STATION SIZE"));
                  mulReservationStationSize = Integer.parseInt(gui.getUserInput("PLEASE ENTER MULTIPLY RESERVATION STATION SIZE"));









                  
                                                                                   this.integerRegisters = new Hashtable<>();
                                                                                   this.floatingPointRegister = new Hashtable<>();
                                                                                   for (int i = 0; i < 32; i++) {
                                                                                       Register register = new Register("R" + i);
                                                                                       integerRegisters.put(i, register);
                                                                                       floatingPointRegister.put(i , new Register("F" + i));
                                                                                   }

        this.addReservationStation = new ReservationStation(addReservationStationSize);
        this.mulReservationStation = new ReservationStation(mulReservationStationSize);
        this.loadBuffer = new LoadBuffer(loadBufferSize);
        this.storeBuffer = new StoreBuffer(storeBufferSize);
        this.queue = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.initializeInstructions();
        Scanner sc = new Scanner(System.in);

        ptr = 0;


        processInstructions();

    }

    public void processInstructions(){
        while (true){
            if (ptr >= instructions.size() && emptyStations()){
                break;
            }
            System.out.println("CLOCK CYCLE : " + clock);

            issue();
            execute();
            writeBack();

            printQueue();
            printRegFile();
            printAddReservationStation();
            printMulReservationStation();
            printStoreBuffer();
            printLoadBuffer();
            System.out.println();

            clock++;

        }
    }
    public void printRegFile(){
        System.out.println("Register Name     Qi     Value");
        for (int i = 0 ; i < integerRegisters.size() ; i++){
            Register r = integerRegisters.get(i);
            System.out.println(r.getName() + "                 " + r.getQi() + "      " + r.getValue());
        }
        for (int i = 0 ; i < floatingPointRegister.size() ; i++){
            Register r = floatingPointRegister.get(i);
            System.out.println(r.getName() + "                 " + r.getQi() + "      " + r.getValue());
        }
    }

    public boolean emptyStations(){
        boolean addReservationStationAvailable = false;
        for (int i = 0 ; i < addReservationStation.RS.length ; i++){
            if (addReservationStation.getBusy(i) == 1){
                addReservationStationAvailable = true;
                break;
            }
        }
        boolean mulReservationStationAvailable = false;
        for (int i = 0 ; i < mulReservationStation.RS.length ; i++){
            if (mulReservationStation.getBusy(i) == 1){
                mulReservationStationAvailable = true;
                break;
            }
        }
        boolean loadBufferAvailable = false;
        for (int i = 0 ; i < loadBuffer.buffer.length ; i++){
            if (loadBuffer.getBusy(i) == 1){
                loadBufferAvailable = true;
                break;
            }
        }

        boolean storeBufferAvailable = false;
        for (int i = 0 ; i < storeBuffer.buffer.length ; i++){
            if (storeBuffer.getBusy(i) == 1){
                storeBufferAvailable = true;
                break;
            }
        }

        return !storeBufferAvailable && !loadBufferAvailable && !addReservationStationAvailable && !mulReservationStationAvailable;

    }


    public int getQueueIssue(int row){
        return Integer.parseInt(queue.get(row).split(" ")[4]);
    }
    public boolean haveExecute(int row){
        return queue.get(row).split(" ").length > 5;
    }
    public float executeInstruction(float operand1 , float operand2 , String operation, int ldValue, int branchVal){
        if (operation.toLowerCase().equals("add.d") || operation.toLowerCase().equals("add")){
            return operand1 + operand2;
        }
        if (operation.toLowerCase().equals("sub.d")){
            return operand1 - operand2;
        }
        if (operation.toLowerCase().equals("mul.d") || operation.toLowerCase().equals("mul")){
            return operand1 * operand2;
        }
        if (operation.toLowerCase().equals("div.d")){
            return operand1 / operand2;
        }
        if (operation.toLowerCase().equals("addi")){
            return operand1 + operand2;
        }
        if (operation.toLowerCase().equals("subi")){
            return operand1 - operand2;
        }
        if (operation.toLowerCase().equals("dadd")){
            return operand1 + operand2;
        }
        if (operation.toLowerCase().equals("l.d")){
            return cache[ldValue];
        }
        if (operation.toLowerCase().equals("bnez")){
            // bnez r1 , 100
            if (operand1 != 0.0f){
                ptr = branchVal;
                return branchVal;
            }
            return ptr;
        }
        return -1;

    }


    public void printAddReservationStation(){
            System.out.println("Station   BUSY      OP      VJ       VK      QJ      QK     A");
                for (int i = 0 ; i < addReservationStationSize ; i++){
                    System.out.print("A" + (i+1) + "         ");
                    String op = "-";
                    if (!addReservationStation.getOP(i).equals(""))
                        op = addReservationStation.getOP(i);

                    System.out.print(addReservationStation.getBusy(i) + "        " + op + "      " + addReservationStation.getVJ(i)
                    + "      " + addReservationStation.getVK(i) + "      ");
                    if (addReservationStation.getQJ(i).equals(""))
                        System.out.print("-" + "       ");
                    else
                        System.out.print(addReservationStation.getQJ(i) + "       ");

                    if (addReservationStation.getQK(i).equals(""))
                        System.out.print("-" + "      ");
                    else
                        System.out.print(addReservationStation.getQK(i) + "      ");


                    if (addReservationStation.getA(i) == 0)
                        System.out.print("-" + "      ");
                    else
                        System.out.println(addReservationStation.getA(i));


                    System.out.println();
                }
                System.out.println();
                System.out.println();

        }

        public void printMulReservationStation(){
            System.out.println("Station   BUSY      OP      VJ       VK      QJ      QK     A");
                for (int i = 0 ; i < mulReservationStationSize ; i++){
                    System.out.print("M" + (i+1) + "         ");
                    String op = "-";
                    if (!mulReservationStation.getOP(i).equals(""))
                        op = mulReservationStation.getOP(i);

                    System.out.print(mulReservationStation.getBusy(i) + "        " + op + "      " + mulReservationStation.getVJ(i)
                    + "      " + mulReservationStation.getVK(i) + "      ");
                    if (mulReservationStation.getQJ(i).equals(""))
                        System.out.print("-" + "       ");
                    else
                        System.out.print(mulReservationStation.getQJ(i) + "       ");

                    if (mulReservationStation.getQK(i).equals(""))
                        System.out.print("-" + "      ");
                    else
                        System.out.print(mulReservationStation.getQK(i) + "      ");


                    if (mulReservationStation.getA(i) == 0)
                        System.out.print("-" + "      ");
                    else
                        System.out.println(mulReservationStation.getA(i));


                    System.out.println();
                }
                System.out.println();
                System.out.println();

        }

        public void printStoreBuffer(){
            System.out.println("Station   BUSY      A      V       Q");
                for (int i = 0 ; i < storeBufferSize ; i++){
                    System.out.print("S" + (i+1) + "         ");
                    System.out.print(storeBuffer.getBusy(i) +"       " + storeBuffer.getV(i)+ "      ");

                    if (storeBuffer.getA(i) == 0)
                        System.out.print("-" + "      ");
                    else
                        System.out.print(storeBuffer.getA(i) + "       ");


                    if (storeBuffer.getQ(i).equals(""))
                        System.out.print("-" + "       ");
                    else
                        System.out.println(storeBuffer.getQ(i));



                    System.out.println();
                }
                System.out.println();
                System.out.println();

        }

    public void issue(){
/*
        int daddLatency = 3;
        int dsubLatency = 3;
        int muldLatency = 3;
        int divdLatency = 3;
        int addiLatency = 3;
        int subiLatency = 3;
        int bnezLatency = 3;
        int loadLatency = 3;
        int storeLatency = 3;

 */
        if (ptr >= this.instructions.size() || isBranch)
            return;
        // S.D F2 100
        // L.D F1 100
        String currInstruction = this.instructions.get(ptr);
        String[] instructionSplit = currInstruction.split(" ");
        String operation = instructionSplit[0];
        String destinationOperand = instructionSplit[1];
        String firstOperand = instructionSplit[2];


        if (operation.toLowerCase().equals("bnez")){
            isBranch = true;
            int row = addReservationStation.getFirstAvailableRow();
            if (row == -1) {
                return;
            }
            Register destination;
            if (destinationOperand.split("")[0].toLowerCase().equals("r")){
                destination = getRegisterByNameI(destinationOperand);
            }
            else {
                destination = getRegisterByNameF(destinationOperand);
            }
            Register firstOp;
            //queue.set(queuePtr , queue.get(queuePtr) + " " + clock);
            addReservationStation.setBusy(row , 1);
            addReservationStation.setOP(row , operation);
            addReservationStation.setVJ(row , destination.getValue());


            addReservationStation.setVK(row , 0);
            addReservationStation.setQJ(row , destination.getQi());
            addReservationStation.setQK(row , "0");
            addReservationStation.setA(row ,Float.parseFloat(firstOperand) );

            // ------------------- EDIT
            addReservationStation.setTimer(row , bnezLatency);
            queueStations.add("A" + (row+1));
            // ----------------------------

            //destination.setQi("A" + (row+1));
            Issued = "A" + (row+1);
            queue.add(instructions.get(ptr) + " --- " + clock);
            ptr++;
            return;
        }
        if (operation.toLowerCase().equals("l.d") || operation.toLowerCase().equals("ld")){
            Register destination;
            if (destinationOperand.split("")[0].toLowerCase().equals("r")){
                destination = getRegisterByNameI(destinationOperand);
            }
            else {
                destination = getRegisterByNameF(destinationOperand);
            }
            int row = loadBuffer.getFirstAvailableRow();
            if (row == -1)
                return;
            //queue.set(queuePtr , queue.get(queuePtr) + " " + clock);
            loadBuffer.setBusy(row , 1);
            loadBuffer.setA(row , Integer.parseInt(firstOperand));

            // ------------------- EDIT
            if (operation.toLowerCase().equals("l.d"))
                loadBuffer.setTimer(row , ldd);
            else
                loadBuffer.setTimer(row , loadLatency);
            queueStations.add("L" + (row+1));
            // ----------------------------

            destination.setQi("L" + (row+1));
            Issued = "L" + (row+1);

            queue.add(instructions.get(ptr) + " ---- " + clock);
            ptr++;
            return;
        }

        if (operation.toLowerCase().equals("s.d") || operation.toLowerCase().equals("sd")){
            Register destination;
            if (destinationOperand.split("")[0].toLowerCase().equals("r")){
                destination = getRegisterByNameI(destinationOperand);
            }
            else {
                destination = getRegisterByNameF(destinationOperand);
            }
            int row = storeBuffer.getFirstAvailableRow();
            if (row == -1)
                return;
            //queue.set(queuePtr , queue.get(queuePtr) + " " + clock);
            storeBuffer.setBusy(row , 1);
            storeBuffer.setA(row , Integer.parseInt(firstOperand));
            storeBuffer.setQ(row , destination.getQi());
            storeBuffer.setV(row , destination.getValue());
            // ------------------- EDIT
            if (operation.toLowerCase().equals("s.d"))
                storeBuffer.setTimer(row , sdd);
            else
                storeBuffer.setTimer(row , storeLatency);

            queueStations.add("S" + (row+1));
            // ----------------------------

            //destination.setQi("S" + (row+1));
            Issued = "S" + (row+1);

            queue.add(instructions.get(ptr) + " ---- " + clock);
            ptr++;
            return;
        }

        String secondOperand = instructionSplit[3];

        Register destination;
        Register firstOp;
        Register secondOp;

        if (destinationOperand.split("")[0].toLowerCase().equals("r")){
            destination = getRegisterByNameI(destinationOperand);
        }
        else {
            destination = getRegisterByNameF(destinationOperand);
        }
        if (firstOperand.split("")[0].toLowerCase().equals("r")){
            firstOp = getRegisterByNameI(firstOperand);
        }
        else {
            firstOp = getRegisterByNameF(firstOperand);
        }
        if (!operation.toLowerCase().equals("addi") && !operation.toLowerCase().equals("subi")) {
            if (secondOperand.split("")[0].toLowerCase().equals("r")) {
                secondOp = getRegisterByNameI(secondOperand);
            } else {
                secondOp = getRegisterByNameF(secondOperand);
            }
        }
        else {
            secondOp = new Register("tempReg");
            secondOp.setValue(Float.parseFloat(secondOperand));
        }

        if (operation.toLowerCase().equals("add.d") || operation.toLowerCase().equals("sub.d") ||
                operation.toLowerCase().equals("addi") || operation.toLowerCase().equals("subi")
        || operation.toLowerCase().equals("bnez") || operation.toLowerCase().equals("add")){
            int row = addReservationStation.getFirstAvailableRow();
            if (row == -1) {
                return;
            }
            //queue.set(queuePtr , queue.get(queuePtr) + " " + clock);
            addReservationStation.setBusy(row , 1);
            addReservationStation.setOP(row , operation);
            addReservationStation.setVJ(row , firstOp.getValue());

            addReservationStation.setVK(row , secondOp.getValue());
            addReservationStation.setQJ(row , firstOp.getQi());
            addReservationStation.setQK(row , secondOp.getQi());
            addReservationStation.setA(row , 0);

            // ------------------- EDIT
            /*
            operation.toLowerCase().equals("add.d") || operation.toLowerCase().equals("sub.d") ||
                operation.toLowerCase().equals("addi") || operation.toLowerCase().equals("subi")
        || operation.toLowerCase().equals("bnez") || operation.toLowerCase().equals("add")
            */
            if (operation.toLowerCase().equals("add.d"))
                addReservationStation.setTimer(row , adddLatency);
            else if (operation.toLowerCase().equals("sub.d"))
                addReservationStation.setTimer(row , subdLatency);
            else if (operation.toLowerCase().equals("addi"))
                addReservationStation.setTimer(row , addiLatency);
            else if (operation.toLowerCase().equals("subi"))
                addReservationStation.setTimer(row , subiLatency);
            else if (operation.toLowerCase().equals("add"))
                addReservationStation.setTimer(row , daddLatency);




            queueStations.add("A" + (row+1));
            // ----------------------------

            destination.setQi("A" + (row+1));
            Issued = "A" + (row+1);
            queue.add(instructions.get(ptr) + " " + clock);
            ptr++;
        }


        if (operation.toLowerCase().equals("mul.d") || operation.toLowerCase().equals("div.d") ||
                operation.toLowerCase().equals("mul") || operation.toLowerCase().equals("div")){
            int row = mulReservationStation.getFirstAvailableRow();
            if (row == -1)
                return;
            //queue.set(queuePtr , queue.get(queuePtr) + " " + clock);
            mulReservationStation.setBusy(row , 1);
            mulReservationStation.setOP(row , operation);
            mulReservationStation.setVJ(row , firstOp.getValue());

            mulReservationStation.setVK(row , secondOp.getValue());
            mulReservationStation.setQJ(row , firstOp.getQi());
            mulReservationStation.setQK(row , secondOp.getQi());
            mulReservationStation.setA(row , 0);

            // ------------------- EDIT
            if (operation.toLowerCase().equals("mul.d"))
                mulReservationStation.setTimer(row , muldLatency);
            else if (operation.toLowerCase().equals("div.d"))
                mulReservationStation.setTimer(row , divdLatency);

            queueStations.add("M" + (row+1));
            // ----------------------------

            destination.setQi("M" + (row+1));
            Issued = "M" + (row+1);
            queue.add(instructions.get(ptr) + " " + clock);
            ptr++;
        }

    }



    public void printQueue(){
        System.out.println("Operation    Destination    First Operand   Second Operand    Issue    Execute      Write Back ");
        for (int i = 0 ; i < queue.size() ; i++) {
            String[] splitQueue = queue.get(i).split(" ");

                System.out.print("  " + splitQueue[0] + "            " + splitQueue[1] + "              "
                        + splitQueue[2] + "               " + splitQueue[3] + "            " + splitQueue[4]);
                if (splitQueue.length > 5) {
                    System.out.print("      " + splitQueue[5]);
                }
                if (splitQueue.length > 6) {
                    System.out.print("        " + splitQueue[6]);
                }
                System.out.println();
            }
        System.out.println();
    }
    public Register getRegisterByNameI(String regName){
        return integerRegisters.get(Integer.parseInt(regName.substring(1)));
    }
    public Register getRegisterByNameF(String regName){
        return floatingPointRegister.get(Integer.parseInt(regName.substring(1)));

    }

    public void execute(){
        for (int i = 0 ; i < addReservationStationSize ; i++){
            String currentStation = "A" + (i+1);
            if (Issued.equals(currentStation)) {
                Issued = "";
                continue;
            }
            if (addReservationStation.getBusy(i) == 1){
                if (addReservationStation.getQJ(i).equals("0") && addReservationStation.getQK(i).equals("0")) {
                    addReservationStation.setTimer(i, addReservationStation.getTimer(i) - 1);
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        if (!haveExecute(targetI))
                            queue.set(targetI , queue.get(targetI) + " " + clock);
                    }
                }
                if (addReservationStation.getTimer(i) == 0){
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        queue.set(targetI , queue.get(targetI) + "..." + clock);
                    }
                }
                if (addReservationStation.getTimer(i) == -1){
                    writeBackQueue.add("A" + (i+1));

                    float res =  executeInstruction(addReservationStation.getVJ(i) , addReservationStation.getVK(i) , addReservationStation.getOP(i), -1 , (int) addReservationStation.getA(i));
                    writeBackQueueValues.add(res); // result el operation dy
                    if (addReservationStation.getOP(i).toLowerCase().equals("bnez"))
                        isBranch = false;
                    //System.out.println(res + " <<<<<");
                }
            }
        }


        for (int i = 0 ; i < storeBufferSize ; i++){
            String currentStation = "S" + (i+1);
            if (Issued.equals(currentStation)) {
                Issued = "";
                continue;
            }
            if (storeBuffer.getBusy(i) == 1){
                if (storeBuffer.getQ(i).equals("0")) {
                    storeBuffer.setTimer(i,storeBuffer.getTimer(i) - 1);
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        if (!haveExecute(targetI))
                            queue.set(targetI , queue.get(targetI) + " " + clock);
                    }
                }
                if (storeBuffer.getTimer(i) == 0){
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        queue.set(targetI , queue.get(targetI) + "..." + clock);
                    }
                }
                if (storeBuffer.getTimer(i) == -1){
                    writeBackQueue.add("S" + (i+1));
                    float res = -1;
                    writeBackQueueValues.add(res); // result el operation dy
                    int address = storeBuffer.getA(i);
                    cache[address] = storeBuffer.getV(i);
                    System.out.println(cache[address] + "<<<<<<<");
                    //System.out.println(res + " <<<<<");
                }
            }
        }

        for (int i = 0 ; i < loadBufferSize ; i++){
            String currentStation = "L" + (i+1);
            if (Issued.equals(currentStation)) {
                Issued = "";
                continue;
            }
            if (loadBuffer.getBusy(i) == 1){
                    loadBuffer.setTimer(i, loadBuffer.getTimer(i) - 1);
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        if (!haveExecute(targetI))
                            queue.set(targetI , queue.get(targetI) + " " + clock);
                    }
                if (loadBuffer.getTimer(i) == 0){
                    if (targetI != -1){
                        queue.set(targetI , queue.get(targetI) + "..." + clock);
                    }
                }
                if (loadBuffer.getTimer(i) == -1){
                    writeBackQueue.add("L" + (i+1));
                    float res =  executeInstruction(-1,-1,"l.d", loadBuffer.getA(i) , -1);
                    //System.out.println(res + " <<<<<<< lod value");
                    writeBackQueueValues.add(res); // result el operation dy
                    //System.out.println(res + " <<<<<");

                }
            }
        }

        for (int i = 0 ; i < mulReservationStationSize ; i++){
            String currentStation = "M" + (i+1);
            if (Issued.equals(currentStation)) {
                Issued = "";
                continue;
            }
            if (mulReservationStation.getBusy(i) == 1){
                if (mulReservationStation.getQJ(i).equals("0") && mulReservationStation.getQK(i).equals("0")) {
                    mulReservationStation.setTimer(i, mulReservationStation.getTimer(i) - 1);
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        if (!haveExecute(targetI))
                            queue.set(targetI , queue.get(targetI) + " " + clock);
                    }
                }
                if (mulReservationStation.getTimer(i) == 0){
                    int targetI = -1;
                    for (int j = 0 ; j < queue.size() ; j++){
                        if (queueStations.get(j).equals(currentStation)) {
                            targetI = j;
                            break;
                        }
                    }
                    if (targetI != -1){
                        queue.set(targetI , queue.get(targetI) + "..." + clock);
                    }
                }
                if (mulReservationStation.getTimer(i) == -1){
                    writeBackQueue.add("M" + (i+1));
                    float res =  executeInstruction(mulReservationStation.getVJ(i) , mulReservationStation.getVK(i) , mulReservationStation.getOP(i), -1 , -1);
                    writeBackQueueValues.add(res); // result el operation dy
                    //System.out.println(res + " <<<<<");

                }
            }
        }

    }

    public void printLoadBuffer(){
        System.out.println("Buffer    BUSY     A");
        for (int i = 0 ; i < loadBufferSize ; i++){
            System.out.print("L" + (i+1) + "          ");
            System.out.print(loadBuffer.getBusy(i) + "       " + loadBuffer.getA(i));
            System.out.println();
        }

    }


    public void writeBack(){
        if (writeBackQueue.isEmpty())
            return;


        // wbq = [S1,L1,A1]
        int minIndexQueue = writeBackQueue.size() + queueStations.size();
        int minIndexWb = 0;
        // [M1 , A1]
        if (writeBackQueue.size() > 1) {
            for (int i = 0 ; i < queueStations.size() ; i++)
                System.out.print(queueStations.get(i) + "  >>>> ");
            for (int i = 0; i < writeBackQueue.size(); i++) {
                for (int j = 0; j < queueStations.size(); j++) {
                    if (queueStations.get(j).toLowerCase().equals(writeBackQueue.get(i).toLowerCase())) {
                        if (j < minIndexQueue) {
                            minIndexWb = i;
                            minIndexQueue = j;
                            break;
                        }
                    }
                }
            }
        }

        String toBePublished = writeBackQueue.remove(minIndexWb);
        float value = writeBackQueueValues.remove(minIndexWb);

        int queueIndex = -1;
        for (int j = 0 ; j < queueStations.size() ; j++){
            if (queueStations.get(j).equals(toBePublished)){
                queueIndex = j;
                break;
            }
        }
        if (queueIndex != -1){
            queue.set(queueIndex , queue.get(queueIndex) + " " + clock);
        }

        for (int i = 0 ; i < queueStations.size() ; i++){
            if (queueStations.get(i).equals(toBePublished))
                queueStations.set(i, "");
        }


        for (int i = 0 ; i < addReservationStationSize ; i++){
            String currentStation = "A" + (i+1);
            if (currentStation.equals(toBePublished)) {
                addReservationStation.setBusy(i , 0);
                addReservationStation.setOP(i , "");
                addReservationStation.setVJ(i , 0);
                addReservationStation.setVK(i ,0);
                addReservationStation.setQJ(i , "");
                addReservationStation.setQK(i , "");
                addReservationStation.setA(i , 0);

                continue;
            }
            System.out.println(addReservationStation.getQJ(i));
            if (addReservationStation.getQJ(i) != null && addReservationStation.getQJ(i).toLowerCase().equals(toBePublished.toLowerCase())){
                addReservationStation.setQJ(i,"0");
                addReservationStation.setVJ(i, value);
            }
            if (addReservationStation.getQK(i) != null && addReservationStation.getQK(i).toLowerCase().equals(toBePublished.toLowerCase())){
                addReservationStation.setQK(i,"0");
                addReservationStation.setVK(i, value);
            }
        }

        for (int i = 0 ; i < storeBufferSize ; i++){
            String currentStation = "S" + (i+1);
            if (currentStation.equals(toBePublished)) {
                storeBuffer.setBusy(i , 0);
                storeBuffer.setQ(i , "");
                storeBuffer.setA(i , 0);

                continue;
            }
            //System.out.println(addReservationStation.getQJ(i));
            if (storeBuffer.getQ(i) != null && storeBuffer.getQ(i).toLowerCase().equals(toBePublished.toLowerCase())){
                storeBuffer.setQ(i,"0");
                storeBuffer.setV(i , value);
            }
        }



        for (int j = 0 ; j < integerRegisters.size() ; j++){
            if (integerRegisters.get(j).getQi().equals(toBePublished)){
                System.out.println("ALOO");
                System.out.println(value);
                integerRegisters.get(j).setQi("0");
                integerRegisters.get(j).setValue(value);
                // set el value bel content bta3 el execute
                break;
            }
        }

        for (int j = 0 ; j < floatingPointRegister.size() ; j++){
            if (floatingPointRegister.get(j).getQi().equals(toBePublished)){
                floatingPointRegister.get(j).setQi("0");
                floatingPointRegister.get(j).setValue(value);
                // set el value bel content bta3 el execute
                break;
            }
        }

        for (int i = 0 ; i < loadBufferSize ; i++){
            String currentStation = "L" + (i+1);
            if (currentStation.equals(toBePublished)) {
                loadBuffer.setBusy(i,0);
                loadBuffer.setA(i , 0);

                //continue;
                break;
            }

        }
        for (int i = 0 ; i < mulReservationStationSize ; i++){
            String currentStation = "M" + (i+1);
            if (currentStation.equals(toBePublished)) {
                mulReservationStation.setBusy(i , 0);
                mulReservationStation.setOP(i , "");
                mulReservationStation.setVJ(i , 0);
                mulReservationStation.setVK(i ,0);
                mulReservationStation.setQJ(i , "");
                mulReservationStation.setQK(i , "");
                mulReservationStation.setA(i , 0);

                continue;
            }
            System.out.println(mulReservationStation.getQJ(i));
            if (mulReservationStation.getQJ(i) != null && mulReservationStation.getQJ(i).toLowerCase().equals(toBePublished.toLowerCase())){
                mulReservationStation.setQJ(i,"0");
                mulReservationStation.setVJ(i, value);
            }
            if (mulReservationStation.getQK(i) != null && mulReservationStation.getQK(i).toLowerCase().equals(toBePublished.toLowerCase())){
                mulReservationStation.setQK(i,"0");
                mulReservationStation.setVK(i, value);
            }
        }
    }


    /*
    * while true
    *   issue
    *   execute
    *   writeBack
    * */

    public void initializeInstructions() throws IOException {
        BufferedReader br = null;
        String itemsPath = "instructions.txt";
        br = new BufferedReader(new FileReader(itemsPath));
        String line = br.readLine();
        while (line != null) {
            if (line.trim().equals("")) {
                line = br.readLine();
                continue;
            }
            instructions.add(line);
            line = br.readLine();
        }
        for (int i = 0 ; i < instructions.size() ; i++){
            System.out.println(instructions.get(i));
        }

    }

    public static void main(String[] args) throws Exception {
        Engine e = new Engine();
    }
}
