import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;//�摜�����ɕK�v
import java.awt.geom.*;//�摜�����ɕK�v
import java.applet.*;	//wav�t�@�C���̍Đ��Ɏg�p

public class MyClient extends JFrame implements MouseListener,MouseMotionListener { 
	private JButton buttonArray[][];//�{�^���p�̔z��
    private JButton buttonchange, buttonreset;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon, canIcon;
    private ImageIcon myIcon, yourIcon;
    private int myColor;
    private int myTurn;
    
    private ImageIcon seticon1, seticon2;
    private AudioClip clip;
    private int bgmnum;
    
    
 
    PrintWriter out;//�o�͗p�̃��C�^�[

	public MyClient() {
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}
        String IPName = JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
        if(IPName.equals("")){
			IPName = "localhost";//���O���Ȃ��Ƃ��́C"localhost"�Ƃ���
		}

		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(600,600);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����

		//�A�C�R���̐ݒ�
		String iconName = "./img/White.jpg";
		whiteIcon = new ImageIcon(iconName);
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
        canIcon = new ImageIcon("canset.jpg");

		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
        
        
		//�{�^���̐���
		buttonArray = new JButton[8][8];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
		for(int i=0;i<8;i++){
            for(int j=0; j<8;j++){
                buttonArray[j][i] = new JButton(boardIcon);//�{�^���ɃA�C�R����ݒ肷��
                c.add(buttonArray[j][i]);//�y�C���ɓ\��t����
                buttonArray[j][i].setBounds(i*50+10,j*50+10,50,50);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
                buttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
                buttonArray[j][i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
                buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
            }
        }
        
        buttonArray[3][3].setIcon(whiteIcon);
        buttonArray[3][4].setIcon(blackIcon);
        buttonArray[4][3].setIcon(blackIcon);
        buttonArray[4][4].setIcon(whiteIcon);
        
        
        
        //���ł��p�X���ł���{�^���̐����Ɣz�u
        buttonchange = new JButton("�p�X");
        c.add(buttonchange);
        buttonchange.setBounds(430,10,140,50);
        buttonchange.addMouseListener(this);
        
        //���ł����Z�b�g�ł���{�^���̐����Ɣz�u
        buttonreset = new JButton("���Z�b�g");
        c.add(buttonreset);
        buttonreset.setBounds(430,65,140,50);
        buttonreset.addMouseListener(this);
        
		
		//�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
			//"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket(IPName, 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			 System.err.println("�G���[���������܂���: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}
		
	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
                
                int count = 0;
                
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�
                String myNumberStr = br.readLine();
                int myNumberInt = Integer.parseInt(myNumberStr);
                
                JLabel blackHPcount = new JLabel("0");
                JLabel whiteHPcount = new JLabel("0");
                
                if (myNumberInt % 2 == 0){
                    myColor = 0;//���̃R�}
                    System.out.println("��");
                }else{
                    myColor = 1;//���̃R�}
                    System.out.println("��");
                }
                
                if (myColor == 0){
                    myIcon = blackIcon;
                    yourIcon = whiteIcon;
                    myTurn = 0;
                    
                    seticon1 = new ImageIcon("Black.jpg");
                    JButton setblackIcon = new JButton(seticon1);//�{�^�����쐬�C�摜��ݒ肷��
                    c.add(setblackIcon);//�y�C���ɓ\��t����
                    setblackIcon.setBounds(10,420,50,50);
					
                    seticon2 = new ImageIcon("White.jpg");
                    JButton whileIconset = new JButton(seticon2);//�{�^�����쐬�C�摜��ݒ肷��
                    c.add(whileIconset);//�y�C���ɓ\��t����
                    whileIconset.setBounds(10,480,50,50);
                    
                    
                    buttonArray[2][3].setIcon(canIcon);
                    buttonArray[3][2].setIcon(canIcon);
                    buttonArray[4][5].setIcon(canIcon);
                    buttonArray[5][4].setIcon(canIcon);
                    
                    
                }else{
                    myIcon = whiteIcon;
                    yourIcon = blackIcon;
                    myTurn = 1;
                    
                    seticon1 = new ImageIcon("White.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
                    JButton setblackIcon = new JButton(seticon1);//�{�^�����쐬�C�摜��ݒ肷��
                    c.add(setblackIcon);//�y�C���ɓ\��t����
                    setblackIcon.setBounds(10,420,50,50);
                    
                    seticon2 = new ImageIcon("Black.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
                    JButton whileIconset = new JButton(seticon2);//�{�^�����쐬�C�摜��ݒ肷��
                    c.add(whileIconset);//�y�C���ɓ\��t����
                    whileIconset.setBounds(10,480,50,50);
                }
                
                
                //���x���̐���
                JLabel theLabel1 = new JLabel("�����̃R�}�� : ");
                c.add(theLabel1);
                theLabel1.setBounds(70,420,140,50);
                theLabel1.setForeground(Color.BLACK);
                
                JLabel theLabel2 = new JLabel("����̃R�}�� : ");
                c.add(theLabel2);
                theLabel2.setBounds(70,480,140,50);
                theLabel2.setForeground(Color.BLACK);
                
                JLabel theLabel3 = new JLabel("2");
                c.add(theLabel3);
                theLabel3.setBounds(175,420,50,50);
                theLabel3.setForeground(Color.BLACK);

                JLabel theLabel4 = new JLabel("2");
                c.add(theLabel4);
                theLabel4.setBounds(175,480,50,50);
                theLabel4.setForeground(Color.BLACK);
                
				while(true) {
                    
                    //�^�[�����؂�ւ�邲�Ƃɕϐ��̏��������s���B
                    int blackIconNum = 0;
                    int whiteIconNum = 0;
                    int number0 = 0;
                    int countTurn = 0;
                    
                    
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						//System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						if(cmd.equals("MOVE")){//cmd�̕�����"MOVE"�����������ׂ�D��������true�ƂȂ�
							//MOVE�̎��̏���(�R�}�̈ړ��̏���)
                            //System.out.println("MOVE��M�����I�I�I�I");
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int x = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							int y = Integer.parseInt(inputTokens[3]);//���l�ɕϊ�����
							buttonArray[theBnum][theBnum].setLocation(x,y);//�w��̃{�^�����ʒu��x,y�ɐݒ肷��
						}
                        
						if(cmd.equals("PLACE")){//cmd�̕�����"PLACE"�����������ׂ�D��������true�ƂȂ�
							//PLACE�̎��̏���(�R�}�̐F�̏���)
                            System.out.println("PLACE��M�����I�I�I�I");
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int theColor = Integer.parseInt(inputTokens[2]);
							int x = theBnum % 8;//x���W
							int y = theBnum / 8;//y���W
                            
                            bgmnum = 0;

                            if (theColor == myColor){
                                //���M���N���C�A���g�ł̏���
                                buttonArray[y][x].setIcon(myIcon);
                                SoundTestWav(bgmnum);
                                
                            }else{
                                //��M���N���C�A���g�ł̏���
                                buttonArray[y][x].setIcon(yourIcon);
                                SoundTestWav(bgmnum);
                                
                            }
                            
                            if(countTurn == 0){
                                for(int i = 0; i < 8; i++){
                                    for(int j = 0; j < 8; j++){
                                        if(buttonArray[j][i].getIcon() == blackIcon){
                                            blackIconNum++;
                                        }else if(buttonArray[j][i].getIcon() == whiteIcon){
                                            whiteIconNum++;
                                        }else if((buttonArray[j][i].getIcon() == boardIcon) || (buttonArray[j][i].getIcon() == canIcon)){
                                            if(buttonArray[j][i].getIcon() == canIcon){
                                                buttonArray[j][i].setIcon(boardIcon);
                                            }
                                            if(judgeButton(j,i,number0,myTurn)){
                                                countTurn++;//���̃^�[���ɑ��肪�R�}��u������
                                            }
                                        }
                                    }
                                }
                            }
                            count++;
                            
                            System.out.println(countTurn);
                            
                            if(countTurn != 0){
                                myTurn = 1 - myTurn;//�^�[�������ւ���
                                count = 0;//�X�L�b�v�{�^���̏�����
                            }
                            
                            String blackIconstr = Integer.toString(blackIconNum);
                            String whiteIconstr = Integer.toString(whiteIconNum);
							//�R�}�̐���\��
                            if(myIcon == blackIcon){
                                theLabel3.setText(blackIconstr);
                                theLabel4.setText(whiteIconstr);
                            }else{
                                theLabel3.setText(whiteIconstr);
                                theLabel4.setText(blackIconstr);
                            }
                            
                            
                            if((count >= 2) || (blackIconNum + whiteIconNum == 64) || (blackIconNum * whiteIconNum == 0)){
                                judgeGame();
                            }
                            
                            
                            //�������̃^�[���̎��A������ꏊ��\������
                            if(myTurn == 0){//�������̃^�[���̎��A������ꏊ��\������
                                nextset();
                            }
                            
						}
                        
						if(cmd.equals("FLIP")){//cmd�̕�����"FLIP"�����������ׂ�D��������true�ƂȂ�
							//FLIP�̎��̏���(�R�}���Ђ�����Ԃ�����)
                            System.out.println("FLIP��M�����I�I�I�I");
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int theColor = Integer.parseInt(inputTokens[2]);
							int x = theBnum % 8;//x���W
							int y = theBnum / 8;//y���W
                            
                            if(theColor == myColor){
                                buttonArray[y][x].setIcon(myIcon);
                            }else{
                                buttonArray[y][x].setIcon(yourIcon);
                            }
                            
						}
                        
                        
                        if(cmd.equals("CHANGETURN")){
                            bgmnum = 1;
                            SoundTestWav(bgmnum);
                            for(int j = 0; j < 8; j++){
                                for(int i = 0; i < 8; i++){
                                    if((buttonArray[j][i].getIcon() == canIcon)){
                                        buttonArray[j][i].setIcon(boardIcon);
                                    }
                                }
                            }
                            if(myTurn == 0){
                                myTurn = 1;
                            }else{
                                myTurn = 0;
                                nextset();
                            }
                            count++;
                            if(count == 2){
                                judgeGame();
                            }
                        }
                        
                        
                        if(cmd.equals("REPLAY")){
                            blackIconNum = 0;
                            whiteIconNum = 0;
                            number0 = 0;
                            countTurn = 0;
                            count = 0;
                            
                            theLabel3.setText("2");
                            theLabel4.setText("2");
                            whiteHPcount.setText("0");
                            blackHPcount.setText("0");
                            
                            for(int i=0;i<8;i++){
                                for(int j=0; j<8;j++){
                                    buttonArray[j][i].setIcon(boardIcon);
                                }
                            }
                            
                            buttonArray[3][3].setIcon(whiteIcon);
                            buttonArray[3][4].setIcon(blackIcon);
                            buttonArray[4][3].setIcon(blackIcon);
                            buttonArray[4][4].setIcon(whiteIcon);
                            
                            if(myTurn == 0){
                                if(myColor == 0){
                                    buttonArray[2][3].setIcon(canIcon);
                                    buttonArray[3][2].setIcon(canIcon);
                                    buttonArray[4][5].setIcon(canIcon);
                                    buttonArray[5][4].setIcon(canIcon);
                                }else{
                                    buttonArray[2][4].setIcon(canIcon);
                                    buttonArray[3][5].setIcon(canIcon);
                                    buttonArray[4][2].setIcon(canIcon);
                                    buttonArray[5][3].setIcon(canIcon);
                                }
                            }
                            
                        }
                        
                        
					}else{
						break;
					}
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}
    
    
    
    
	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
    
    
	//���ɂ�����R�}�̏ꏊ��\��
    public void nextset(){
        int num = 2;
        int turn = 0;
        for(int j = 1; j < 8; j++){
            for(int i = 1; i < 8; i++){
                judgeButton(j,i,num,turn);
            }
        }
    }
    
	//�^�[����ς���
    public void changeTurn(){
        System.out.println("CHANGETURN");
        String msg = "CHANGETURN";
        out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
        out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
    }
    
    //�R�}���u���邩�ǂ����̔��������.number�Łu�z�u�ł��邩�̊m�F�v�Ɓu�����p�X�v�Ɓu���ɔz�u�ł���ꏊ�̕\���v��؂�ւ��Ă���
    public boolean judgeButton(int y, int x, int number, int myTurn){
        boolean flag = false;
        
        for(int j=-1;j<2;j++){
            for(int i=-1;i<2;i++){
                if(!((i == 0) && (j == 0))){
                
                    if((number == 1) || (number == 2)){
                        int flipNum = flipButtons(y,x,j,i);
                        if(flipNum >= 1){
                            flag = true;
                            for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
                                //�{�^���̈ʒu�������
                                int msgy = y + dy;
                                int msgx = x + dx;
                                int theArrayIndex = msgy*8 + msgx;
                                if(number == 1){
                                    //�T�[�o�ɏ��𑗂�
                                    String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
                                    out.println(msg);
                                    out.flush();
                                }else if(number == 2){
                                    //���u����ꏊ�ɍ��ە\��
                                    if(buttonArray[y][x].getIcon() == boardIcon){
                                        buttonArray[y][x].setIcon(canIcon);
                                        //System.out.println("���u������W�́ix,y �j = ( " + x + " , " + y + " �j");
                                    }
                                }
                                
                            }
                        }
                    }else if(number == 0){//number0�̂Ƃ�
                        if(myTurn == 0){
                            int flipNum2 = flipButtons2(y,x,j,i);
                            if(flipNum2 >= 1){
                                flag = true;
                            }
                        }else{
                            int flipNum2 = flipButtons(y,x,j,i);
                            if(flipNum2 >= 1){
                                flag = true;
                            }
                        }
                    }
                }
            }
        }
        return flag;
    }
	
    //�Ђ�����Ԃ��R�}�̐����J�E���g����
    public int flipButtons(int y, int x, int j, int i){
        int flipNum = 0;
        for(int dy=j, dx=i; ; dy+=j, dx+=i){
            if((y+dy < 0) || (y+dy > 7) || (x+dx < 0) || (x+dx > 7)){//��O�Ȃ炷��flipNum��Ԃ�
                return flipNum = 0;
            }
            if((buttonArray[y+dy][x+dx].getIcon() == boardIcon) || (buttonArray[y+dy][x+dx].getIcon() == canIcon)){//boardIcon�������flipNum��������
                return flipNum = 0;
            }else if(buttonArray[y+dy][x+dx].getIcon() == myIcon){//myIcon�Ȃ�flipNum��Ԃ�
                return flipNum;
            }else if(buttonArray[y+dy][x+dx].getIcon() == yourIcon){//yourIcon�Ȃ�flipNum��1��������
                flipNum++;
            }
        }
    }
    
	
	//�����p�X
    public int flipButtons2(int y, int x, int j, int i){
        int flipNum = 0;
        for(int dy=j, dx=i; ; dy+=j, dx+=i){
            if((y+dy < 0) || (y+dy > 7) || (x+dx < 0) || (x+dx > 7)){
                return flipNum = 0;
            }
            if(buttonArray[y+dy][x+dx].getIcon() == boardIcon){
                return flipNum = 0;
            }else if(buttonArray[y+dy][x+dx].getIcon() == yourIcon){
                return flipNum;
            }else if(buttonArray[y+dy][x+dx].getIcon() == myIcon){
                flipNum++;
            }
        }
    }
	
	
	//�I������
    public void judgeGame(){
        int boardIconNum = 0;
        int myIconNum = 0;
        int yourIconNum = 0;
        String message;
        for(int i=0;i<8;i++){
            for(int j=0; j<8;j++){
                if(buttonArray[j][i].getIcon() == boardIcon){
                    boardIconNum++;
                }else if(buttonArray[j][i].getIcon() == myIcon){
                    myIconNum++;
                }else if(buttonArray[j][i].getIcon() == yourIcon){
                    yourIconNum++;
                }
            }
        }
        if(myIconNum > yourIconNum){
            bgmnum = 3;
            SoundTestWav(bgmnum);
            message = "���Ȃ��̏����I";
            WinDialogWindow dlg = new WinDialogWindow(this);
            setVisible(true);
        }else if (myIconNum < yourIconNum){
            bgmnum = 4;
            SoundTestWav(bgmnum);
            message = "���Ȃ��̕����I";
            LoseDialogWindow dlg = new LoseDialogWindow(this);
            setVisible(true);
        }else{
            message = "�Ё@���@��@��";
            DrawDialogWindow dlg = new DrawDialogWindow(this);
            setVisible(true);
        }
        System.out.println(message);
    }
	
	
	//�Q�[���̏�����
    public void resetGame(){
        bgmnum = 2;
        String msg = "REPLAY";//�T�[�o�ɏ��𑗂�
        out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
        out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
        
        SoundTestWav(bgmnum);
        
    }
	
	
	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
		int number = 1;
		
		if (myTurn == 0){
            System.out.println("�N���b�N!");
            
            
            JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
            String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
            Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������
            //System.out.println(theIcon);//�f�o�b�O�i�m�F�p�j�ɁC�N���b�N�����A�C�R���̖��O���o�͂���
            
            
            //�N���b�N�����A�C�R����boardIcon��canIcon�̂Ƃ�msg�𑗐M�ł���
            if((theIcon == boardIcon) || (theIcon == canIcon)){
                int temp = Integer.parseInt(theArrayIndex);//theArrayIndex��ϐ�temp�ɕϊ�
                int x = temp % 8;
                int y = temp /8;
                if(judgeButton(y,x,number,myTurn)){
                    //�R�}��u����
                    String msg = "PLACE"+" "+theArrayIndex+" "+myColor;//�T�[�o�ɏ��𑗂�
                    out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
                    out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
                }else{
                    //�R�}��u���Ȃ�
                    System.out.println("�����ɂ͔z�u�ł��܂���!");
                }
            }
            
            if(theButton == buttonchange){
                changeTurn();
            }
            
            if(theButton == buttonreset){
                resetGame();
            }
        }
        repaint();//�I�u�W�F�N�g�̍ĕ`����s��
	}
    
	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
		//System.out.println("�}�E�X��������");
	}
	
	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
		//System.out.println("�}�E�X�E�o");
	}
	
	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
		//System.out.println("�}�E�X��������");
	}
	
	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
		//System.out.println("�}�E�X�������");
	}
	
	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
    /*
		//System.out.println("�}�E�X���h���b�O");
		JButton theButton = (JButton)e.getComponent();//�^���Ⴄ�̂ŃL���X�g����
		String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
        
        if (theArrayIndex.charAt(0) == '0'){
            Point theMLoc = e.getPoint();//�������R���|�[�l���g����Ƃ��鑊�΍��W
            //System.out.println(theMLoc);//�f�o�b�O�i�m�F�p�j�ɁC�擾�����}�E�X�̈ʒu���R���\�[���ɏo�͂���
            Point theBtnLocation = theButton.getLocation();//�N���b�N�����{�^�������W���擾����
            theBtnLocation.x += theMLoc.x-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
            theBtnLocation.y += theMLoc.y-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
            theButton.setLocation(theBtnLocation);//�}�E�X�̈ʒu�ɂ��킹�ăI�u�W�F�N�g���ړ�����
 
            //���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
            String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

            //�T�[�o�ɏ��𑗂�
            out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
            out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

            repaint();//�I�u�W�F�N�g�̍ĕ`����s��
        }
    */
        
	}

	public void mouseMoved(MouseEvent e) {//�}�E�X���I�u�W�F�N�g��ňړ������Ƃ��̏���
    /*
		//System.out.println("�}�E�X�ړ�");
		int theMLocX = e.getX();//�}�E�X��x���W�𓾂�
		int theMLocY = e.getY();//�}�E�X��y���W�𓾂�
		//System.out.println(theMLocX+","+theMLocY);//�R���\�[���ɏo�͂���
    */
	}
	
	
	//bgm�̊Ǘ�
    public void SoundTestWav(int bgmnum){
        if(bgmnum == 0){
            //�����̓ǂݍ���
            clip = Applet.newAudioClip(getClass().getResource("place.wav"));
            clip.play();
        }else if(bgmnum == 1){
            clip = Applet.newAudioClip(getClass().getResource("skip.wav"));
            clip.play();
        }else if(bgmnum == 2){
            clip = Applet.newAudioClip(getClass().getResource("reset.wav"));
            clip.play();
        }else if(bgmnum == 3){
            clip = Applet.newAudioClip(getClass().getResource("win.wav"));
            clip.play();
        }else if(bgmnum == 4){
            clip = Applet.newAudioClip(getClass().getResource("lose.wav"));
            clip.play();
        }
    }
    
}


//�����̃_�C�A���O���o��
class WinDialogWindow extends JDialog implements ActionListener{
    WinDialogWindow(JFrame owner) {
        super(owner);//�Ăяo�����ƂƂ̐e�q�֌W�̐ݒ�D������R�����g�A�E�g����ƕʁX�̃_�C�A���O�ɂȂ�

		Container c = this.getContentPane();	//�t���[���̃y�C�����擾����
        c.setLayout(null);		//�������C�A�E�g�̐ݒ���s��Ȃ�

        JButton theButton = new JButton();//�摜��\��t���郉�x��
        ImageIcon theImage = new ImageIcon("win.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
        theButton.setIcon(theImage);//���x����ݒ�
        theButton.setBounds(0,0,526,234);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
        theButton.addActionListener(this);//�{�^�����N���b�N�����Ƃ���actionPerformed�Ŏ󂯎�邽��
        c.add(theButton);//�_�C�A���O�ɓ\��t����i�\��t���Ȃ��ƕ\������Ȃ�

        setTitle("You Win!");//�^�C�g���̐ݒ�
        setSize(526, 234);//�傫���̐ݒ�
        setResizable(false);//�g��k���֎~//true�ɂ���Ɗg��k���ł���悤�ɂȂ�
        setUndecorated(true); //�^�C�g����\�����Ȃ�
        setModal(true);//������܂ŉ���G��Ȃ�����ifalse�ɂ���ƐG���j

        //�_�C�A���O�̑傫����\���ꏊ��ύX�ł���
        //�e�̃_�C�A���O�̒��S�ɕ\���������ꍇ�́C�e�̃E�B���h�E�̒��S���W�����߂āC�q�̃_�C�A���O�̑傫���̔������炷
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialog��p������
    }
}

//�����̃_�C�A���O���o��
class LoseDialogWindow extends JDialog implements ActionListener{
    LoseDialogWindow(JFrame owner) {
        super(owner);//�Ăяo�����ƂƂ̐e�q�֌W�̐ݒ�D������R�����g�A�E�g����ƕʁX�̃_�C�A���O�ɂȂ�

		Container c = this.getContentPane();	//�t���[���̃y�C�����擾����
        c.setLayout(null);		//�������C�A�E�g�̐ݒ���s��Ȃ�

        JButton theButton = new JButton();//�摜��\��t���郉�x��
        ImageIcon theImage = new ImageIcon("lose.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
        theButton.setIcon(theImage);//���x����ݒ�
        theButton.setBounds(0,0,526,234);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
        theButton.addActionListener(this);//�{�^�����N���b�N�����Ƃ���actionPerformed�Ŏ󂯎�邽��
        c.add(theButton);//�_�C�A���O�ɓ\��t����i�\��t���Ȃ��ƕ\������Ȃ�

        setTitle("You Lose!");//�^�C�g���̐ݒ�
        setSize(526, 234);//�傫���̐ݒ�
        setResizable(false);//�g��k���֎~//true�ɂ���Ɗg��k���ł���悤�ɂȂ�
        setUndecorated(true); //�^�C�g����\�����Ȃ�
        setModal(true);//������܂ŉ���G��Ȃ�����ifalse�ɂ���ƐG���j

        //�_�C�A���O�̑傫����\���ꏊ��ύX�ł���
        //�e�̃_�C�A���O�̒��S�ɕ\���������ꍇ�́C�e�̃E�B���h�E�̒��S���W�����߂āC�q�̃_�C�A���O�̑傫���̔������炷
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialog��p������
    }
}


//�Ђ��킯�̃_�C�A���O���o��
class DrawDialogWindow extends JDialog implements ActionListener{
    DrawDialogWindow(JFrame owner) {
        super(owner);//�Ăяo�����ƂƂ̐e�q�֌W�̐ݒ�D������R�����g�A�E�g����ƕʁX�̃_�C�A���O�ɂȂ�

		Container c = this.getContentPane();	//�t���[���̃y�C�����擾����
        c.setLayout(null);		//�������C�A�E�g�̐ݒ���s��Ȃ�

        JButton theButton = new JButton();//�摜��\��t���郉�x��
        ImageIcon theImage = new ImageIcon("draw.jpg");//�Ȃɂ��摜�t�@�C�����_�E�����[�h���Ă���
        theButton.setIcon(theImage);//���x����ݒ�
        theButton.setBounds(0,0,526,234);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
        theButton.addActionListener(this);//�{�^�����N���b�N�����Ƃ���actionPerformed�Ŏ󂯎�邽��
        c.add(theButton);//�_�C�A���O�ɓ\��t����i�\��t���Ȃ��ƕ\������Ȃ�

        setTitle("!!DRAW!!");//�^�C�g���̐ݒ�
        setSize(526, 234);//�傫���̐ݒ�
        setResizable(false);//�g��k���֎~//true�ɂ���Ɗg��k���ł���悤�ɂȂ�
        setUndecorated(true); //�^�C�g����\�����Ȃ�
        setModal(true);//������܂ŉ���G��Ȃ�����ifalse�ɂ���ƐG���j

        //�_�C�A���O�̑傫����\���ꏊ��ύX�ł���
        //�e�̃_�C�A���O�̒��S�ɕ\���������ꍇ�́C�e�̃E�B���h�E�̒��S���W�����߂āC�q�̃_�C�A���O�̑傫���̔������炷
        setLocation(owner.getBounds().x+owner.getWidth()/2-this.getWidth()/2,owner.getBounds().y+owner.getHeight()/2-this.getHeight()/2);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        this.dispose();//Dialog��p������
    }
}

