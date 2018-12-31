package util;

import model.BlockModel;

import java.util.ArrayList;

import static model.BlockModel.SpicaBlock.*;

/**
 * Created by yuusuke on 2018/11/28.
 */
public class SpicaWrapper {
    private UdpSend udpSend;
    private ArrayList<BlockModel> blocks = new ArrayList<>();
    boolean flag1;
    boolean flag2;
    boolean flag3;

    public SpicaWrapper(String ip, int port) {
        udpSend = new UdpSend(ip, port);
    }

    /*
    時間はミリ秒で送る
    ただし、指定範囲は小数点1桁まで、それ以降は切り捨て
    speedは現状1~255まで
     */
    public void front(int time, int speed) {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(BlockModel.SpicaBlock.FRONT);
        blockModel.setId("0001");
        blockModel.setSpeed(speed);
        blockModel.setTime(time);
        blocks.add(blockModel);
    }

    public void back(int time, int speed) {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(BlockModel.SpicaBlock.BACK);
        blockModel.setId("0002");
        blockModel.setSpeed(speed);
        blockModel.setTime(time);
        blocks.add(blockModel);
    }

    public void left(int time, int speed) {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(BlockModel.SpicaBlock.LEFT);
        blockModel.setId("0003");
        blockModel.setSpeed(speed);
        blockModel.setTime(time);
        blocks.add(blockModel);
    }

    public void right(int time, int speed) {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(BlockModel.SpicaBlock.RIGHT);
        blockModel.setId("0004");
        blockModel.setSpeed(speed);
        blockModel.setTime(time);
        blocks.add(blockModel);
    }

    public void ifStartBlock(int value, String flag) {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(IF_START);
        blockModel.setId("0007");
        blockModel.setValue(value);
        if (flag.equals("up")) {
            blockModel.setIfGreaterOrLess(1);
        } else {
            blockModel.setIfGreaterOrLess(0);
        }
        blocks.add(blockModel);
    }

    public void elseBlock() {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(BlockModel.SpicaBlock.ELSE);
        blocks.add(blockModel);
    }

    public void ifEndBlock() {
        BlockModel blockModel = new BlockModel();
        blockModel.setBlock(IF_END);
        blockModel.setId("0008");
        blocks.add(blockModel);
    }

    // TODO Block配列の検査機構を作る(if用)


    public void run() {
        String sendTexts = generateUdpSendTexts();
        if (sendTexts.equals("error")) {
            System.out.println("コンパイルエラー");
            return;
        }
        udpSend.UdpSendText(sendTexts);
    }

    /*
    ifの部分だけID値が異なる
    ほかは一緒
    ifは三桁目の値を変える
    trueなら0100系
    falseなら0200系
    forを展開後、ifstartEndでsplit
    forの展開をする必要はない,そのまま送ればできるから、ifだけ検査して、IDをうまくやる
     */


    private boolean ifStatementCheck() {
        flag1 = false;
        flag2 = false;
        flag3 = false;

        for (int i = 0; i < blocks.size(); i++) {
            BlockModel block = blocks.get(i);
            if (block.getBlock() == IF_START) {
                flag1 = true;

            } else if (block.getBlock() == ELSE && flag1) {
                flag2 = true;


            } else if (block.getBlock() == BlockModel.SpicaBlock.IF_END && flag1 && flag2) {
                flag3 = true;
            }
        }
        if (flag1 && flag2 && flag3) {
            return true;
        } else if (flag2 || flag3) {
            return false;
        } else {
            return true;
        }

    }


    int generateFlag;

    private String generateUdpSendTexts() {
        if (!ifStatementCheck()) {
            return "error";
        }
        ifStateCreate();
        String sendStringData = "";
        for (BlockModel script : blocks) {
            String ifState = String.format("%02d", script.getIfState());
            String blockId = String.format("%02d", script.getBlock().getId());

            //速度値は左右同じものを使う
            String leftSpeed = String.format("%03d", script.getSpeed());
            if (script.getBlock() == IF_END || script.getBlock() == FOR_START || script.getBlock() == FOR_END) {
                leftSpeed = String.format("%03d", 0);
            }

            String rightSpeed = String.format("%03d", script.getSpeed());
            if (script.getBlock() == IF_START || script.getBlock() == IF_END || script.getBlock() == FOR_START || script.getBlock() == FOR_END) {
                rightSpeed = String.format("%03d", 0);
            }

            String value = String.format("%03d", (int) Math.round(script.getValue()));
            if (script.getBlock() == IF_START || script.getBlock() == FOR_START) {

                value = String.format("%03d", (int) Math.round(script.getValue()));
            } else if (script.getBlock() == IF_END || script.getBlock() == FOR_END) {
                value = String.format("%03d", 0);
            }
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
            System.out.println(String.valueOf(ifState) + blockId + String.valueOf(leftSpeed) + String.valueOf(rightSpeed) + String.valueOf(value));

            sendStringData = sendStringData + String.valueOf(ifState) + blockId + String.valueOf(leftSpeed) + String.valueOf(rightSpeed) + String.valueOf(value);
        }
        return sendStringData;


    }

    private void ifStateCreate() {
        for (int i = 0; i < blocks.size(); i++) {
            BlockModel block = blocks.get(i);
            switch (block.getBlock()) {
                case FRONT:
                case BACK:
                case LEFT:
                case RIGHT:
                case FOR_START:
                case FOR_END:
                    if (generateFlag == 0) {

                    } else if (generateFlag == 1) {
                        block.setIfState(1);
//                        String tmpIdStr = block.getId();
//                        int tmpIdInt = Integer.valueOf(tmpIdStr);
//                        tmpIdInt += 100;
//                        tmpIdStr = "0" + tmpIdInt;
//                        block.setId(tmpIdStr);

                    } else if (generateFlag == 2) {
//                        String tmpIdStr = block.getId();
//                        int tmpIdInt = Integer.valueOf(tmpIdStr);
//                        tmpIdInt += 200;
//                        tmpIdStr = "0" + tmpIdInt;
//                        block.setId(tmpIdStr);
                        block.setIfState(2);
                    }
                    break;

                case IF_START:
                    generateFlag = 1;
                    break;
                case IF_END:
                    generateFlag = 0;
                    break;
                case ELSE:
                    generateFlag = 2;
                    break;

            }
        }
    }


}
